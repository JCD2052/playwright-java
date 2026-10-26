package io.github.jcd2052.browser.factory;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import io.github.jcd2052.browser.configuration.IBrowserProperties;
import io.github.jcd2052.browser.launcher.IBrowserLauncher;
import io.github.jcd2052.browser.launcher.IBrowserLauncherRegistry;

/**
 * Default {@link IBrowserFactory}: launches the browser named by {@link IBrowserProperties#getName()} through the
 * {@link IBrowserLauncher} registered for that name in the given {@link IBrowserLauncherRegistry}.
 * <p>
 * The properties are translated into Playwright's launch options: headless mode, launch arguments, and the
 * launch timeout (taken from {@link IBrowserProperties#getTimeout()}). A configured
 * {@link IBrowserProperties#getTestIdAttribute()} is applied to the driver, so {@code Selector.byTestId(...)}
 * uses it.
 * </p>
 *
 * @param <T> the {@link IBrowserProperties} type the factory is configured with; a project can pass its own
 *            interface extending {@link IBrowserProperties}
 */
public class BrowserFactory<T extends IBrowserProperties> implements IBrowserFactory {
    /**
     * The configuration properties defining which browser to launch and its settings.
     */
    private final T browserProperties;
    /**
     * Maps browser names (e.g. "chrome", "firefox") to their launchers.
     */
    private final IBrowserLauncherRegistry launcherRegistry;

    /**
     * Creates a factory for the given configuration.
     *
     * @param browserProperties The configuration: which browser to launch and how (headless mode, timeout, arguments).
     * @param launcherRegistry  The launchers available by browser name.
     */
    public BrowserFactory(T browserProperties, IBrowserLauncherRegistry launcherRegistry) {
        this.browserProperties = browserProperties;
        this.launcherRegistry = launcherRegistry;
    }

    /**
     * Launches the configured browser in the given driver.
     * <p>
     * Builds Playwright's {@link BrowserType.LaunchOptions} from the properties, applies the custom test-id
     * attribute (if any) to the driver, and launches through the launcher registered for the browser name.
     *
     * @param playwright The Playwright driver to launch the browser in.
     * @return The launched browser, with no windows open yet.
     * @throws IllegalArgumentException if no launcher is registered for the configured browser name.
     */
    @Override
    public Browser createBrowser(Playwright playwright) {
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(browserProperties.isHeadless())
                .setTimeout(browserProperties.getTimeout().doubleValue());

        if (browserProperties.getArgs() != null && !browserProperties.getArgs().isEmpty()) {
            launchOptions.setArgs(browserProperties.getArgs());
        }

        String customTestId = browserProperties.getTestIdAttribute();
        if (customTestId != null && !customTestId.trim().isEmpty()) {
            playwright.selectors().setTestIdAttribute(customTestId.trim());
        }

        IBrowserLauncher launcher = launcherRegistry.getLauncher(browserProperties.getName());
        if (launcher == null) {
            throw new IllegalArgumentException(
                    "Unsupported browser specified in properties: " + browserProperties.getName());
        }

        return launcher.launch(playwright, launchOptions);
    }
}