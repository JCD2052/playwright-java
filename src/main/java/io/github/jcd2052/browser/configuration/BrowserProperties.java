package io.github.jcd2052.browser.configuration;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Default {@link IBrowserProperties}: a plain holder for the configuration, with chainable setters
 * ({@code new BrowserProperties().setName("chrome").setHeadless(true)}). See {@link IBrowserProperties} for what
 * each setting does.
 */
@Data
@Accessors(chain = true)
public class BrowserProperties implements IBrowserProperties {
    /**
     * The folder that saved Playwright traces are written to.
     */
    private String tracingSaveFolder;

    /**
     * The name of the browser to launch (e.g. "chrome", "chromium", "edge", "firefox", "webkit").
     */
    private String name;

    /**
     * The default timeout in milliseconds for element actions and waits; also used as the launch timeout.
     */
    private Long timeout;

    /**
     * The default timeout in milliseconds for navigations.
     */
    private Long pageLoadTimeout;

    /**
     * The viewport width of new windows, in pixels.
     */
    private Integer width;

    /**
     * The viewport height of new windows, in pixels.
     */
    private Integer height;

    /**
     * Whether elements are outlined in red before some actions, for debugging.
     */
    private boolean highlight;

    /**
     * Whether tests should record Playwright traces.
     */
    private boolean tracing;

    /**
     * Whether traces record screenshots.
     */
    private boolean tracingScreenshotsEnabled;

    /**
     * Whether traces record DOM snapshots.
     */
    private boolean tracingSnapshotsEnabled;

    /**
     * Flag indicating whether the browser should run in headless mode (no GUI).
     */
    private boolean headless;

    /**
     * Custom command-line arguments to pass to the browser executable.
     */
    private List<String> args;

    /**
     * The custom attribute Playwright should use instead of {@code data-testid} for {@code Selector.byTestId()}.
     */
    private String testIdAttribute;

    /**
     * The locale to emulate (e.g., "en-US"), or {@code null} to use the system default.
     */
    private String locale;

    /**
     * The IANA timezone ID to emulate (e.g., "Europe/Kyiv"), or {@code null} to use the system default.
     */
    private String timezoneId;

    /**
     * The latitude to emulate for the Geolocation API. Requires {@link #geolocationLongitude}
     * and {@code "geolocation"} to be present in {@link #permissions}.
     */
    private Double geolocationLatitude;

    /**
     * The longitude to emulate for the Geolocation API. Requires {@link #geolocationLatitude}
     * and {@code "geolocation"} to be present in {@link #permissions}.
     */
    private Double geolocationLongitude;

    /**
     * Browser permissions to grant automatically on every new context (e.g., "geolocation", "camera").
     */
    private List<String> permissions;

    /**
     * A custom User-Agent string to emulate a specific browser or device.
     */
    private String userAgent;

    /**
     * The device scale factor (DPR) to emulate, e.g. 2.0 for high-density mobile displays.
     */
    private Double deviceScaleFactor;

    /**
     * Whether to emulate a mobile device.
     */
    private boolean mobile;

    /**
     * Whether to enable touch event support, as on a real mobile or tablet device.
     */
    private boolean hasTouch;

    /**
     * Path to a previously saved Playwright storage-state JSON file to preload into every
     * new browser context, letting tests start already authenticated instead of repeating a UI login.
     */
    private String storageStatePath;
}