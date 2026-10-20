package io.github.jcd2052.browser.services;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.Tracing;
import io.github.jcd2052.browser.browser.PlaywrightBrowser;
import io.github.jcd2052.browser.browser.interfaces.IBrowser;
import io.github.jcd2052.browser.configuration.IBrowserProperties;
import io.github.jcd2052.browser.factory.IBrowserFactory;
import io.github.jcd2052.browser.services.interfaces.IBrowserService;
import io.github.jcd2052.exceptions.PlaywrightExceptionParser;
import io.github.jcd2052.logger.LoggerProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Concrete implementation of the {@link IBrowserService}.
 * <p>
 * This service manages the lifecycle of Playwright browsers. It is designed to be
 * thread-safe for parallel test execution by utilizing a {@link ThreadLocal} storage
 * mechanism for the browser instances. This ensures that each executing thread
 * (e.g., each parallel TestNG method) gets its own completely isolated browser session.</p>
 */
public class BrowserService<T extends IBrowserProperties> implements IBrowserService<T> {
    /**
     * Thread-local storage to hold a separate browser instance for each executing thread,
     * guaranteeing thread safety during parallel test execution.
     */
    private final ThreadLocal<IBrowser> threadLocalBrowser = new ThreadLocal<>();
    /**
     * Every browser opened by this service and not yet closed, across all threads, so
     * {@link #closeAll()} can release browsers whose threads never called {@link #close()}.
     */
    private final Set<IBrowser> openBrowsers = ConcurrentHashMap.newKeySet();
    private final T browserProperties;
    private final IBrowserFactory<T> browserFactory;
    private final Supplier<Playwright> playwrightSupplier;

    /**
     * Constructs a new {@code BrowserService}.
     *
     * @param browserProperties The configuration properties for the browser (tracing, headless, etc.).
     * @param browserFactory    The factory responsible for instantiating the actual Playwright browsers.
     */
    public BrowserService(T browserProperties, IBrowserFactory<T> browserFactory) {
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
            IBrowserFactory<T> browserFactory,
            Supplier<Playwright> playwrightSupplier) {
        this.browserProperties = browserProperties;
        this.browserFactory = browserFactory;
        this.playwrightSupplier = playwrightSupplier;
    }

    @Override
    public T getBrowserProperties() {
        return browserProperties;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the current thread's browser has lost its connection (its {@link Playwright} instance was
     * closed or its driver process died), Playwright still reports the browser as connected, so the
     * loss only shows up here as a failed call. In that case the dead browser is replaced and the
     * window and tab are opened once more, so one lost connection doesn't break every later test on
     * this thread. Any other failure is rethrown unchanged.
     */
    @Override
    public void start() {
        try {
            IBrowserService.super.start();
        } catch (PlaywrightException e) {
            if (!PlaywrightExceptionParser.isConnectionLost(e)) {
                throw e;
            }
            LoggerProvider.getLogger().warn("Lost the browser connection, launching a new browser: "
                    + e.getMessage().lines().findFirst().orElse(""));
            setBrowser(browserFactory);
            IBrowserService.super.start();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Lifecycle Note:</b> This method retrieves the currently active browser window
     * and, if tracing is enabled via properties, starts the Playwright tracer to begin
     * recording DOM snapshots and screenshots for the session.
     */
    @Override
    public void startTracing() {
        if (browserProperties.isTracing()) {
            getBrowser().requireCurrentBrowserWindow().getBrowserContext().tracing().start(new Tracing.StartOptions()
                    .setScreenshots(browserProperties.isScreenshots())
                    .setSnapshots(browserProperties.isSnapshots()));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Implementation Note:</b> Explicitly stops the Playwright tracer, exports the recorded
     * data to a ZIP file on disk, and reads that file into memory to be returned.
     * This method is typically called during test teardown <b>only if a test fails</b>,
     * ensuring that valuable debugging artifacts are preserved for root-cause analysis.</p>
     *
     * @param filename The specific name to assign to the exported trace ZIP file.
     * @return A {@code byte[]} containing the ZIP file data, or an empty byte array
     * ({@code new byte[0]}) if tracing is disabled or an error occurs during file reading.
     */
    @Override
    public byte[] stopAndSaveTrace(String filename) {
        if (browserProperties.isTracing()) {
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
                LoggerProvider.getLogger().error("Failed to read trace file into byte array: " + e.getMessage());
            }
        }
        return new byte[0];
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
     * so its driver process is released) and replaces it with a new instance generated by the
     * provided factory. A failure while closing the old browser is logged and does not prevent the
     * new one from being created.
     *
     * @param browserFactory The factory used to spawn the new browser instance.
     * @throws IllegalArgumentException if the provided factory is null.
     */
    @Override
    public void setBrowser(IBrowserFactory<T> browserFactory) {
        if (browserFactory == null) {
            throw new IllegalArgumentException("Browser factory cannot be null");
        }

        IBrowser cached = threadLocalBrowser.get();
        if (cached != null) {
            threadLocalBrowser.remove();
            closeQuietly(cached);
        }
        Playwright playwright = getPlaywright();
        Browser playwrightBrowser;
        try {
            playwrightBrowser = browserFactory.createBrowser(playwright);
        } catch (RuntimeException e) {
            playwright.close();
            throw e;
        }
        IBrowser browser = new PlaywrightBrowser(playwright, playwrightBrowser, browserProperties);
        openBrowsers.add(browser);
        threadLocalBrowser.set(browser);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gracefully terminates the active Playwright browser for the current thread and
     * clears the {@link ThreadLocal} reference to prevent memory leaks between test suites.
     * A failure while closing is logged rather than thrown, so a broken browser cannot fail test
     * teardown; the reference is cleared either way, so the next call starts a fresh browser.
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
     * {@inheritDoc}
     * <p>
     * Failures are logged per browser so one broken browser does not keep the others open.
     */
    @Override
    public void closeAll() {
        threadLocalBrowser.remove();
        openBrowsers.forEach(this::closeQuietly);
    }

    /**
     * Closes the given browser and forgets it, logging instead of throwing on failure.
     *
     * @param browser the browser to close
     */
    private void closeQuietly(IBrowser browser) {
        openBrowsers.remove(browser);
        try {
            browser.close();
        } catch (RuntimeException e) {
            LoggerProvider.getLogger().warn("Failed to close browser: " + e.getMessage());
        }
    }

    /**
     * Helper method to initialize the core Playwright connection.
     *
     * @return A {@link Playwright} instance from the configured supplier.
     */
    private Playwright getPlaywright() {
        return playwrightSupplier.get();
    }
}