package org.jcd2052.browser.browser;

import com.microsoft.playwright.BrowserContext;
import lombok.Getter;
import lombok.Setter;
import org.jcd2052.browser.browser.interfaces.IBrowserTab;
import org.jcd2052.browser.browser.interfaces.IBrowserWindow;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class BrowserWindow implements IBrowserWindow {
    private final BrowserContext browserContext;
    @Setter
    private IBrowserTab currentBrowserTab;

    public BrowserWindow(BrowserContext context) {
        this.browserContext = context;
    }

    @Override
    public IBrowserTab openNewTab() {
        IBrowserTab browserTab = new BrowserTab(browserContext.newPage());
        setCurrentBrowserTab(browserTab);
        return browserTab;
    }

    @Override
    public Collection<IBrowserTab> getBrowserTabs() {
        return browserContext.pages()
                .stream()
                .map(BrowserTab::new)
                .collect(Collectors.toList());
    }

    @Override
    public void switchToTab(IBrowserTab tab) {
        tab.getPage().bringToFront();
        currentBrowserTab = tab;
    }
}
