package org.jcd2052.core.browser.configuration;

/**
 * Defines the configuration properties required to initialize and manage a browser session.
 * <p>
 * These properties control various aspects of the browser's behavior, such as viewport size,
 * execution mode (headless or visible), timeouts, and Playwright tracing capabilities.
 */
public interface IBrowserProperties {

    /**
     * Retrieves the name of the browser to be launched.
     *
     * @return The browser name (e.g., "chromium", "firefox", "webkit", "chrome", "edge").
     */
    String getName();

    /**
     * Retrieves the directory path where Playwright execution traces will be saved.
     *
     * @return The file path to the tracing save folder.
     */
    String getTracingSaveFolder();

    /**
     * Retrieves the default timeout duration for browser and page operations.
     *
     * @return The timeout in milliseconds.
     */
    Long getTimeout();

    /**
     * Retrieves the default timeout duration for page loading.
     *
     * @return The timeout in milliseconds.
     */
    Long getPageLoadTimeout();

    /**
     * Retrieves the default width of the browser viewport.
     *
     * @return The viewport width in pixels.
     */
    Integer getWidth();

    /**
     * Retrieves the default height of the browser viewport.
     *
     * @return The viewport height in pixels.
     */
    Integer getHeight();

    /**
     * Checks if the browser should be launched in headless mode.
     * Headless mode runs the browser in the background without a graphical user interface.
     *
     * @return {@code true} if headless mode is enabled, {@code false} otherwise.
     */
    boolean isHeadless();

    /**
     * Checks if visual element highlighting is enabled.
     * When enabled, the framework may draw borders or highlight elements being interacted with
     * during test execution (useful for debugging).
     *
     * @return {@code true} if element highlighting is enabled, {@code false} otherwise.
     */
    boolean isHighlight();

    /**
     * Checks if Playwright tracing is enabled for the browser session.
     * Tracing records a detailed log of the execution, which can be viewed in the Playwright Trace Viewer.
     *
     * @return {@code true} if tracing is enabled, {@code false} otherwise.
     */
    boolean isTracing();

    /**
     * Checks if screenshots should be captured during execution.
     * This is often used in conjunction with tracing to record visual states.
     *
     * @return {@code true} if screenshots are enabled, {@code false} otherwise.
     */
    boolean isScreenshots();

    /**
     * Checks if DOM snapshots should be captured during execution.
     * Snapshots allow for DOM inspection within the Playwright Trace Viewer.
     *
     * @return {@code true} if snapshots are enabled, {@code false} otherwise.
     */
    boolean isSnapshots();
}