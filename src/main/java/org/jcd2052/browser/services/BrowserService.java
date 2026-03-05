package org.jcd2052.browser.services;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jcd2052.browser.browser.interfaces.IBrowser;
import org.jcd2052.browser.browser.PlaywrightBrowser;
import org.jcd2052.browser.configuration.Configuration;
import org.jcd2052.browser.factory.IBrowserFactory;
import org.jcd2052.browser.services.interfaces.IBrowserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrowserService implements IBrowserService {
    private static final ThreadLocal<IBrowser> threadLocalBrowser = new ThreadLocal<>();
    private final Configuration configuration;
    private final IBrowserFactory browserFactory;

    @Autowired
    public BrowserService(Configuration configuration, IBrowserFactory browserFactory) {
        this.configuration = configuration;
        this.browserFactory = browserFactory;
    }

    @Override
    public IBrowser getBrowser() {
        IBrowser cached = threadLocalBrowser.get();
        if (cached != null && !cached.isClosed()) {
            return cached;
        }
        setBrowser(browserFactory);
        return threadLocalBrowser.get();
    }

    @Override
    public void setBrowser(IBrowserFactory browserFactory) {
        if (browserFactory == null) {
            throw new IllegalArgumentException("Browser factory cannot be null");
        }

        IBrowser cached = threadLocalBrowser.get();
        synchronized (this) {
            if (cached != null && !cached.isClosed()) {
                cached.close();
            }
            Browser playwrightBrowser = browserFactory.createBrowser(getPlaywright());
            IBrowser browser = new PlaywrightBrowser(playwrightBrowser);
            threadLocalBrowser.set(browser);
        }
    }

    public void close() {
        IBrowser browser = threadLocalBrowser.get();
        if (browser != null) {
            browser.close();
            threadLocalBrowser.remove();

        }
    }

    private Playwright getPlaywright() {
        return Playwright.create();
    }
}
