package org.jcd2052.core.browser.launcher;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

/**
 * Concrete implementation of {@link IBrowserLauncher} for Mozilla Firefox.
 * <p>
 * This component is responsible for launching the Firefox browser utilizing
 * Playwright's built-in Firefox engine.
 */
public class FirefoxLauncher implements IBrowserLauncher {
    /**
     * Retrieves the identifier for this launcher.
     *
     * @return The string "firefox", which maps to the browser name in the configuration properties.
     */
    @Override
    public String getName() {
        return "firefox";
    }

    /**
     * Launches a Mozilla Firefox browser instance.
     * <p>
     * <b>Implementation Note:</b> This calls {@code playwright.firefox().launch()}
     * applying the provided configuration options.
     *
     * @param playwright The active {@link Playwright} connection instance.
     * @param options    The configuration options (e.g., headless, timeouts) to apply during launch.
     * @return A newly launched Playwright {@link Browser} instance running Mozilla Firefox.
     */
    @Override
    public Browser launch(Playwright playwright, BrowserType.LaunchOptions options) {
        return playwright.firefox().launch(options);
    }
}