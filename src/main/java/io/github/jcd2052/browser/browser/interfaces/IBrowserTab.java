package io.github.jcd2052.browser.browser.interfaces;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Represents a single tab (a Playwright {@link Page}) within a browser window.
 * <p>
 * All page-level actions are performed on a tab: navigation and history, screenshots, viewport size, raw
 * mouse and keyboard input, dialogs, downloads, web storage, and the clipboard. Element interactions go
 * through the framework's elements instead, which always act on the current tab.
 * </p>
 */
public interface IBrowserTab {
    /**
     * Gets the underlying Playwright {@link Page}, for anything this interface doesn't cover.
     *
     * @return The raw Playwright page object.
     */
    Page getPage();

    /**
     * Gets a manager for this tab's {@code localStorage}, scoped to the origin of the loaded page.
     *
     * @return The local storage manager.
     */
    IStorageManager getLocalStorage();

    /**
     * Gets a manager for this tab's {@code sessionStorage}, scoped to the origin of the loaded page.
     *
     * @return The session storage manager.
     */
    IStorageManager getSessionStorage();

    /**
     * Gets raw, coordinate-based mouse controls for this tab.
     *
     * @return The mouse actions.
     */
    IMouseActions getMouseActions();

    /**
     * Gets raw keyboard controls for this tab, which send keys to the focused element.
     *
     * @return The keyboard actions.
     */
    IKeyboardActions getKeyboardActions();

    /**
     * Takes a full-page screenshot of this tab.
     *
     * @return A byte array representing the captured PNG image.
     */
    default byte[] takeScreenshot() {
        return getPage().screenshot(new Page.ScreenshotOptions().setFullPage(true));
    }

    /**
     * Runs a JavaScript expression or function in this tab's page and returns its result, for page-level scripts
     * such as reading {@code document.cookie} or calling {@code window.open(...)}. Scripts on one element go
     * through that element's {@code getJsActions()} instead.
     *
     * @param script A JavaScript expression (e.g. {@code "document.title"}) or function
     *               (e.g. {@code "() => window.scrollY"}); a function returning a promise is awaited.
     * @return The result converted to Java ({@code String}, {@code Number}, {@code Boolean}, {@code List},
     * {@code Map}), or {@code null} for {@code undefined} and {@code null}.
     */
    default Object evaluate(String script) {
        return getPage().evaluate(script);
    }

    /**
     * Reads the current text of the system clipboard through the page.
     * <p>
     * The window needs clipboard-read permission (e.g. {@code clipboard-read} in
     * {@link io.github.jcd2052.browser.configuration.IBrowserProperties#getPermissions()}).
     * </p>
     *
     * @return The clipboard text, or an empty string if there is none.
     */
    String readClipboard();

    /**
     * Runs an action that opens a dialog ({@code alert}, {@code confirm} or {@code prompt}), handles that
     * dialog, and returns it, so its message and other values can be read afterwards:
     * <pre>{@code
     * String message = tab.handleNextAlert(IAlert::accept, saveButton::click).getMessage();
     * }</pre>
     * <p>
     * Waits, up to the window's default timeout, for the dialog to open, so dialogs opened shortly after the
     * action (e.g. from a timer) are handled too. The handler must accept or decline the dialog; otherwise the
     * page stays blocked by it.
     * </p>
     *
     * @param alertHandler            What to do with the dialog, e.g. {@link IAlert#accept()}.
     * @param actionThatTriggersAlert The action (e.g. clicking a button) that opens the dialog.
     * @return The dialog that was handled.
     * @throws com.microsoft.playwright.TimeoutError if no dialog opens in time; the handler is then removed, so
     *                                               it doesn't catch a later dialog.
     */
    IAlert handleNextAlert(Consumer<IAlert> alertHandler, Runnable actionThatTriggersAlert);

    /**
     * Gets the URL of the page loaded in this tab.
     *
     * @return The current URL.
     */
    default String getCurrentUrl() {
        return getPage().url();
    }

    /**
     * Runs an action that starts a file download, waits for the download, and saves the file.
     *
     * @param actionThatTriggersDownload The action (e.g. clicking a link) that starts the download.
     * @param pathToSave                 Where to save the downloaded file.
     */
    default void saveFileAfterAction(Runnable actionThatTriggersDownload, Path pathToSave) {
        waitForDownload(actionThatTriggersDownload).saveAs(pathToSave);
    }

    /**
     * Runs an action that starts a file download and waits for the download to begin.
     *
     * @param actionThatTriggersDownload The action (e.g. clicking a link) that starts the download.
     * @return The Playwright {@link Download}, to read its file name or save it.
     */
    default Download waitForDownload(Runnable actionThatTriggersDownload) {
        return getPage().waitForDownload(actionThatTriggersDownload);
    }

    /**
     * Resizes this tab's viewport, e.g. to check a responsive layout.
     *
     * @param width  The new viewport width in pixels.
     * @param height The new viewport height in pixels.
     */
    default void setViewportSize(int width, int height) {
        getPage().setViewportSize(width, height);
    }

    /**
     * Gets the title of the page loaded in this tab.
     *
     * @return The page title.
     */
    default String getTitle() {
        return getPage().title();
    }

    /**
     * Navigates forward to the next page in this tab's history.
     */
    default void goForward() {
        getPage().goForward();
    }

    /**
     * Navigates back to the previous page in this tab's history.
     */
    default void goBack() {
        getPage().goBack();
    }

    /**
     * Reloads the page in this tab and waits for it to finish loading.
     */
    default void reloadTab() {
        getPage().reload();
        getPage().waitForLoadState();
    }

    /**
     * Closes this tab. If it was its window's current tab, the window's newest remaining tab becomes current.
     */
    default void closeTab() {
        getPage().close();
    }

    /**
     * Navigates this tab to the given URL.
     *
     * @param url The target URL.
     */
    default void navigateTo(String url) {
        getPage().navigate(url);
    }

    /**
     * Waits until the page has had no network connections for at least 500 ms.
     * <p>
     * Playwright discourages relying on this for test logic, since some pages never go idle; waiting for a
     * specific element or condition is usually more reliable.
     * </p>
     */
    default void waitForNetworkIdle() {
        getPage().waitForLoadState(LoadState.NETWORKIDLE);
    }
}