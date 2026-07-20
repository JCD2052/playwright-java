package org.jcd2052.core.browser.configuration;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Concrete implementation of the {@link IBrowserProperties} interface.
 * <p>
 * This class acts as a data holder for browser configuration settings.
 * It utilizes Lombok's {@code @Data} for boilerplate code generation (getters, setters, etc.)
 * and {@code @Accessors(chain = true)} to allow for fluent, chainable setter methods
 * during object initialization.
 */
@Data
@Accessors(chain = true)
public class BrowserProperties implements IBrowserProperties {
    /**
     * The directory path where Playwright execution traces and artifacts will be saved.
     */
    private String tracingSaveFolder;

    /**
     * The name of the browser to launch (e.g., "chromium", "firefox", "webkit", "chrome").
     */
    private String name;

    /**
     * The default maximum timeout duration in milliseconds for browser and page operations.
     */
    private Long timeout;

    /**
     * The default maximum timeout duration in milliseconds page loading.
     */
    private Long pageLoadTimeout;

    /**
     * The default width of the browser viewport in pixels.
     */
    private Integer width;

    /**
     * The default height of the browser viewport in pixels.
     */
    private Integer height;

    /**
     * Flag indicating whether visual element highlighting is enabled during execution.
     */
    private boolean highlight;

    /**
     * Flag indicating whether Playwright tracing is enabled to record test execution.
     */
    private boolean tracing;

    /**
     * Flag indicating whether screenshots should be captured during test execution.
     */
    private boolean screenshots;

    /**
     * Flag indicating whether DOM snapshots should be captured (often used alongside tracing).
     */
    private boolean snapshots;

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