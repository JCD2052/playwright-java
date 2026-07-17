package org.jcd2052.core.browser.launcher;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

/**
 * Concrete implementation of {@link IBrowserLauncher} for WebKit.
 * <p>
 * This component is responsible for launching the WebKit browser engine
 * (the engine that powers Apple's Safari browser) utilizing Playwright's built-in
 * WebKit support.
 */
public class WebkitLauncher implements IBrowserLauncher {
    /**
     * Retrieves the identifier for this launcher.
     *
     * @return The string "webkit", which maps to the browser name in the configuration properties.
     */
    @Override
    public String getName() {
        return "webkit";
    }

    /**
     * Launches a WebKit browser instance.
     * <p>
     * <b>Implementation Note:</b> This calls {@code playwright.webkit().launch()}
     * applying the provided configuration options.
     *
     * @param playwright The active {@link Playwright} connection instance.
     * @param options    The configuration options (e.g., headless, timeouts) to apply during launch.
     * @return A newly launched Playwright {@link Browser} instance running WebKit.
     */
    @Override
    public Browser launch(Playwright playwright, BrowserType.LaunchOptions options) {
        return playwright.webkit().launch(options);
    }
}