package org.jcd2052.core.browser.launcher;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

/**
 * Concrete implementation of {@link IBrowserLauncher} for Google Chrome.
 * <p>
 * This component is responsible for launching the Google Chrome browser.
 * It achieves this by using Playwright's Chromium engine and explicitly setting
 * the channel to "chrome", which forces Playwright to use the installed Chrome
 * browser instead of the default bundled Chromium.
 */
public class ChromeLauncher implements IBrowserLauncher {
    /**
     * Retrieves the identifier for this launcher.
     *
     * @return The string "chrome", which maps to the browser name in the configuration properties.
     */
    @Override
    public String getName() {
        return "chrome";
    }

    /**
     * Launches a Google Chrome browser instance.
     * <p>
     * <b>Implementation Note:</b> This calls {@code playwright.chromium().launch()} but
     * overrides the channel option to "chrome".
     *
     * @param playwright The active {@link Playwright} connection instance.
     * @param options    The configuration options (e.g., headless, timeouts) to apply during launch.
     * @return A newly launched Playwright {@link Browser} instance running Google Chrome.
     */
    @Override
    public Browser launch(Playwright playwright, BrowserType.LaunchOptions options) {
        return playwright.chromium().launch(options.setChannel("chrome"));
    }
}