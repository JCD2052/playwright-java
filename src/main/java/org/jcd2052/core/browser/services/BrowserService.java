package org.jcd2052.core.browser.services;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import org.jcd2052.core.browser.browser.PlaywrightBrowser;
import org.jcd2052.core.browser.browser.interfaces.IBrowser;
import org.jcd2052.core.browser.browser.interfaces.IBrowserWindow;
import org.jcd2052.core.browser.configuration.IBrowserProperties;
import org.jcd2052.core.browser.factory.IBrowserFactory;
import org.jcd2052.core.browser.services.interfaces.IBrowserService;
import org.jcd2052.core.logger.LoggerProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

/**
 * Concrete implementation of the {@link IBrowserService}.
 * <p>
 * This service manages the lifecycle of Playwright browsers. It is designed to be
 * thread-safe for parallel test execution by utilizing a {@link ThreadLocal} storage
 * mechanism for the browser instances. This ensures that each executing thread
 * (e.g., each parallel TestNG method) gets its own completely isolated browser session.</p>
 */
public class BrowserService implements IBrowserService {
    /**
     * Thread-local storage to hold a separate browser instance for each executing thread,
     * guaranteeing thread safety during parallel test execution.
     */
    private final ThreadLocal<IBrowser> threadLocalBrowser = new ThreadLocal<>();
    private final IBrowserProperties browserProperties;
    private final IBrowserFactory browserFactory;
    private final Supplier<Playwright> playwrightSupplier;

    /**
     * Constructs a new {@code BrowserService}.
     *
     * @param browserProperties The configuration properties for the browser (tracing, headless, etc.).
     * @param browserFactory    The factory responsible for instantiating the actual Playwright browsers.
     */
    public BrowserService(IBrowserProperties browserProperties, IBrowserFactory browserFactory) {
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
            IBrowserProperties browserProperties,
            IBrowserFactory browserFactory,
            Supplier<Playwright> playwrightSupplier) {
        this.browserProperties = browserProperties;
        this.browserFactory = browserFactory;
        this.playwrightSupplier = playwrightSupplier;
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
        IBrowserWindow iBrowserWindow = getBrowser().getCurrentBrowserWindow();
        if (browserProperties.isTracing()) {
            iBrowserWindow.getBrowserContext().tracing().start(new Tracing.StartOptions()
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
                        .getCurrentBrowserWindow()
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
     * Closes any existing browser instance for the current thread and replaces it
     * with a new instance generated by the provided factory.
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
        if (cached != null && !cached.isClosed()) {
            cached.close();
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
        threadLocalBrowser.set(browser);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gracefully terminates the active Playwright browser for the current thread and
     * clears the {@link ThreadLocal} reference to prevent memory leaks between test suites.
     */
    @Override
    public void close() {
        IBrowser browser = threadLocalBrowser.get();
        if (browser != null) {
            browser.close();
            threadLocalBrowser.remove();
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