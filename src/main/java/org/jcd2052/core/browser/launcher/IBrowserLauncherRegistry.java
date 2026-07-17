package org.jcd2052.core.browser.launcher;

/**
 * Defines the contract for registering and retrieving browser launchers.
 */
public interface IBrowserLauncherRegistry {
    /**
     * Registers a new browser launcher into the framework.
     *
     * @param launcher The specific launcher implementation to register.
     */
    void register(IBrowserLauncher launcher);

    /**
     * Retrieves a registered browser launcher by its string identifier.
     *
     * @param name The name of the browser (e.g., "chrome", "firefox").
     * @return The corresponding {@link IBrowserLauncher}, or null if not found.
     */
    IBrowserLauncher getLauncher(String name);
}