package org.jcd2052.core.browser.browser.interfaces;

import com.microsoft.playwright.Page;

/**
 * Represents a single tab (or Page in Playwright terminology) within a browser window.
 * Provides default implementations for basic browser navigation and tab-level interactions.
 */
public interface IBrowserTab {
    /**
     * Gets the underlying Playwright {@link Page} instance associated with this tab.
     *
     * @return The raw Playwright page object.
     */
    Page getPage();

    IAlert getAlert();

    /**
     * Retrieves the current URL of the active tab.
     *
     * @return The current URL as a string.
     */
    default String getCurrentUrl() {
        return getPage().url();
    }

    /**
     * Retrieves the title of the current page.
     *
     * @return The title of the page as a string.
     */
    default String getTitle() {
        return getPage().title();
    }

    /**
     * Navigates the tab forward to the next page in the browser history.
     */
    default void goForward() {
        getPage().goForward();
    }

    /**
     * Navigates the tab backward to the previous page in the browser history.
     */
    default void goBack() {
        getPage().goBack();
    }

    /**
     * Reloads (refreshes) the current page in the tab.
     */
    default void reloadTab() {
        getPage().reload();
        getPage().waitForLoadState();
    }

    /**
     * Closes this specific browser tab.
     */
    default void closeTab() {
        getPage().close();
    }

    /**
     * Navigates the tab to the specified URL.
     *
     * @param url The target URL string to navigate to.
     */
    default void navigateTo(String url) {
        getPage().navigate(url);
    }
}