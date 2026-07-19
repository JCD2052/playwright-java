package org.jcd2052.core.browser.launcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Concrete implementation of the {@link IBrowserLauncherRegistry}.
 * Selector default, it automatically registers the core Playwright supported browsers.
 */
public class BrowserLauncherRegistry implements IBrowserLauncherRegistry {
    private final Map<String, IBrowserLauncher> registry = new HashMap<>();

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