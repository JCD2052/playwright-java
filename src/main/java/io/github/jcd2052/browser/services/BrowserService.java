package io.github.jcd2052.browser.services;

import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import io.github.jcd2052.browser.browser.PlaywrightBrowser;
import io.github.jcd2052.browser.browser.interfaces.IBrowser;
import io.github.jcd2052.browser.configuration.IBrowserProperties;
import io.github.jcd2052.browser.factory.IBrowserFactory;
import io.github.jcd2052.browser.services.interfaces.IBrowserService;
import io.github.jcd2052.logger.LoggerProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

/**
 * Default {@link IBrowserService}, keeping each thread's browser in a {@link ThreadLocal}.
 * <p>
 * Each browser gets its own {@link Playwright} driver and is launched by the {@link IBrowserFactory}, so
 * every executing thread (e.g. each parallel TestNG invocation) has its own isolated browser process.
 * </p>
 *
 * @param <T> the {@link IBrowserProperties} type the service is configured with
 */
public class BrowserService<T extends IBrowserProperties> implements IBrowserService<T> {
    /**
     * Each thread's browser; threads never share one.
     */
    private final ThreadLocal<IBrowser> threadLocalBrowser = new ThreadLocal<>();
    private final T browserProperties;
    private final IBrowserFactory browserFactory;
    private final Supplier<Playwright> playwrightSupplier;

    /**
     * Constructs a new {@code BrowserService}.
     *
     * @param browserProperties The configuration properties for the browser (tracing, headless, etc.).
     * @param browserFactory    The factory responsible for instantiating the actual Playwright browsers.
     */
    public BrowserService(T browserProperties, IBrowserFactory browserFactory) {
        this(browserProperties, browserFactory, Playwright::create);
    }

    /**
     * Constructs a new {@code BrowserService} with a custom source for the underlying
     * {@link Playwright} connection.
     * <p>
     * This overload exists primarily so unit tests can substitute a mocked {@link Playwright}
     * instance instead of launching a real driver process via {@link Playwright#create()}.
     * Production code should generally use {@link #BrowserService(IBrowserProperties, IBrowserFactory)}.
     *
     * @param browserProperties  The configuration properties for the browser (tracing, headless, etc.).
     * @param browserFactory     The factory responsible for instantiating the actual Playwright browsers.
     * @param playwrightSupplier Supplies the {@link Playwright} connection to use for each new browser.
     */
    public BrowserService(
            T browserProperties,
            IBrowserFactory browserFactory,
            Supplier<Playwright> playwrightSupplier) {
        this.browserProperties = browserProperties;
        this.browserFactory = browserFactory;
        this.playwrightSupplier = playwrightSupplier;
    }

    @Override
    public boolean isTracingEnabled() {
        return browserProperties.isTracing();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Screenshots and DOM snapshots are recorded as configured by {@link IBrowserProperties#isTracingScreenshotsEnabled()}
     * and {@link IBrowserProperties#isTracingSnapshotsEnabled()}.
     */
    @Override
    public void startTracing() {
        getBrowser().requireCurrentBrowserWindow().getBrowserContext().tracing().start(new Tracing.StartOptions()
                .setScreenshots(browserProperties.isTracingScreenshotsEnabled())
                .setSnapshots(browserProperties.isTracingSnapshotsEnabled()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * The trace is saved as {@code filename} inside {@link IBrowserProperties#getTracingSaveFolder()}, then read
     * back into memory. A failure is logged rather than thrown, so collecting a trace cannot fail test teardown.
     *
     * @param filename The specific name to assign to the exported trace ZIP file.
     * @return A {@code byte[]} containing the ZIP file data, or an empty byte array if the trace could not be
     * saved or read.
     */
    @Override
    public byte[] stopAndSaveTrace(String filename) {
        try {
            Path tracePath = Paths.get(browserProperties.getTracingSaveFolder(), filename);
            Path traceDirectory = tracePath.getParent();
            if (traceDirectory != null) {
                Files.createDirectories(traceDirectory);
            }
            getBrowser()
                    .requireCurrentBrowserWindow()
                    .getBrowserContext()
                    .tracing()
                    .stop(new Tracing.StopOptions().setPath(tracePath));
            return Files.readAllBytes(tracePath);
        } catch (Exception e) {
            LoggerProvider.getLogger().error("Failed to save the trace: " + e.getMessage());
            return new byte[0];
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Implementation Note:</b> This method retrieves the browser from {@link ThreadLocal} storage.
     * No additional synchronization is required: each thread only ever reads and writes its own
     * {@code ThreadLocal} slot, so concurrent calls from different threads (e.g. parallel TestNG
     * data providers) can never race against each other here. Locking this method would only
     * serialize browser startup across threads and undermine the parallel execution model.
     */
    @Override
    public IBrowser getBrowser() {
        IBrowser cached = threadLocalBrowser.get();
        if (cached != null && !cached.isClosed()) {
            return cached;
        }
        setBrowser(browserFactory);
        return threadLocalBrowser.get();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Closes any existing browser instance for the current thread (including one that has crashed,
     * so its driver process is released) and replaces it with a new one: a new {@link Playwright}
     * driver is started, the factory launches the browser in it, and a first window is opened. If any
     * of that fails, the new driver is closed again so no process is left running. A failure while
     * closing the old browser is logged and does not prevent the replacement.
     *
     * @param browserFactory The factory used to spawn the new browser instance.
     * @throws IllegalArgumentException if the provided factory is null.
     */
    @Override
    public void setBrowser(IBrowserFactory browserFactory) {
        if (browserFactory == null) {
            throw new IllegalArgumentException("Browser factory cannot be null");
        }

        IBrowser cached = threadLocalBrowser.get();
        if (cached != null) {
            threadLocalBrowser.remove();
            closeQuietly(cached);
        }
        Playwright playwright = playwrightSupplier.get();
        IBrowser browser;
        try {
            browser = new PlaywrightBrowser(playwright, browserFactory.createBrowser(playwright), browserProperties);
            browser.openNewWindow();
        } catch (RuntimeException e) {
            playwright.close();
            throw e;
        }
        threadLocalBrowser.set(browser);
    }

    /**
     * {@inheritDoc}
     * <p>
     * A failure while closing is logged rather than thrown, so a broken browser cannot fail test teardown;
     * the thread's browser is forgotten either way.
     */
    @Override
    public void close() {
        IBrowser browser = threadLocalBrowser.get();
        if (browser != null) {
            threadLocalBrowser.remove();
            closeQuietly(browser);
        }
    }

    /**
     * Closes the given browser, logging instead of throwing on failure.
     *
     * @param browser the browser to close
     */
    private void closeQuietly(IBrowser browser) {
        try {
            browser.close();
        } catch (RuntimeException e) {
            LoggerProvider.getLogger().warn("Failed to close browser: " + e.getMessage());
        }
    }
}