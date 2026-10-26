package io.github.jcd2052.browser.services.interfaces;

import io.github.jcd2052.browser.browser.interfaces.IBrowser;
import io.github.jcd2052.browser.configuration.IBrowserProperties;
import io.github.jcd2052.browser.factory.IBrowserFactory;

/**
 * Manages the browser of each test thread: one thread, one browser.
 * <p>
 * {@link #getBrowser()} returns the calling thread's browser, launching it (with a first window) when the
 * thread has none, and {@link #close()} closes it, normally in test teardown. Threads never share a browser,
 * so tests can run in parallel without sharing cookies, storage or pages. The service also records Playwright
 * traces of the current window.
 * </p>
 *
 * @param <T> the {@link IBrowserProperties} type the service is configured with; a project can use its own
 *            interface extending {@link IBrowserProperties}
 */
public interface IBrowserService<T extends IBrowserProperties> {
    /**
     * Checks whether tracing is enabled in the configuration ({@link IBrowserProperties#isTracing()}).
     * <p>{@link #startTracing()} and {@link #stopAndSaveTrace(String)} always act; use this to decide
     * whether to call them.</p>
     *
     * @return {@code true} if tracing is enabled, {@code false} otherwise.
     */
    boolean isTracingEnabled();

    /**
     * Stops the Playwright trace started by {@link #startTracing()}, exports the recorded data to a ZIP
     * archive, and returns the binary data for reporting integration.
     * <p>This method is intended to be called during test teardown (typically when a test fails),
     * ensuring that valuable debugging artifacts are preserved for analysis and easily
     * attachable to test execution reports.</p>
     *
     * @param filename The specific name to assign to the exported trace ZIP file.
     * @return A {@code byte[]} array containing the compiled trace ZIP file data,
     * or an empty array if it could not be saved.
     */
    byte[] stopAndSaveTrace(String filename);

    /**
     * Gets the current thread's browser instance.
     * <p>If the thread has a running browser, it is returned as is. If it has none (never created, closed,
     * or crashed), a new one is created with the registered factory and opened with a first window.</p>
     *
     * @return The current {@link IBrowser} instance.
     */
    IBrowser getBrowser();

    /**
     * Replaces the current thread's browser with a new one launched by the given factory, closing the old one.
     * The new browser opens with a first window.
     *
     * @param browserFactory The factory to launch the new browser with.
     */
    void setBrowser(IBrowserFactory browserFactory);

    /**
     * Closes the current thread's browser, with all its windows and its Playwright driver, and forgets it, so
     * the next {@link #getBrowser()} launches a new one. Call it in the teardown of every test.
     */
    void close();

    /**
     * Begins recording a Playwright trace (screenshots and DOM snapshots, as configured) for the current
     * browser window.
     * <p>This should typically be called right after the window is opened at the start of a test.</p>
     */
    void startTracing();
}