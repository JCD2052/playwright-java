package org.jcd2052.core.browser.configuration;

import java.util.List;

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

    /**
     * Retrieves the list of custom command-line arguments to pass to the browser executable on startup.
     *
     * @return A list of argument strings (e.g., "--no-sandbox", "--disable-gpu").
     */
    List<String> getArgs();
    /**
     * Retrieves the custom test ID attribute to be used globally by Playwright Selectors.
     * If null or empty, Playwright defaults to 'data-testid'.
     *
     * @return the custom attribute string (e.g., "data-a-test").
     */
    String getTestIdAttribute();

    /**
     * Retrieves the locale to emulate for the browser context (e.g., the {@code navigator.language}
     * value, and affects number/date formatting and the {@code Accept-Language} header).
     *
     * @return the locale string (e.g., "en-US", "de-DE"), or {@code null} to use the system default.
     */
    String getLocale();

    /**
     * Retrieves the timezone to emulate for the browser context.
     *
     * @return an IANA timezone ID (e.g., "America/New_York", "Europe/Kyiv"), or {@code null} to use the system default.
     */
    String getTimezoneId();

    /**
     * Retrieves the latitude to emulate for the {@code Geolocation} API.
     * Has no effect unless {@link #getGeolocationLongitude()} is also set and {@code "geolocation"}
     * is included in {@link #getPermissions()}.
     *
     * @return the latitude in decimal degrees, or {@code null} if geolocation emulation is not configured.
     */
    Double getGeolocationLatitude();

    /**
     * Retrieves the longitude to emulate for the {@code Geolocation} API.
     * Has no effect unless {@link #getGeolocationLatitude()} is also set and {@code "geolocation"}
     * is included in {@link #getPermissions()}.
     *
     * @return the longitude in decimal degrees, or {@code null} if geolocation emulation is not configured.
     */
    Double getGeolocationLongitude();

    /**
     * Retrieves the browser permissions to grant automatically on every new context
     * (e.g., {@code "geolocation"}, {@code "notifications"}, {@code "camera"}).
     *
     * @return a list of permission names, or {@code null}/empty to grant none by default.
     */
    List<String> getPermissions();

    /**
     * Retrieves a custom {@code User-Agent} string to emulate a specific browser or device.
     *
     * @return the user agent string, or {@code null} to use Playwright's default for the launched browser.
     */
    String getUserAgent();

    /**
     * Retrieves the device scale factor (DPR) to emulate, e.g. {@code 2.0} or {@code 3.0} for
     * high-density mobile displays.
     *
     * @return the device scale factor, or {@code null} to use the default of {@code 1.0}.
     */
    Double getDeviceScaleFactor();

    /**
     * Checks whether the context should emulate a mobile device (affects viewport meta tag handling
     * and touch event support alongside {@link #isHasTouch()}).
     *
     * @return {@code true} to emulate a mobile device, {@code false} otherwise.
     */
    boolean isMobile();

    /**
     * Checks whether the context should support touch events, as on a real mobile or tablet device.
     *
     * @return {@code true} to enable touch support, {@code false} otherwise.
     */
    boolean isHasTouch();

    /**
     * Retrieves the path to a previously saved Playwright storage-state JSON file
     * (cookies and local storage) to preload into every new browser context.
     * <p>
     * This is the standard way to skip a UI login flow in every test: authenticate once,
     * save the resulting storage state to disk, then point this property at that file so
     * subsequent runs start already signed in.
     * </p>
     *
     * @return the file path to the storage-state JSON file, or {@code null}/empty to start each
     * context with a clean, unauthenticated session.
     */
    String getStorageStatePath();
}