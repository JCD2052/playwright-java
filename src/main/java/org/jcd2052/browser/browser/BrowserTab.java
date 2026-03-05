package org.jcd2052.browser.browser;

import com.microsoft.playwright.Page;
import lombok.Getter;
import org.jcd2052.browser.browser.interfaces.IBrowserTab;

public class BrowserTab implements IBrowserTab {
    @Getter
    private final Page page;

    public BrowserTab(Page page) {
        this.page = page;
    }

    @Override
    public String getCurrentUrl() {
        return page.url();
    }

    @Override
    public String getTitle() {
        return page.title();
    }

    @Override
    public void goForward() {
        page.goForward();
    }

    @Override
    public void goBack() {
        page.goBack();
    }

    @Override
    public void reloadTab() {
        page.reload();
    }

    @Override
    public void closeTab() {
        page.close();
    }

    @Override
    public void navigateTo(String url) {
        page.navigate(url);
    }
}
