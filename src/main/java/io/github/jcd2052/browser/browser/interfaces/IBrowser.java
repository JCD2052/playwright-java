package io.github.jcd2052.browser.browser.interfaces;

import com.microsoft.playwright.Browser;
import io.github.jcd2052.browser.configuration.IBrowserProperties;

import java.nio.file.Path;
import java.util.List;

/**
 * Represents one separate browser process managed by the framework.
 * <p>
 * A browser owns its windows (Playwright browser contexts), and each window owns its tabs (pages).
 * Windows and tabs are only reachable through the browser that opened them: a second browser instance
 * has no access to the windows and tabs of the first.
 * </p>
 */
public interface IBrowser {
    /**
     * Gets the underlying Playwright {@link Browser}.
     *
     * @return The raw Playwright browser object.
     */
    Browser getBrowser();

    /**
     * Retrieves all windows of this browser that are currently open, in no particular order.
     * A window can be kept in a variable and passed to {@link #switchToWindow(IBrowserWindow)} later.
     *
     * @return A list of {@link IBrowserWindow}s; empty if no window is open.
     */
    List<IBrowserWindow> getBrowserWindows();

    /**
     * Retrieves the currently active browser window.
     *
     * @return The current {@link IBrowserWindow}, or {@code null} if no window is open.
     */
    IBrowserWindow getCurrentBrowserWindow();

    /**
     * Opens a new browser window (a Playwright browser context) with one tab, and makes it current.
     * <p>
     * If {@link IBrowserProperties#getStorageStatePath()} is configured, the new window is preloaded with
     * that saved storage state. To load a specific state file for one window without changing the global
     * configuration, use {@link #openNewWindow(Path)}.
     *
     * @return The newly opened {@link IBrowserWindow}.
     */
    IBrowserWindow openNewWindow();

    /**
     * Opens a new browser window with one tab, preloaded with a saved storage state (cookies and local
     * storage) from the given file, and makes it current.
     * <p>
     * This overrides {@link IBrowserProperties#getStorageStatePath()} for this window only, so different
     * windows in the same test can each start already signed in as a different user. Pairs with
     * {@link IBrowserWindow#saveStorageState(Path)}, which produces the file this method consumes.
     *
     * @param storageStatePath the file path to a previously saved Playwright storage-state JSON file
     * @return The newly opened {@link IBrowserWindow}, already authenticated per the saved state.
     */
    IBrowserWindow openNewWindow(Path storageStatePath);

    /**
     * Closes the whole browser: all its windows and tabs, and the Playwright driver process.
     */
    void close();

    /**
     * Checks whether the browser is no longer usable: it was closed, or it disconnected (crashed).
     *
     * @return {@code true} if the browser is closed or disconnected, {@code false} otherwise.
     */
    boolean isClosed();

    /**
     * Switches to the given window, making it current and bringing its current tab to the front.
     * <p>
     * A window obtained from {@link #getBrowserWindows()} starts on its newest tab: only the current window
     * remembers which of its tabs was used. To return to a specific tab, keep it in a variable and use
     * {@link IBrowserWindow#switchToTab(IBrowserTab)}.
     *
     * @param window The {@link IBrowserWindow} to switch to.
     */
    void switchToWindow(IBrowserWindow window);

    /**
     * Closes the given window together with all its tabs. If it was the current window, another open
     * window (if any) becomes current.
     *
     * @param window The {@link IBrowserWindow} to close.
     */
    void closeWindow(IBrowserWindow window);

    /**
     * Retrieves the currently active browser window, failing fast with a descriptive error if none is open.
     *
     * @return The current {@link IBrowserWindow}, never {@code null}.
     * @throws IllegalStateException if no window is open (every window of this browser has been closed).
     */
    default IBrowserWindow requireCurrentBrowserWindow() {
        IBrowserWindow window = getCurrentBrowserWindow();
        if (window == null) {
            throw new IllegalStateException(
                    "No browser window is open. Call IBrowserService.getBrowser() or IBrowser.openNewWindow() first.");
        }
        return window;
    }

    /**
     * Retrieves the currently active tab of the current window, failing fast with a descriptive error
     * if there is no window or tab open.
     *
     * @return The current {@link IBrowserTab}, never {@code null}.
     * @throws IllegalStateException if no window or no tab is open.
     */
    default IBrowserTab requireCurrentBrowserTab() {
        IBrowserTab tab = requireCurrentBrowserWindow().getCurrentBrowserTab();
        if (tab == null) {
            throw new IllegalStateException(
                    "No browser tab is open in the current window. "
                            + "Call IBrowserService.getBrowser() or IBrowserWindow.openNewTab() first.");
        }
        return tab;
    }

    /**
     * Navigates the current tab to the given URL.
     *
     * @param url The target URL.
     */
    default void navigateTo(String url) {
        requireCurrentBrowserTab().navigateTo(url);
    }

    /**
     * Takes a full-page screenshot of the current tab.
     *
     * @return A byte array representing the captured PNG image.
     */
    default byte[] takeScreenshot() {
        return requireCurrentBrowserTab().takeScreenshot();
    }

    /**
     * Resizes the viewport of the current tab.
     *
     * @param width  The new viewport width in pixels.
     * @param height The new viewport height in pixels.
     */
    default void setViewportSize(int width, int height) {
        requireCurrentBrowserTab().setViewportSize(width, height);
    }

    /**
     * Closes the current window, with all its tabs.
     */
    default void closeWindow() {
        closeWindow(requireCurrentBrowserWindow());
    }
}