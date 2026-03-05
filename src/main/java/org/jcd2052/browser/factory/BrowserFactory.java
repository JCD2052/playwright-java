package org.jcd2052.browser.factory;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import org.jcd2052.browser.configuration.BrowserProperties;
import org.jcd2052.browser.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BrowserFactory implements IBrowserFactory {
    private final BrowserProperties browserProperties;

    @Autowired
    public BrowserFactory(Configuration configuration) {
        this.browserProperties = configuration.getBrowserProperties();
    }

    @Override
    public Browser createBrowser(Playwright playwright) {
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();

        launchOptions.setHeadless(browserProperties.getHeadless());
        launchOptions.setTimeout(browserProperties.getTimeout());

        switch (browserProperties.getName()) {
            case "chrome":
                return playwright.chromium().launch(launchOptions.setChannel("chrome"));
            case "firefox":
                return playwright.firefox().launch(launchOptions);
            case "edge":
                return playwright.chromium().launch(launchOptions.setChannel("msedge"));
            case "webkit":
                return playwright.webkit().launch(launchOptions);
            default:
                throw new IllegalArgumentException("Unsupported browser");
        }
    }
}
