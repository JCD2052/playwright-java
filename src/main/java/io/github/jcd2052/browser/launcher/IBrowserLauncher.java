package io.github.jcd2052.browser.launcher;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

/**
 * Launches one kind of browser, such as installed Chrome or Playwright's bundled Firefox.
 * <p>
 * Each launcher has a name that the configuration refers to ({@code IBrowserProperties.getName()}). Register
 * custom launchers in an {@link IBrowserLauncherRegistry} to support more browsers or browser channels.
 * </p>
 */
public interface IBrowserLauncher {

    /**
     * Gets the name this launcher is registered under, matched against the configured browser name
     * (case-insensitively), e.g. "chrome", "chromium", "edge", "firefox" or "webkit".
     *
     * @return The launcher's browser name.
     */
    String getName();

    /**
     * Launches this launcher's browser in the given driver.
     *
     * @param playwright The Playwright driver to launch the browser in.
     * @param options    The launch options built from the configuration (headless mode, timeout, arguments).
     * @return The launched browser.
     */
    Browser launch(Playwright playwright, BrowserType.LaunchOptions options);
}