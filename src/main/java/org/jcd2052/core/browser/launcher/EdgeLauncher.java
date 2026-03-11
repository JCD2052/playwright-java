package org.jcd2052.core.browser.launcher;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

/**
 * Concrete implementation of {@link IBrowserLauncher} for Microsoft Edge.
 * <p>
 * This component is responsible for launching the Microsoft Edge browser.
 * It achieves this by utilizing Playwright's Chromium engine and explicitly
 * setting the channel to "msedge", which forces Playwright to use the installed
 * Microsoft Edge browser rather than the default bundled Chromium.
 */
public class EdgeLauncher implements IBrowserLauncher {
    /**
     * Retrieves the identifier for this launcher.
     *
     * @return The string "edge", which maps to the browser name in the configuration properties.
     */
    @Override
    public String getName() {
        return "edge";
    }

    /**
     * Launches a Microsoft Edge browser instance.
     * <p>
     * <b>Implementation Note:</b> This calls {@code playwright.chromium().launch()} but
     * overrides the channel option to "msedge".
     *
     * @param playwright The active {@link Playwright} connection instance.
     * @param options    The configuration options (e.g., headless, timeouts) to apply during launch.
     * @return A newly launched Playwright {@link Browser} instance running Microsoft Edge.
     */
    @Override
    public Browser launch(Playwright playwright, BrowserType.LaunchOptions options) {
        return playwright.chromium().launch(options.setChannel("msedge"));
    }
}