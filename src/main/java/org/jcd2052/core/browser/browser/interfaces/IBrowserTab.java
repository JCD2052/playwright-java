package org.jcd2052.core.browser.browser.interfaces;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;

import java.nio.file.Path;

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
     * @return The Local Storage manager for the current tab.
     */
    IStorageManager getLocalStorage();

    /**
     * @return The Session Storage manager for the current tab.
     */
    IStorageManager getSessionStorage();

    /**
     * Retrieves the current URL of the active tab.
     *
     * @return The current URL as a string.
     */
    default String getCurrentUrl() {
        return getPage().url();
    }

    default void saveFileAfterAction(Runnable actionThatTriggersDownload, Path pathToSave) {
        waitForDownload(actionThatTriggersDownload).saveAs(pathToSave);
    }

    /**
     * Waits for a file download to be triggered by the provided action.
     *
     * @param actionThatTriggersDownload The action (e.g., clicking a button) that initiates the download.
     * @return A Playwright Download object containing the downloaded file metadata.
     */
    default Download waitForDownload(Runnable actionThatTriggersDownload) {
        return getPage().waitForDownload(actionThatTriggersDownload);
    }

    /**
     * Dynamically resizes the browser viewport during test execution.
     * Useful for testing responsive UI layouts on the fly.
     *
     * @param width  The new viewport width in pixels.
     * @param height The new viewport height in pixels.
     */
    default void setViewportSize(int width, int height) {
        getPage().setViewportSize(width, height);
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