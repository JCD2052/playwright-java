package org.jcd2052.core.browser.services.interfaces;

import org.jcd2052.core.browser.browser.interfaces.IBrowser;
import org.jcd2052.core.browser.factory.IBrowserFactory;

/**
 * A core framework service defining the contract for Playwright browser lifecycle management.
 * <p>Implementations of this interface are responsible for safely starting, stopping,
 * and providing access to {@link IBrowser} instances. It ensures that browser sessions
 * are properly allocated to executing threads (crucial for parallel test execution)
 * and manages recording capabilities like Playwright Tracing.</p>
 */
public interface IBrowserService {
    /**
     * Begins recording Playwright traces (screenshots, DOM snapshots) for the current
     * browser context, if tracing is enabled in the configuration.
     * <p>This should typically be called immediately after a new window/context is opened
     * at the start of a test session.</p>
     */
    void startTracing();

    /**
     * Stops the Playwright tracer, exports the recorded data to a ZIP archive,
     * and returns the binary data for reporting integration.
     * <p>This method is intended to be called during test teardown (typically when a test fails),
     * ensuring that valuable debugging artifacts are preserved for analysis and easily
     * attachable to test execution reports.</p>
     *
     * @param filename The specific name to assign to the exported trace ZIP file.
     * @return A {@code byte[]} array containing the compiled trace ZIP file data,
     * or {@code null} if tracing was disabled or failed to save.
     */
    byte[] stopAndSaveTrace(String filename);

    /**
     * Retrieves the active, thread-safe browser instance for the current execution context.
     * <p>If a browser does not currently exist for the active thread, the implementation
     * should automatically instantiate a new one using the registered factory.</p>
     *
     * @return The current {@link IBrowser} instance.
     */
    IBrowser getBrowser();

    /**
     * Forces the creation or replacement of the current thread's browser instance
     * using the provided factory.
     *
     * @param browserFactory The {@link IBrowserFactory} used to instantiate the new browser.
     */
    void setBrowser(IBrowserFactory browserFactory);

    /**
     * Gracefully terminates the active browser instance and cleans up any
     * underlying thread-local references to prevent memory leaks between test suites.
     */
    void close();

    /**
     * Convenience default method to initialize the browser session for the current executing test.
     * <p>This method automatically retrieves the active browser and opens a fresh window
     * and tab, preparing a clean state for test navigation and execution.</p>
     */
    default void start() {
        getBrowser().openNewWindow().openNewTab();
    }

    default void navigateTo(String url) {
        getBrowser().getCurrentBrowserWindow().getCurrentBrowserTab().navigateTo(url);
    }
}