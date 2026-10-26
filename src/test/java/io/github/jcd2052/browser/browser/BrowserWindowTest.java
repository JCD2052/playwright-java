package io.github.jcd2052.browser.browser;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.github.jcd2052.browser.browser.interfaces.IBrowserTab;
import io.github.jcd2052.browser.browser.interfaces.IBrowserWindow;
import io.github.jcd2052.browser.configuration.BrowserProperties;
import io.github.jcd2052.browser.factory.BrowserFactory;
import io.github.jcd2052.browser.launcher.BrowserLauncherRegistry;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Verifies the browser → window → tab model of {@link PlaywrightBrowser}, {@link BrowserWindow} and
 * {@link BrowserTab} against a real headless Chromium.
 */
public class BrowserWindowTest {
    private PlaywrightBrowser browser;

    /**
     * Launches a fresh headless Chromium for each test and wraps it, without opening any window.
     */
    @BeforeMethod
    public void launchBrowser() {
        BrowserProperties properties = new BrowserProperties()
                .setName("chromium")
                .setHeadless(true)
                .setTimeout(10000L)
                .setPageLoadTimeout(10000L)
                .setWidth(800)
                .setHeight(600);
        Playwright playwright = Playwright.create();
        browser = new PlaywrightBrowser(
                playwright,
                new BrowserFactory<>(properties, new BrowserLauncherRegistry()).createBrowser(playwright),
                properties);
    }

    /**
     * Closes the whole browser and its Playwright driver after each test.
     */
    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        browser.close();
    }

    /**
     * Verifies that a freshly launched browser has no windows and no current window, and is not closed.
     */
    @Test(description = "A launched browser has no windows until one is opened")
    public void aLaunchedBrowserHasNoWindowsUntilOneIsOpened() {
        Assert.assertTrue(browser.getBrowserWindows().isEmpty());
        Assert.assertNull(browser.getCurrentBrowserWindow());
        Assert.assertFalse(browser.isClosed());
    }

    /**
     * Verifies that a new window becomes the current window and comes with exactly one tab, which is its current tab.
     */
    @Test(description = "openNewWindow opens a window with one tab and makes it current")
    public void openNewWindowOpensAWindowWithOneTabAndMakesItCurrent() {
        IBrowserWindow window = browser.openNewWindow();

        Assert.assertSame(browser.getCurrentBrowserWindow(), window);
        Assert.assertEquals(window.getBrowserTabs().size(), 1);
        Assert.assertEquals(window.getCurrentBrowserTab(), window.getBrowserTabs().get(0));
    }

    /**
     * Verifies that every open window is listed, and that the current window is returned as the same instance
     * (so it keeps its current tab).
     */
    @Test(description = "getBrowserWindows lists every open window and keeps the current instance")
    public void getBrowserWindowsListsEveryOpenWindowAndKeepsTheCurrentInstance() {
        IBrowserWindow first = browser.openNewWindow();
        IBrowserWindow second = browser.openNewWindow();

        List<IBrowserWindow> windows = browser.getBrowserWindows();

        Assert.assertEquals(windows.size(), 2);
        Assert.assertTrue(windows.contains(first));
        Assert.assertTrue(windows.contains(second));
        Assert.assertTrue(windows.stream().anyMatch(window -> window == second));
    }

    /**
     * Verifies that switching back to a window kept in a variable makes it current again, on the tab it was on.
     */
    @Test(description = "switchToWindow makes a kept window current with the tab it was on")
    public void switchToWindowMakesAKeptWindowCurrentWithTheTabItWasOn() {
        IBrowserWindow first = browser.openNewWindow();
        IBrowserTab firstTab = first.getCurrentBrowserTab();
        first.openNewTab();
        first.switchToTab(firstTab);
        browser.openNewWindow();

        browser.switchToWindow(first);

        Assert.assertSame(browser.getCurrentBrowserWindow(), first);
        Assert.assertEquals(browser.requireCurrentBrowserTab(), firstTab);
    }

    /**
     * Verifies that a window obtained from {@link PlaywrightBrowser#getBrowserWindows()} (other than the current one)
     * does not remember its last-used tab and starts on its newest tab.
     */
    @Test(description = "A window taken from the list starts on its newest tab")
    public void aWindowTakenFromTheListStartsOnItsNewestTab() {
        IBrowserWindow first = browser.openNewWindow();
        IBrowserTab firstTab = first.getCurrentBrowserTab();
        IBrowserTab newestTab = first.openNewTab();
        first.switchToTab(firstTab);
        browser.openNewWindow();

        IBrowserWindow listed = browser.getBrowserWindows().stream()
                .filter(first::equals)
                .findFirst()
                .orElseThrow();

        Assert.assertEquals(listed.getCurrentBrowserTab(), newestTab);
    }

    /**
     * Verifies that closing a window closes its tabs, that another open window becomes current, and that closing
     * the last window leaves no current window.
     */
    @Test(description = "closeWindow closes its tabs and another open window becomes current")
    public void closeWindowClosesItsTabsAndAnotherOpenWindowBecomesCurrent() {
        IBrowserWindow first = browser.openNewWindow();
        IBrowserWindow second = browser.openNewWindow();
        Page secondWindowPage = second.getCurrentBrowserTab().getPage();

        browser.closeWindow(second);

        Assert.assertTrue(secondWindowPage.isClosed());
        Assert.assertEquals(browser.getBrowserWindows(), List.of(first));
        Assert.assertEquals(browser.getCurrentBrowserWindow(), first);

        browser.closeWindow();
        Assert.assertTrue(browser.getBrowserWindows().isEmpty());
        Assert.assertNull(browser.getCurrentBrowserWindow());
    }

    /**
     * Verifies that closing the browser closes it completely, even with several windows still open.
     */
    @Test(description = "close closes the whole browser whatever windows are open")
    public void closeClosesTheWholeBrowserWhateverWindowsAreOpen() {
        browser.openNewWindow();
        browser.openNewWindow();

        browser.close();

        Assert.assertTrue(browser.isClosed());
        Assert.assertNull(browser.getCurrentBrowserWindow());
    }

    /**
     * Verifies that a new tab becomes the window's current tab, that tabs are listed oldest first, and that
     * switching to a tab makes it current.
     */
    @Test(description = "A window opens, switches and lists its tabs oldest first")
    public void windowOpensSwitchesAndListsItsTabsOldestFirst() {
        IBrowserWindow window = browser.openNewWindow();
        IBrowserTab first = window.getCurrentBrowserTab();
        IBrowserTab second = window.openNewTab();

        Assert.assertEquals(window.getBrowserTabs(), List.of(first, second));
        Assert.assertEquals(window.getCurrentBrowserTab(), second);

        window.switchToTab(first);
        Assert.assertEquals(window.getCurrentBrowserTab(), first);
    }

    /**
     * Verifies that closing the current tab makes the window's newest remaining tab current, and that closing
     * every tab leaves no current tab.
     */
    @Test(description = "closeTab falls back to the newest remaining tab")
    public void closeTabFallsBackToTheNewestRemainingTab() {
        IBrowserWindow window = browser.openNewWindow();
        IBrowserTab first = window.getCurrentBrowserTab();
        IBrowserTab second = window.openNewTab();
        IBrowserTab third = window.openNewTab();
        window.switchToTab(first);

        window.closeTab(first);
        Assert.assertEquals(window.getBrowserTabs(), List.of(second, third));
        Assert.assertEquals(window.getCurrentBrowserTab(), third);

        window.closeTab(third);
        window.closeTab(second);
        Assert.assertTrue(window.getBrowserTabs().isEmpty());
        Assert.assertNull(window.getCurrentBrowserTab());
    }

    /**
     * Verifies that a popup opened by the page appears in the window's tab list.
     */
    @Test(description = "getBrowserTabs includes popups opened by the page")
    public void getBrowserTabsIncludesPopupsOpenedByThePage() {
        IBrowserWindow window = browser.openNewWindow();
        IBrowserTab tab = window.getCurrentBrowserTab();

        Page popup = tab.getPage().waitForPopup(() -> tab.evaluate("window.open('about:blank')"));

        Assert.assertEquals(window.getBrowserTabs(), List.of(tab, new BrowserTab(popup)));
    }
}
