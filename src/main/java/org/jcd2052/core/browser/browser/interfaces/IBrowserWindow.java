package org.jcd2052.core.browser.browser.interfaces;

import com.microsoft.playwright.BrowserContext;

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
}