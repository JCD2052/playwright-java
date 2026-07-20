package org.jcd2052.core.browser.browser.interfaces;

import com.microsoft.playwright.BrowserContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Represents a single browser window (or {@link BrowserContext} in Playwright).
 * A window provides an isolated session that contains its own collection of {@link IBrowserTab}s.
 */
public interface IBrowserWindow {
    /**
     * Opens a new tab (Page) within this specific browser window and sets it as the active tab.
     *
     * @return The newly created {@link IBrowserTab}.
     */
    IBrowserTab openNewTab();

    /**
     * Retrieves all currently open tabs within this browser window.
     *
     * @return A collection of {@link IBrowserTab} objects.
     */
    Collection<IBrowserTab> getBrowserTabs();

    /**
     * Gets the underlying Playwright {@link BrowserContext} instance.
     * This is useful when you need to access raw Playwright context features (like managing cookies or permissions).
     *
     * @return The raw Playwright browser context object.
     */
    BrowserContext getBrowserContext();

    /**
     * Retrieves the currently active browser tab for this window.
     *
     * @return The current {@link IBrowserTab}.
     */
    IBrowserTab getCurrentBrowserTab();

    /**
     * Provides access to the cookie manager for this specific window.
     *
     * @return The ICookieManager instance.
     */
    ICookieManager getCookieManager();

    /**
     * Sets the specified tab as the currently tracked active tab in the framework.
     * Note: This usually just updates the internal reference. To actually bring a tab to the front,
     * use {@link #switchToTab(IBrowserTab)}.
     *
     * @param currentIBrowserTab The {@link IBrowserTab} to set as current.
     */
    void setCurrentBrowserTab(IBrowserTab currentIBrowserTab);

    /**
     * Brings the specified tab to the front (makes it visible) and sets it as the active tab.
     *
     * @param tab The {@link IBrowserTab} to switch to.
     */
    void switchToTab(IBrowserTab tab);

    void closeWindow();

    /**
     * Saves this window's current storage state (cookies and local storage) to a JSON file on disk.
     * <p>
     * Pair this with {@link org.jcd2052.core.browser.configuration.IBrowserProperties#getStorageStatePath()}
     * to skip a UI login in future runs: authenticate once (e.g. in a one-off setup test or a
     * {@code @BeforeSuite} method), call this to persist the session, then point
     * {@code storageStatePath} at that file so subsequent contexts start already signed in.
     * </p>
     *
     * @param path the file path to write the storage-state JSON to; parent directories are created if needed.
     */
    default void saveStorageState(Path path) {
        Path parent = path.getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new UncheckedIOException(
                        "Failed to create directories for storage state file: " + path,
                        e);
            }
        }
        getBrowserContext().storageState(new BrowserContext.StorageStateOptions().setPath(path));
    }
}