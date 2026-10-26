package io.github.jcd2052.browser.browser;

import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.Cookie;
import io.github.jcd2052.browser.browser.interfaces.IBrowserTab;
import io.github.jcd2052.browser.browser.interfaces.IBrowserWindow;
import io.github.jcd2052.browser.browser.interfaces.ICookieManager;
import io.github.jcd2052.browser.configuration.BrowserProperties;
import io.github.jcd2052.browser.factory.BrowserFactory;
import io.github.jcd2052.browser.launcher.BrowserLauncherRegistry;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Verifies {@link CookieManager}, obtained through {@link IBrowserWindow#getCookieManager()}, against a real
 * headless Chromium.
 * <p>
 * Cookies are set for the made-up origin {@link #ORIGIN}; pages of that origin are answered locally by a
 * Playwright route, so no network access is needed.
 * </p>
 */
public class CookieManagerTest {
    /**
     * The origin every test cookie belongs to.
     */
    private static final String ORIGIN = "https://cookies.test";

    private PlaywrightBrowser browser;
    private IBrowserWindow window;
    private ICookieManager cookieManager;

    /**
     * Launches a fresh headless Chromium for each test, opens a window, and makes its pages of {@link #ORIGIN}
     * load locally.
     */
    @BeforeMethod
    public void openWindow() {
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
        window = browser.openNewWindow();
        window.getBrowserContext().route(ORIGIN + "/**", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("text/html")
                .setBody("<html><body>cookies</body></html>")));
        cookieManager = window.getCookieManager();
    }

    /**
     * Closes the whole browser and its Playwright driver after each test.
     */
    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        browser.close();
    }

    /**
     * Verifies that a newly opened window has no cookies.
     */
    @Test(description = "A new window has no cookies")
    public void aNewWindowHasNoCookies() {
        Assert.assertTrue(cookieManager.getCookies().isEmpty());
    }

    /**
     * Verifies that added cookies can be read back, with their values and the domain of their origin.
     */
    @Test(description = "Added cookies can be read back")
    public void addedCookiesCanBeReadBack() {
        cookieManager.addCookies(List.of(cookie("session", "abc123"), cookie("theme", "dark")));

        Map<String, String> values = cookieValues(cookieManager.getCookies());

        Assert.assertEquals(values, Map.of("session", "abc123", "theme", "dark"));
        Assert.assertTrue(cookieManager.getCookies().stream().allMatch(cookie -> "cookies.test".equals(cookie.domain)));
    }

    /**
     * Verifies that adding a cookie with the name of an existing one replaces its value.
     */
    @Test(description = "Adding a cookie with an existing name replaces its value")
    public void addingACookieWithAnExistingNameReplacesItsValue() {
        cookieManager.addCookies(List.of(cookie("theme", "dark")));

        cookieManager.addCookies(List.of(cookie("theme", "light")));

        Assert.assertEquals(cookieValues(cookieManager.getCookies()), Map.of("theme", "light"));
    }

    /**
     * Verifies that clearing removes every cookie of the window.
     */
    @Test(description = "clearCookies removes every cookie")
    public void clearCookiesRemovesEveryCookie() {
        cookieManager.addCookies(List.of(cookie("session", "abc123"), cookie("theme", "dark")));

        cookieManager.clearCookies();

        Assert.assertTrue(cookieManager.getCookies().isEmpty());
    }

    /**
     * Verifies that added cookies reach the pages of the window: a page of their origin, opened in another tab,
     * sees them through {@code document.cookie}.
     */
    @Test(description = "Cookies are shared by every tab of the window")
    public void cookiesAreSharedByEveryTabOfTheWindow() {
        cookieManager.addCookies(List.of(cookie("session", "abc123")));

        IBrowserTab otherTab = window.openNewTab();
        otherTab.navigateTo(ORIGIN + "/");

        Assert.assertEquals(otherTab.evaluate("document.cookie"), "session=abc123");
    }

    /**
     * Verifies that cookies set by a page are visible to the cookie manager.
     */
    @Test(description = "Cookies set by a page are visible to the cookie manager")
    public void cookiesSetByAPageAreVisibleToTheCookieManager() {
        IBrowserTab tab = window.getCurrentBrowserTab();
        tab.navigateTo(ORIGIN + "/");

        tab.evaluate("document.cookie = 'consent=yes'");

        Assert.assertEquals(cookieValues(cookieManager.getCookies()), Map.of("consent", "yes"));
    }

    /**
     * Verifies that cookies belong to their window only: another window of the same browser does not see them,
     * and clearing them there leaves this window's cookies alone.
     */
    @Test(description = "Cookies are isolated between windows")
    public void cookiesAreIsolatedBetweenWindows() {
        cookieManager.addCookies(List.of(cookie("session", "abc123")));

        ICookieManager otherWindowCookies = browser.openNewWindow().getCookieManager();
        otherWindowCookies.clearCookies();

        Assert.assertTrue(otherWindowCookies.getCookies().isEmpty());
        Assert.assertEquals(cookieValues(cookieManager.getCookies()), Map.of("session", "abc123"));
    }

    /**
     * Creates a cookie of {@link #ORIGIN}.
     */
    private static Cookie cookie(String name, String value) {
        return new Cookie(name, value).setUrl(ORIGIN);
    }

    /**
     * Maps cookies by name to their value.
     */
    private static Map<String, String> cookieValues(List<Cookie> cookies) {
        return cookies.stream().collect(Collectors.toMap(cookie -> cookie.name, cookie -> cookie.value));
    }
}
