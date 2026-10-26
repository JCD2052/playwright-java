package io.github.jcd2052.browser.launcher;

/**
 * Keeps the available {@link IBrowserLauncher}s by browser name, so the factory can find the launcher for the
 * configured browser. Names are matched case-insensitively.
 */
public interface IBrowserLauncherRegistry {
    /**
     * Registers a launcher under its own name ({@link IBrowserLauncher#getName()}), replacing any launcher
     * already registered under that name.
     *
     * @param launcher The launcher to register.
     */
    void register(IBrowserLauncher launcher);

    /**
     * Finds the launcher registered under the given browser name, ignoring case.
     *
     * @param name The browser name, e.g. "chrome" or "Firefox".
     * @return The launcher, or {@code null} if none is registered under that name (or the name is {@code null}).
     */
    IBrowserLauncher getLauncher(String name);
}