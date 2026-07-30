package io.github.jcd2052.browser.browser;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.options.Cookie;
import io.github.jcd2052.browser.browser.interfaces.ICookieManager;
import io.github.jcd2052.logger.LoggerProvider;

import java.util.List;

public class CookieManager implements ICookieManager {
    private final BrowserContext browserContext;

    public CookieManager(BrowserContext browserContext) {
        this.browserContext = browserContext;
    }

    @Override
    public void addCookies(List<Cookie> cookies) {
        LoggerProvider.getLogger().info("Adding %d cookies to the browser context", cookies.size());
        browserContext.addCookies(cookies);
    }

    @Override
    public List<Cookie> getCookies() {
        return browserContext.cookies();
    }

    @Override
    public void clearCookies() {
        LoggerProvider.getLogger().info("Clearing all cookies from the browser context");
        browserContext.clearCookies();
    }
}