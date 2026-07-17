package org.jcd2052.core.browser.launcher;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

/**
 * Defines the contract for specific browser launching strategies.
 * <p>
 * Implementations of this interface (e.g., ChromeLauncher, FirefoxLauncher) are responsible
 * for executing the specific Playwright commands required to launch a particular browser engine.
 */
public interface IBrowserLauncher {

    /**
     * Retrieves the unique string identifier for this browser launcher.
     * This name should match the browser name provided in the configuration properties
     * (e.g., "chrome", "firefox", "webkit", "edge").
     *
     * @return The string identifier of the browser.
     */
    String getName();

    /**
     * Launches the specific browser using the provided Playwright instance and launch options.
     *
     * @param playwright The active {@link Playwright} connection instance.
     * @param options    The configuration options (like headless mode, timeouts, args) to apply
     * during the browser launch.
     * @return A newly launched Playwright {@link Browser} instance.
     */
    Browser launch(Playwright playwright, BrowserType.LaunchOptions options);
}