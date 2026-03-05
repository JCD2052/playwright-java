package org.jcd2052.browser.browser.interfaces;

import com.microsoft.playwright.BrowserContext;

import java.util.Collection;

public interface IBrowserWindow {
    IBrowserTab openNewTab();

    Collection<IBrowserTab> getBrowserTabs();

    BrowserContext getBrowserContext();

    IBrowserTab getCurrentBrowserTab();

    void setCurrentBrowserTab(IBrowserTab currentIBrowserTab);

    void switchToTab(IBrowserTab tab);
}
