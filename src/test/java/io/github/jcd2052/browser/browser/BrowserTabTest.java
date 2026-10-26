package io.github.jcd2052.browser.browser;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import io.github.jcd2052.browser.browser.interfaces.IBrowserTab;
import io.github.jcd2052.browser.browser.interfaces.IBrowserWindow;
import io.github.jcd2052.browser.browser.interfaces.IKeyboardActions;
import io.github.jcd2052.browser.browser.interfaces.IMouseActions;
import io.github.jcd2052.browser.browser.interfaces.IStorageManager;
import io.github.jcd2052.browser.configuration.BrowserProperties;
import io.github.jcd2052.browser.factory.BrowserFactory;
import io.github.jcd2052.browser.launcher.BrowserLauncherRegistry;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Verifies {@link BrowserTab}, through {@link IBrowserTab}, against a real headless Chromium.
 * <p>
 * Pages are served for the made-up origin {@link #ORIGIN} by a Playwright route (see {@link #RESPONSES}), so no
 * network access is needed; web storage and the clipboard need such a real (and secure) origin.
 * </p>
 */
public class BrowserTabTest {
    /**
     * The origin every test page belongs to.
     */
    private static final String ORIGIN = "https://tab.test";

    /**
     * The content of the file served as a download.
     */
    private static final String REPORT = "report body";

    /**
     * What the route answers for each path of {@link #ORIGIN}: two plain pages, a page that loads data a moment
     * after it has loaded, that data, and a file served as a download.
     */
    private static final Map<String, Route.FulfillOptions> RESPONSES = Map.of(
            "/", html("<title>Home</title><a id='download' href='/report.txt'>report</a>"),
            "/second", html("<title>Second</title>"),
            "/idle", html("<p id='result'></p><script>setTimeout(() => fetch('/data')"
                    + ".then(response => response.text()).then(text => result.textContent = text), 100);</script>"),
            "/data", new Route.FulfillOptions().setContentType("text/plain").setBody("loaded"),
            "/report.txt", new Route.FulfillOptions()
                    .setContentType("text/plain")
                    .setHeaders(Map.of("Content-Disposition", "attachment; filename=report.txt"))
                    .setBody(REPORT));

    /**
     * A page whose {@code #target} box covers the top-left 200 x 100 pixels and records the mouse events it gets:
     * button events in {@code window.events}, moves in {@code window.moves}. The page is tall enough to scroll.
     */
    private static final String MOUSE_PAGE = "<body style='margin:0; height:3000px'>"
            + "<div id='target' style='width:200px; height:100px'></div>"
            + "<script>window.events = []; window.moves = 0;"
            + " for (const type of ['mousedown', 'mouseup', 'click', 'dblclick'])"
            + " target.addEventListener(type, () => events.push(type));"
            + " target.addEventListener('mousemove', () => moves++);</script></body>";

    /**
     * A page with a text field that counts its {@code keydown} events in {@code window.keydowns}.
     */
    private static final String KEYBOARD_PAGE = "<input id='field'>"
            + "<script>window.keydowns = 0; field.addEventListener('keydown', () => keydowns++);</script>";

    /**
     * The first bytes of every PNG file, used to check that a screenshot is a real image.
     */
    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 'P', 'N', 'G'};

    private PlaywrightBrowser browser;
    private IBrowserWindow window;
    private IBrowserTab tab;

    /**
     * Launches a fresh headless Chromium for each test, with clipboard access, opens a window whose pages of
     * {@link #ORIGIN} load locally, and loads the home page in its tab.
     */
    @BeforeMethod
    public void openHomePage() {
        BrowserProperties properties = new BrowserProperties()
                .setName("chromium")
                .setHeadless(true)
                .setTimeout(10000L)
                .setPageLoadTimeout(10000L)
                .setWidth(800)
                .setHeight(600)
                .setPermissions(List.of("clipboard-read", "clipboard-write"));
        Playwright playwright = Playwright.create();
        browser = new PlaywrightBrowser(
                playwright,
                new BrowserFactory<>(properties, new BrowserLauncherRegistry()).createBrowser(playwright),
                properties);
        window = browser.openNewWindow();
        window.getBrowserContext().route(ORIGIN + "/**", BrowserTabTest::respond);
        tab = window.getCurrentBrowserTab();
        tab.navigateTo(ORIGIN + "/");
    }

    /**
     * Closes the whole browser and its Playwright driver after each test.
     */
    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        browser.close();
    }

    /**
     * Verifies that navigating loads the page, whose URL and title the tab then reports.
     */
    @Test(description = "navigateTo loads the page and the tab reports its URL and title")
    public void navigateToLoadsThePageAndTheTabReportsItsUrlAndTitle() {
        tab.navigateTo(ORIGIN + "/second");

        Assert.assertEquals(tab.getCurrentUrl(), ORIGIN + "/second");
        Assert.assertEquals(tab.getTitle(), "Second");
    }

    /**
     * Verifies that going back and forward moves through the tab's history.
     */
    @Test(description = "goBack and goForward move through the tab's history")
    public void goBackAndGoForwardMoveThroughTheTabsHistory() {
        tab.navigateTo(ORIGIN + "/second");

        tab.goBack();
        Assert.assertEquals(tab.getCurrentUrl(), ORIGIN + "/");
        Assert.assertEquals(tab.getTitle(), "Home");

        tab.goForward();
        Assert.assertEquals(tab.getCurrentUrl(), ORIGIN + "/second");
        Assert.assertEquals(tab.getTitle(), "Second");
    }

    /**
     * Verifies that reloading loads the same page afresh, dropping the state the old page had.
     */
    @Test(description = "reloadTab loads the same page afresh")
    public void reloadTabLoadsTheSamePageAfresh() {
        tab.evaluate("window.marker = 'set'");

        tab.reloadTab();

        Assert.assertEquals(tab.getCurrentUrl(), ORIGIN + "/");
        Assert.assertNull(tab.evaluate("window.marker"));
    }

    /**
     * Verifies that scripts run in the page and their results are converted to Java: expressions, functions,
     * awaited promises, objects and arrays, and {@code undefined}.
     */
    @Test(description = "evaluate runs a script in the page and converts its result")
    public void evaluateRunsAScriptInThePageAndConvertsItsResult() {
        Assert.assertEquals(tab.evaluate("document.title"), "Home");
        Assert.assertEquals(tab.evaluate("() => 6 * 7"), 42);
        Assert.assertEquals(tab.evaluate("async () => 'later'"), "later");
        Assert.assertEquals(tab.evaluate("({name: 'tab', tags: ['a', 'b']})"),
                Map.of("name", "tab", "tags", List.of("a", "b")));
        Assert.assertNull(tab.evaluate("undefined"));
    }

    /**
     * Verifies that resizing the viewport changes the size the page sees.
     */
    @Test(description = "setViewportSize resizes the viewport the page sees")
    public void setViewportSizeResizesTheViewportThePageSees() {
        tab.setViewportSize(500, 400);

        Assert.assertEquals(tab.evaluate("[window.innerWidth, window.innerHeight]"), List.of(500, 400));
    }

    /**
     * Verifies that a screenshot is a PNG of the whole page, not just of the visible part.
     */
    @Test(description = "takeScreenshot captures the whole page as a PNG")
    public void takeScreenshotCapturesTheWholePageAsAPng() {
        tab.getPage().setContent("<body style='margin:0'><div style='height:2000px'></div></body>");

        byte[] screenshot = tab.takeScreenshot();

        for (int i = 0; i < PNG_SIGNATURE.length; i++) {
            Assert.assertEquals(screenshot[i], PNG_SIGNATURE[i]);
        }
        Assert.assertEquals(pngHeight(screenshot), 2000);
    }

    /**
     * Verifies that local storage items can be set, read, removed and cleared, and that a missing item is
     * {@code null}.
     */
    @Test(description = "Local storage items can be set, read, removed and cleared")
    public void localStorageItemsCanBeSetReadRemovedAndCleared() {
        IStorageManager storage = tab.getLocalStorage();
        storage.setItem("theme", "dark");
        storage.setItem("language", "en");

        Assert.assertEquals(storage.getItem("theme"), "dark");
        Assert.assertNull(storage.getItem("missing"));

        storage.removeItem("theme");
        Assert.assertNull(storage.getItem("theme"));
        Assert.assertEquals(storage.getItem("language"), "en");

        storage.clear();
        Assert.assertNull(storage.getItem("language"));
    }

    /**
     * Verifies that local and session storage are separate, that local storage is shared by the window's tabs
     * on the same origin, and that session storage belongs to its tab only.
     */
    @Test(description = "Local storage is shared by the window's tabs, session storage is not")
    public void localStorageIsSharedByTheWindowsTabsSessionStorageIsNot() {
        tab.getLocalStorage().setItem("theme", "dark");
        tab.getSessionStorage().setItem("draft", "hello");

        Assert.assertNull(tab.getSessionStorage().getItem("theme"));
        Assert.assertNull(tab.getLocalStorage().getItem("draft"));

        IBrowserTab otherTab = window.openNewTab();
        otherTab.navigateTo(ORIGIN + "/");

        Assert.assertEquals(otherTab.getLocalStorage().getItem("theme"), "dark");
        Assert.assertNull(otherTab.getSessionStorage().getItem("draft"));
    }

    /**
     * Verifies that mouse clicks and double-clicks land at the given position: on the target box they fire its
     * button events, outside it they don't.
     */
    @Test(description = "Mouse clicks and double-clicks land at the given position")
    public void mouseClicksAndDoubleClicksLandAtTheGivenPosition() {
        tab.getPage().setContent(MOUSE_PAGE);
        IMouseActions mouse = tab.getMouseActions();

        mouse.click(300, 300);
        Assert.assertEquals(tab.evaluate("window.events"), List.of());

        mouse.click(50, 50);
        Assert.assertEquals(tab.evaluate("window.events"), List.of("mousedown", "mouseup", "click"));

        tab.evaluate("window.events = []");
        mouse.doubleClick(50, 50);
        Assert.assertEquals(tab.evaluate("window.events"),
                List.of("mousedown", "mouseup", "click", "mousedown", "mouseup", "click", "dblclick"));
    }

    /**
     * Verifies a drag made of raw mouse actions: pressing, moving in steps (one {@code mousemove} per step), and
     * releasing.
     */
    @Test(description = "Mouse down, stepped move and up make a drag")
    public void mouseDownSteppedMoveAndUpMakeADrag() {
        tab.getPage().setContent(MOUSE_PAGE);
        IMouseActions mouse = tab.getMouseActions();

        mouse.move(10, 10);
        mouse.down();
        mouse.move(150, 80, 5);
        mouse.up();

        Assert.assertEquals(tab.evaluate("window.moves"), 6);
        Assert.assertEquals(tab.evaluate("window.events"), List.of("mousedown", "mouseup", "click"));
    }

    /**
     * Verifies that the mouse wheel scrolls the page by the given amount.
     */
    @Test(description = "The mouse wheel scrolls the page")
    public void theMouseWheelScrollsThePage() {
        tab.getPage().setContent(MOUSE_PAGE);

        tab.getMouseActions().move(50, 50);
        tab.getMouseActions().wheel(0, 300);

        tab.getPage().waitForFunction("() => window.scrollY === 300");
        Assert.assertEquals(tab.evaluate("window.scrollX"), 0);
    }

    /**
     * Verifies that typing sends the text key by key to the focused field, and that a pressed key acts on it.
     */
    @Test(description = "The keyboard types text and presses keys in the focused field")
    public void theKeyboardTypesTextAndPressesKeysInTheFocusedField() {
        openKeyboardPage();

        tab.getKeyboardActions().type("abc");
        tab.getKeyboardActions().press("Backspace");

        Assert.assertEquals(tab.evaluate("field.value"), "ab");
        Assert.assertEquals(tab.evaluate("window.keydowns"), 4);
    }

    /**
     * Verifies that inserted text reaches the focused field without any key events.
     */
    @Test(description = "insertText fills the focused field without key events")
    public void insertTextFillsTheFocusedFieldWithoutKeyEvents() {
        openKeyboardPage();

        tab.getKeyboardActions().insertText("hello");

        Assert.assertEquals(tab.evaluate("field.value"), "hello");
        Assert.assertEquals(tab.evaluate("window.keydowns"), 0);
    }

    /**
     * Verifies that a key held down modifies the keys pressed until it is released.
     */
    @Test(description = "A key held down modifies the keys pressed until it is released")
    public void aKeyHeldDownModifiesTheKeysPressedUntilItIsReleased() {
        openKeyboardPage();
        IKeyboardActions keyboard = tab.getKeyboardActions();

        keyboard.down("Shift");
        keyboard.press("KeyA");
        keyboard.up("Shift");
        keyboard.press("KeyA");

        Assert.assertEquals(tab.evaluate("field.value"), "Aa");
    }

    /**
     * Verifies that the clipboard can be read when the window has clipboard permissions.
     */
    @Test(description = "readClipboard returns the copied text")
    public void readClipboardReturnsTheCopiedText() {
        tab.evaluate("async () => await navigator.clipboard.writeText('copied text')");

        Assert.assertEquals(tab.readClipboard(), "copied text");
    }

    /**
     * Verifies that a download started by an action is waited for and reports its file name.
     */
    @Test(description = "waitForDownload returns the download started by the action")
    public void waitForDownloadReturnsTheDownloadStartedByTheAction() {
        Download download = tab.waitForDownload(() -> tab.getPage().click("#download"));

        Assert.assertEquals(download.suggestedFilename(), "report.txt");
    }

    /**
     * Verifies that a download started by an action is saved to the given path.
     */
    @Test(description = "saveFileAfterAction saves the downloaded file")
    public void saveFileAfterActionSavesTheDownloadedFile() throws IOException {
        Path directory = Files.createTempDirectory("browser-tab-test");
        Path target = directory.resolve("report.txt");
        try {
            tab.saveFileAfterAction(() -> tab.getPage().click("#download"), target);

            Assert.assertEquals(Files.readString(target), REPORT);
        } finally {
            Files.deleteIfExists(target);
            Files.delete(directory);
        }
    }

    /**
     * Verifies that waiting for network idle waits for requests a page makes after it has loaded.
     */
    @Test(description = "waitForNetworkIdle waits for requests made after the page loaded")
    public void waitForNetworkIdleWaitsForRequestsMadeAfterThePageLoaded() {
        tab.navigateTo(ORIGIN + "/idle");

        tab.waitForNetworkIdle();

        Assert.assertEquals(tab.evaluate("result.textContent"), "loaded");
    }

    /**
     * Verifies that closing a tab closes its page and removes it from its window, whose remaining tab becomes
     * current.
     */
    @Test(description = "closeTab closes the page and removes it from the window")
    public void closeTabClosesThePageAndRemovesItFromTheWindow() {
        IBrowserTab otherTab = window.openNewTab();

        otherTab.closeTab();

        Assert.assertTrue(otherTab.getPage().isClosed());
        Assert.assertEquals(window.getBrowserTabs(), List.of(tab));
        Assert.assertEquals(window.getCurrentBrowserTab(), tab);
    }

    /**
     * Loads {@link #KEYBOARD_PAGE} and focuses its field, so the keyboard types into it.
     */
    private void openKeyboardPage() {
        tab.getPage().setContent(KEYBOARD_PAGE);
        tab.getPage().focus("#field");
    }

    /**
     * Answers a request to {@link #ORIGIN} with its response from {@link #RESPONSES}, or a 404.
     */
    private static void respond(Route route) {
        Route.FulfillOptions response = RESPONSES.get(URI.create(route.request().url()).getPath());
        route.fulfill(response != null ? response : new Route.FulfillOptions().setStatus(404));
    }

    /**
     * Creates an HTML response with the given body.
     */
    private static Route.FulfillOptions html(String body) {
        return new Route.FulfillOptions().setContentType("text/html").setBody(body);
    }

    /**
     * Reads the image height from a PNG's header.
     */
    private static int pngHeight(byte[] png) {
        return ByteBuffer.wrap(png, 20, 4).getInt();
    }
}
