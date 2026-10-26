package io.github.jcd2052.browser.browser.interfaces;

import com.microsoft.playwright.options.Cookie;
import java.util.List;

/**
 * Manages the cookies of one browser window (Playwright browser context); every tab of the window shares them.
 */
public interface ICookieManager {
    /**
     * Adds cookies to this window.
     *
     * @param cookies The Playwright cookies to add.
     */
    void addCookies(List<Cookie> cookies);

    /**
     * Retrieves all cookies of this window.
     *
     * @return The window's current cookies.
     */
    List<Cookie> getCookies();

    /**
     * Removes all cookies from this window.
     */
    void clearCookies();
}