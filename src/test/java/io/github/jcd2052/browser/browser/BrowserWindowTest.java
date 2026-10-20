package io.github.jcd2052.browser.browser;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.github.jcd2052.browser.browser.interfaces.IBrowserTab;
import io.github.jcd2052.browser.browser.interfaces.IBrowserWindow;
import io.github.jcd2052.browser.configuration.BrowserProperties;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * Verifies that {@link PlaywrightBrowser} and {@link BrowserWindow} hand out stable wrappers for the
 * same Playwright context/page, and keep the "current" window/tab valid as they close.
 * Runs against a real headless Chromium.
 */
public class BrowserWindowTest {
    private PlaywrightBrowser browser;

    @BeforeMethod
    public void launchBrowser() {
        Playwright playwright = Playwright.create();
        Browser rawBrowser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        BrowserProperties properties = new BrowserProperties()
                .setTimeout(5000L)
                .setPageLoadTimeout(5000L)
                .setWidth(800)
                .setHeight(600);
        browser = new PlaywrightBrowser(playwright, rawBrowser, properties);
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        browser.close();
    }

    @Test
    public void getBrowserWindowsReturnsTheSameWindowInstanceWithItsCurrentTab() {
        IBrowserWindow window = browser.openNewWindow();
        IBrowserTab tab = window.openNewTab();

        Collection<IBrowserWindow> windows = browser.getBrowserWindows();

        assertEquals(windows.size(), 1);
        IBrowserWindow listedWindow = windows.iterator().next();
        assertSame(listedWindow, window);
        assertSame(listedWindow.getCurrentBrowserTab(), tab);
    }

    @Test
    public void getBrowserTabsIncludesPopupsAndReturnsStableInstances() {
        IBrowserWindow window = browser.openNewWindow();
        IBrowserTab tab = window.openNewTab();

        Page popup = tab.getPage().waitForPopup(() -> tab.getPage().evaluate("window.open('about:blank')"));

        Collection<IBrowserTab> tabs = window.getBrowserTabs();
        assertEquals(tabs.size(), 2);
        assertTrue(tabs.contains(tab));
        IBrowserTab popupTab = tabs.stream().filter(t -> t.getPage() == popup).findFirst().orElseThrow();
        assertSame(window.getBrowserTabs().stream().filter(t -> t.getPage() == popup).findFirst().orElseThrow(),
                popupTab);
    }

    @Test
    public void closingTheCurrentTabFallsBackToTheMostRecentRemainingTab() {
        IBrowserWindow window = browser.openNewWindow();
        IBrowserTab first = window.openNewTab();
        IBrowserTab second = window.openNewTab();

        second.closeTab();
        assertSame(window.getCurrentBrowserTab(), first);

        first.closeTab();
        assertNull(window.getCurrentBrowserTab());
    }

    @Test
    public void closingTheCurrentWindowFallsBackToTheMostRecentRemainingWindow() {
        IBrowserWindow first = browser.openNewWindow();
        IBrowserWindow second = browser.openNewWindow();

        second.closeWindow();
        assertSame(browser.getCurrentBrowserWindow(), first);
        assertEquals(browser.getBrowserWindows().size(), 1);
    }
}
