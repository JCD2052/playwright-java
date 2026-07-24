package org.jcd2052.core.browser.factory;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import org.jcd2052.core.browser.configuration.IBrowserProperties;

/**
 * Factory interface responsible for creating and configuring Playwright {@link Browser} instances.
 * <p>
 * Implementations of this interface handle the specific launch arguments, browser types
 * (e.g., Chromium, Firefox, WebKit), and configurations required to start a browser session.
 * <p>
 * Parameterized over {@code T} so that projects with configuration needs beyond the framework's
 * built-in properties (e.g. a proxy profile, an environment name) can supply their own interface
 * extending {@link IBrowserProperties} and get it back, fully typed, via {@link #getBrowserProperties()}
 * — no casting required.
 *
 * @param <T> the concrete {@link IBrowserProperties} type this factory was configured with
 */
public interface IBrowserFactory<T extends IBrowserProperties> {
    /**
     * Creates and launches a new Playwright {@link Browser} instance.
     *
     * @param playwright The active {@link Playwright} connection instance used to launch the browser.
     * @return A newly launched and configured Playwright {@link Browser} ready for context creation.
     */
    Browser createBrowser(Playwright playwright);

    /**
     * The browser properties this factory was constructed with, in their original, fully-typed form.
     *
     * @return the configured properties instance.
     */
    T getBrowserProperties();
}