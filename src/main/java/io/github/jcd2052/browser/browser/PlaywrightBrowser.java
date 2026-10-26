package io.github.jcd2052.browser.browser;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Geolocation;
import io.github.jcd2052.browser.browser.interfaces.IBrowser;
import io.github.jcd2052.browser.browser.interfaces.IBrowserTab;
import io.github.jcd2052.browser.browser.interfaces.IBrowserWindow;
import io.github.jcd2052.browser.configuration.IBrowserProperties;
import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete Playwright implementation of the {@link IBrowser} interface.
 * <p>
 * Wraps one running browser process: the {@link Playwright} driver and the {@link Browser} it launched.
 * Launching is done by whoever creates this object (normally the browser service, through the browser
 * factory); this class manages the browser's windows and closes the whole instance.
 * <p>
 * Windows are always read from Playwright ({@link Browser#contexts()}); the only state kept here is which
 * window is current. Each window keeps track of its own current tab.
 */
public class PlaywrightBrowser implements IBrowser {
    /**
     * The Playwright driver process that owns the browser.
     */
    private final Playwright playwright;
    /**
     * The launched browser.
     */
    @Getter
    private final Browser browser;
    private final IBrowserProperties browserProperties;
    /**
     * The current window, or {@code null} if no window is open.
     */
    private IBrowserWindow currentBrowserWindow;
    /**
     * Whether {@link #close()} was called. Needed because Playwright has no "is closed" check for a
     * {@link Playwright} instance, and {@link Browser#isConnected()} keeps returning {@code true} after
     * {@link Playwright#close()}. It also makes a second {@link #close()} a no-op.
     */
    private boolean isClosed;

    /**
     * Wraps an already launched browser.
     *
     * @param playwright        The Playwright driver process that owns the browser.
     * @param browser           The launched Playwright {@link Browser}.
     * @param browserProperties The configuration applied to every new window (viewport, timeouts, ...).
     */
    public PlaywrightBrowser(Playwright playwright, Browser browser, IBrowserProperties browserProperties) {
        this.playwright = playwright;
        this.browser = browser;
        this.browserProperties = browserProperties;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The order of the windows is not defined. The current window is returned as the same instance as
     * {@link #getCurrentBrowserWindow()}, so it keeps its current tab.
     */
    @Override
    public List<IBrowserWindow> getBrowserWindows() {
        return browser.contexts().stream().map(this::wrap).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the current window has been closed, another open window (if any) becomes current.
     */
    @Override
    public IBrowserWindow getCurrentBrowserWindow() {
        if (currentBrowserWindow != null && !browser.contexts().contains(currentBrowserWindow.getBrowserContext())) {
            List<BrowserContext> openContexts = browser.contexts();
            currentBrowserWindow = openContexts.isEmpty() ? null : new BrowserWindow(openContexts.get(0));
        }
        return currentBrowserWindow;
    }

    @Override
    public IBrowserWindow openNewWindow() {
        return openNewWindow(null);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The new window opens with one tab. A {@code null} path uses the configured storage state, exactly
     * like {@link #openNewWindow()}.
     */
    @Override
    public IBrowserWindow openNewWindow(Path storageStatePath) {
        BrowserContext context = browser.newContext(buildContextOptions(storageStatePath));
        context.setDefaultTimeout(browserProperties.getTimeout());
        context.setDefaultNavigationTimeout(browserProperties.getPageLoadTimeout());

        IBrowserWindow window = new BrowserWindow(context);
        window.openNewTab();
        currentBrowserWindow = window;
        return window;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The window's current tab is brought to the front.
     */
    @Override
    public void switchToWindow(IBrowserWindow window) {
        IBrowserTab tab = window.getCurrentBrowserTab();
        if (tab != null) {
            window.switchToTab(tab);
        }
        currentBrowserWindow = window;
    }

    @Override
    public void closeWindow(IBrowserWindow window) {
        window.closeWindow();
    }

    /**
     * {@inheritDoc}
     * <p>
     * {@link Playwright#close()} stops the driver process and with it every browser it launched, whatever
     * windows are still open. Calling this more than once has no effect.
     */
    @Override
    public void close() {
        if (isClosed) {
            return;
        }
        isClosed = true;
        currentBrowserWindow = null;
        playwright.close();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Also reports {@code true} when the browser has disconnected on its own (for example it crashed or
     * was killed), so callers replace it instead of reusing a dead instance.
     */
    @Override
    public boolean isClosed() {
        return isClosed || !browser.isConnected();
    }

    /**
     * Wraps a context as a window, reusing the current window's instance so it keeps its current tab.
     */
    private IBrowserWindow wrap(BrowserContext context) {
        IBrowserWindow current = currentBrowserWindow;
        return current != null && current.getBrowserContext() == context ? current : new BrowserWindow(context);
    }

    /**
     * Translates the configured properties into Playwright's context options. Every setting beyond the
     * viewport is optional: unset (null/blank) properties are omitted so Playwright uses its defaults.
     */
    private Browser.NewContextOptions buildContextOptions(Path storageStatePath) {
        Browser.NewContextOptions options = new Browser.NewContextOptions()
                .setViewportSize(browserProperties.getWidth(), browserProperties.getHeight());

        if (isNotBlank(browserProperties.getLocale())) {
            options.setLocale(browserProperties.getLocale());
        }
        if (isNotBlank(browserProperties.getTimezoneId())) {
            options.setTimezoneId(browserProperties.getTimezoneId());
        }
        if (browserProperties.getGeolocationLatitude() != null && browserProperties.getGeolocationLongitude() != null) {
            options.setGeolocation(new Geolocation(
                    browserProperties.getGeolocationLatitude(),
                    browserProperties.getGeolocationLongitude()));
        }
        List<String> permissions = browserProperties.getPermissions();
        if (permissions != null && !permissions.isEmpty()) {
            options.setPermissions(permissions);
        }
        if (isNotBlank(browserProperties.getUserAgent())) {
            options.setUserAgent(browserProperties.getUserAgent());
        }
        if (browserProperties.getDeviceScaleFactor() != null) {
            options.setDeviceScaleFactor(browserProperties.getDeviceScaleFactor());
        }
        if (browserProperties.isMobile()) {
            options.setIsMobile(true);
        }
        if (browserProperties.isHasTouch()) {
            options.setHasTouch(true);
        }
        if (storageStatePath != null) {
            options.setStorageStatePath(storageStatePath);
        } else if (isNotBlank(browserProperties.getStorageStatePath())) {
            options.setStorageStatePath(Paths.get(browserProperties.getStorageStatePath()));
        }
        return options;
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
