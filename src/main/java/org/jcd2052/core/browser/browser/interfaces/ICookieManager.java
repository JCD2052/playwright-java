package org.jcd2052.core.browser.browser.interfaces;

import com.microsoft.playwright.options.Cookie;
import java.util.List;

/**
 * Manages browser cookies for a specific browser window/context.
 */
public interface ICookieManager {
    /**
     * Adds an array of cookies to the current browser context.
     *
     * @param cookies A list of Playwright Cookie objects to add.
     */
    void addCookies(List<Cookie> cookies);

    /**
     * Retrieves all cookies from the current browser context.
     *
     * @return A list of current Playwright Cookie objects.
     */
    List<Cookie> getCookies();

    /**
     * Clears all cookies from the current browser context.
     */
    void clearCookies();
}