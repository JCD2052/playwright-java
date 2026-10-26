package io.github.jcd2052.browser.configuration;

import java.util.List;

/**
 * The configuration of the browsers the framework launches and of the windows it opens in them.
 * <p>
 * Launch settings (browser name, headless mode, launch arguments, test-id attribute) are applied when a browser
 * is launched; window settings (viewport, timeouts, locale, timezone, geolocation, permissions, device
 * emulation, storage state) are applied to every new window. Optional settings left {@code null} or blank fall
 * back to Playwright's defaults. Projects can extend this interface with settings of their own.
 * </p>
 */
public interface IBrowserProperties {

    /**
     * Gets the name of the browser to launch, matched case-insensitively against the registered launchers.
     *
     * @return The browser name: "chrome", "chromium", "edge", "firefox", "webkit", or a custom launcher's name.
     */
    String getName();

    /**
     * Gets the folder that saved Playwright traces are written to.
     *
     * @return The tracing folder path.
     */
    String getTracingSaveFolder();

    /**
     * Gets the default timeout for element actions and waits in every new window. It is also used as the
     * browser launch timeout, so keep it above the time a cold browser start takes.
     *
     * @return The timeout in milliseconds.
     */
    Long getTimeout();

    /**
     * Gets the default timeout for navigations (loading a page) in every new window.
     *
     * @return The timeout in milliseconds.
     */
    Long getPageLoadTimeout();

    /**
     * Gets the viewport width of every new window.
     *
     * @return The viewport width in pixels.
     */
    Integer getWidth();

    /**
     * Gets the viewport height of every new window.
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
     * Checks whether elements are highlighted while the framework works with them: when enabled, an element is
     * outlined in red before actions such as reading its text or typing into it. Useful for debugging.
     *
     * @return {@code true} if element highlighting is enabled, {@code false} otherwise.
     */
    boolean isHighlight();

    /**
     * Checks whether tests should record Playwright traces, which can be opened in the Playwright Trace Viewer.
     * The browser service's start and stop methods don't check this; callers ask
     * {@code IBrowserService.isTracingEnabled()} first.
     *
     * @return {@code true} if tracing is enabled, {@code false} otherwise.
     */
    boolean isTracing();

    /**
     * Checks whether traces record screenshots of the page as the test runs.
     *
     * @return {@code true} if screenshots are enabled, {@code false} otherwise.
     */
    boolean isTracingScreenshotsEnabled();

    /**
     * Checks whether traces record DOM snapshots, which let the Trace Viewer show the page at each step.
     *
     * @return {@code true} if snapshots are enabled, {@code false} otherwise.
     */
    boolean isTracingSnapshotsEnabled();

    /**
     * Gets extra command-line arguments to pass to the browser when it is launched.
     *
     * @return A list of argument strings (e.g., "--no-sandbox", "--disable-gpu").
     */
    List<String> getArgs();
    /**
     * Gets the attribute {@code Selector.byTestId(...)} matches against, applied to the Playwright driver when a
     * browser is launched. If {@code null} or blank, Playwright's default {@code data-testid} is used.
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
     * Gets the path of a previously saved Playwright storage-state JSON file (cookies and local storage) to
     * preload into every new window. The file must exist when a window is opened.
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