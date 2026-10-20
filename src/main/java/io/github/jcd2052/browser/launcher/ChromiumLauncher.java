package io.github.jcd2052.browser.launcher;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

/**
 * Concrete implementation of {@link IBrowserLauncher} for Playwright's bundled Chromium.
 * <p>
 * Unlike {@link ChromeLauncher}, this does not require Google Chrome to be installed: it uses the
 * Chromium build that Playwright downloads itself, which makes it the usual choice for CI agents and
 * containers.
 */
public class ChromiumLauncher implements IBrowserLauncher {
    /**
     * Retrieves the identifier for this launcher.
     *
     * @return The string "chromium", which maps to the browser name in the configuration properties.
     */
    @Override
    public String getName() {
        return "chromium";
    }

    /**
     * Launches Playwright's bundled Chromium browser.
     *
     * @param playwright The active {@link Playwright} connection instance.
     * @param options    The configuration options (e.g., headless, timeouts) to apply during launch.
     * @return A newly launched Playwright {@link Browser} instance running Chromium.
     */
    @Override
    public Browser launch(Playwright playwright, BrowserType.LaunchOptions options) {
        return playwright.chromium().launch(options);
    }
}
