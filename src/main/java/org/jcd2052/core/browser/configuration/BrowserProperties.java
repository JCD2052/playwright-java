package org.jcd2052.core.browser.configuration;

import lombok.Data;
import lombok.experimental.Accessors;

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
}