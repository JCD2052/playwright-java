package org.jcd2052.browser.browser.interfaces;

import com.microsoft.playwright.Page;

public interface IBrowserTab {
    String getCurrentUrl();

    String getTitle();

    Page getPage();

    void goForward();

    void goBack();

    void reloadTab();

    void closeTab();

    void navigateTo(String url);
}
