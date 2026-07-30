package io.github.jcd2052.browser.launcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Concrete implementation of the {@link IBrowserLauncherRegistry}.
 * By default, it automatically registers the core Playwright supported browsers.
 */
public class BrowserLauncherRegistry implements IBrowserLauncherRegistry {
    private final Map<String, IBrowserLauncher> registry = new HashMap<>();

    /**
     * Creates the registry and registers the four core Playwright-supported browsers
     * ({@code chrome}, {@code firefox}, {@code edge}, {@code webkit}) under their default names.
     */
    public BrowserLauncherRegistry() {
        register(new ChromeLauncher());
        register(new FirefoxLauncher());
        register(new EdgeLauncher());
        register(new WebkitLauncher());
    }

    @Override
    public void register(IBrowserLauncher launcher) {
        registry.put(launcher.getName().toLowerCase(), launcher);
    }

    @Override
    public IBrowserLauncher getLauncher(String name) {
        if (name == null) {
            return null;
        }
        return registry.get(name.toLowerCase());
    }
}