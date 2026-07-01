package org.jcd2052.core.browser.browser.interfaces;

import com.microsoft.playwright.Browser;
import org.jcd2052.core.browser.browser.BrowserWindow;

import java.util.Collection;

/**
 * Represents a web browser instance managed by the framework.
 * Provides functionality to manage windows (contexts), take screenshots, and handle the browser lifecycle.
 */
public interface IBrowser {
    /**
     * Opens a new browser window (BrowserContext in Playwright) and sets it as the current active window.
     *
     * @return The newly created {@link IBrowserWindow}.
     */
    IBrowserWindow openNewWindow();

    /**
     * Retrieves all currently open browser windows associated with this browser instance.
     *
     * @return A collection of {@link IBrowserWindow} objects representing the open windows.
     */
    Collection<IBrowserWindow> getBrowserWindows();

    /**
     * Retrieves the name of the browser type (e.g., "chromium", "firefox", "webkit").
     *
     * @return The browser name as a string.
     */
    String getBrowserName();

    /**
     * Takes a screenshot of the currently active browser tab within the current window.
     *
     * @return A byte array representing the captured screenshot image.
     */
    byte[] takeScreenshot();

    /**
     * Gets the underlying Playwright {@link Browser} instance.
     *
     * @return The raw Playwright browser object.
     */
    Browser getBrowser();

    /**
     * Retrieves the currently active browser window.
     *
     * @return The current {@link IBrowserWindow}.
     */
    IBrowserWindow getCurrentBrowserWindow();

    /**
     * Checks whether the browser instance has been closed.
     *
     * @return {@code true} if the browser is closed, {@code false} otherwise.
     */
    boolean isClosed();

    /**
     * Sets the specified browser window as the currently active window.
     *
     * @param currentBrowserWindow The {@link IBrowserWindow} to set as active.
     */
    void setCurrentBrowserWindow(IBrowserWindow currentBrowserWindow);

    /**
     * Closes the browser instance, along with all its associated windows and tabs.
     */
    void close();

    /**
     * Switches the active browser context to the specified window and brings its current tab to the front.
     *
     * @param window The {@link BrowserWindow} to switch to.
     */
    void switchToWindow(IBrowserWindow window);

    /**
     * Reloads the currently active tab in the current browser window.
     */
    default void reload() {
        getCurrentBrowserWindow().getCurrentBrowserTab().reloadTab();
    }

    default void closeWindow() {
        getCurrentBrowserWindow().closeWindow();
    }
}