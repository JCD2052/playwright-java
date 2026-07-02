package org.jcd2052.core.browser.factory;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import org.jcd2052.core.browser.configuration.IBrowserProperties;
import org.jcd2052.core.browser.launcher.IBrowserLauncher;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Concrete implementation of {@link IBrowserFactory}.
 * <p>
 * This component acts as a central factory for spawning Playwright {@link Browser} instances.
 * It leverages Spring's dependency injection to discover all registered {@link IBrowserLauncher}
 * beans and selects the appropriate launcher based on the provided {@link IBrowserProperties}.
 */
public class BrowserFactory implements IBrowserFactory {
    /**
     * The configuration properties defining which browser to launch and its settings.
     */
    private final IBrowserProperties browserProperties;
    /**
     * A registry mapping browser names (e.g., "chrome", "firefox") to their respective launchers.
     */
    private final Map<String, IBrowserLauncher> launcherRegistry;
    /**
     * Constructs a new {@code BrowserFactory}.
     * <p>
     * Spring automatically injects the active browser properties and a list of all available
     * classes implementing {@link IBrowserLauncher}. The list of launchers is mapped into a
     * registry for quick O(1) lookups during browser creation.
     *
     * @param browserProperties The browser configuration settings.
     * @param launchers         A list of all available browser launchers provided by the Spring context.
     */
    public BrowserFactory(IBrowserProperties browserProperties, List<IBrowserLauncher> launchers) {
        this.browserProperties = browserProperties;
        this.launcherRegistry = launchers.stream()
                .collect(Collectors.toMap(IBrowserLauncher::getName, Function.identity()));
    }

    /**
     * Creates and launches a new Playwright {@link Browser} based on the configured properties.
     * <p>
     * This method translates the {@link IBrowserProperties} (like headless mode and timeout) into
     * Playwright's {@link BrowserType.LaunchOptions}. It then retrieves the correct launcher from
     * the registry and spawns the browser.
     *
     * @param playwright The active {@link Playwright} connection instance.
     * @return A newly launched Playwright {@link Browser}.
     * @throws IllegalArgumentException If the browser name specified in properties is not supported
     *                                  (i.e., no matching launcher is found in the registry).
     */
    @Override
    public Browser createBrowser(Playwright playwright) {
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(browserProperties.isHeadless())
                .setTimeout(browserProperties.getTimeout().doubleValue());

        if (browserProperties.getArgs() != null && !browserProperties.getArgs().isEmpty()) {
            launchOptions.setArgs(browserProperties.getArgs());
        }

        IBrowserLauncher launcher = launcherRegistry.get(browserProperties.getName().toLowerCase());

        if (launcher == null) {
            throw new IllegalArgumentException(
                    "Unsupported browser specified in properties: " + browserProperties.getName());
        }

        return launcher.launch(playwright, launchOptions);
    }
}