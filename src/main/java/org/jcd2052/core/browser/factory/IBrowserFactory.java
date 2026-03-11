package org.jcd2052.core.browser.factory;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;

/**
 * Factory interface responsible for creating and configuring Playwright {@link Browser} instances.
 * <p>
 * Implementations of this interface handle the specific launch arguments, browser types
 * (e.g., Chromium, Firefox, WebKit), and configurations required to start a browser session.
 */
public interface IBrowserFactory {
    /**
     * Creates and launches a new Playwright {@link Browser} instance.
     *
     * @param playwright The active {@link Playwright} connection instance used to launch the browser.
     * @return A newly launched and configured Playwright {@link Browser} ready for context creation.
     */
    Browser createBrowser(Playwright playwright);
}