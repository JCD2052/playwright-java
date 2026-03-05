package org.jcd2052.browser.browser;

import com.microsoft.playwright.Browser;
import lombok.Getter;
import lombok.Setter;
import org.jcd2052.browser.browser.interfaces.IBrowser;
import org.jcd2052.browser.browser.interfaces.IBrowserWindow;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class PlaywrightBrowser implements IBrowser {
    private final Browser browser;
    @Setter
    private IBrowserWindow currentBrowserWindow;
    private boolean isClosed;

    public PlaywrightBrowser(com.microsoft.playwright.Browser browser) {
        this.browser = browser;
        isClosed = false;
    }

    @Override
    public IBrowserWindow openNewWindow() {
        BrowserWindow browserWindow = new BrowserWindow(browser.newContext());
        setCurrentBrowserWindow(browserWindow);
        return browserWindow;
    }

    @Override
    public Collection<IBrowserWindow> getBrowserWindows() {
        return browser.contexts()
                .stream()
                .map(BrowserWindow::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getBrowserName() {
        return browser.browserType().name();
    }

    @Override
    public byte[] takeScreenshot() {
        return getCurrentBrowserWindow()
                .getCurrentBrowserTab()
                .getPage()
                .screenshot();
    }

    @Override
    public void switchToWindow(BrowserWindow window) {
        window.switchToTab(window.getCurrentBrowserTab());
        setCurrentBrowserWindow(window);
    }

    @Override
    public void close() {
        browser.close();
        isClosed = true;
    }
}
