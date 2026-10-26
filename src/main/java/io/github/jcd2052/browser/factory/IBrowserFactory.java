package io.github.jcd2052.browser.factory;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import io.github.jcd2052.browser.configuration.IBrowserProperties;

/**
 * Launches Playwright {@link Browser} instances from the configuration.
 * <p>
 * Implementations decide which browser to launch and with which launch options (headless mode, launch
 * arguments, ...), usually from {@link IBrowserProperties}. The browser service calls this once per browser
 * it creates, with a new {@link Playwright} driver each time.
 * </p>
 */
public interface IBrowserFactory {
    /**
     * Launches a new browser in the given Playwright driver.
     *
     * @param playwright The Playwright driver to launch the browser in.
     * @return The launched browser, with no windows open yet.
     */
    Browser createBrowser(Playwright playwright);
}