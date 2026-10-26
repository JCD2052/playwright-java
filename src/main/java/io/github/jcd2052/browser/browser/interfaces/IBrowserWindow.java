package io.github.jcd2052.browser.browser.interfaces;

import com.microsoft.playwright.BrowserContext;
import io.github.jcd2052.browser.configuration.IBrowserProperties;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Represents a single browser window (a Playwright {@link BrowserContext}).
 * <p>
 * A window is an isolated session: its tabs share cookies and storage with each other, but not with other
 * windows. It manages its own {@link IBrowserTab}s and keeps track of which one is current.
 * </p>
 */
public interface IBrowserWindow {
    /**
     * Opens a new tab in this window and makes it the current tab.
     *
     * @return The newly created {@link IBrowserTab}.
     */
    IBrowserTab openNewTab();

    /**
     * Closes the given tab. If it was the current tab, this window's newest remaining tab becomes current.
     *
     * @param tab The {@link IBrowserTab} to close.
     */
    void closeTab(IBrowserTab tab);

    /**
     * Retrieves all currently open tabs within this browser window, including popups opened by its pages.
     *
     * @return A list of {@link IBrowserTab}s, oldest first.
     */
    List<IBrowserTab> getBrowserTabs();

    /**
     * Gets the underlying Playwright {@link BrowserContext}, for context features this interface doesn't cover
     * (permissions, routes, tracing, ...).
     *
     * @return The raw Playwright browser context object.
     */
    BrowserContext getBrowserContext();

    /**
     * Retrieves this window's current tab: the tab last opened or switched to in this window, or, if there
     * is none (or it was closed), this window's newest tab.
     *
     * @return The current {@link IBrowserTab}, or {@code null} if no tab is open.
     */
    IBrowserTab getCurrentBrowserTab();

    /**
     * Gets a manager for this window's cookies, which all its tabs share.
     *
     * @return The cookie manager.
     */
    ICookieManager getCookieManager();

    /**
     * Brings the given tab to the front and makes it this window's current tab.
     *
     * @param tab The {@link IBrowserTab} to switch to.
     */
    void switchToTab(IBrowserTab tab);

    /**
     * Closes this window together with all its tabs.
     */
    void closeWindow();

    /**
     * Saves this window's current storage state (cookies and local storage) to a JSON file on disk.
     * <p>
     * Use it to skip a UI login in later tests: authenticate once (e.g. in a one-off setup test or a
     * {@code @BeforeSuite} method), call this to persist the session, then load that file into new windows,
     * either for every window through {@link IBrowserProperties#getStorageStatePath()} or for one window with
     * {@link IBrowser#openNewWindow(Path)}.
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