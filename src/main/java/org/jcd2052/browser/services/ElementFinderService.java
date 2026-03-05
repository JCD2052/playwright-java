package org.jcd2052.browser.services;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.jcd2052.browser.services.interfaces.IElementFinderService;
import org.jcd2052.elements.interfaces.IElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElementFinderService implements IElementFinderService {
    private final BrowserService browserService;

    @Autowired
    public ElementFinderService(BrowserService browserService) {
        this.browserService = browserService;
    }

    public Locator findElement(String selector) {
        return getCurrentPageFromBrowser().locator(selector);
    }

    public Locator findChildElement(IElement parentElement, String selector) {
        return getCurrentPageFromBrowser().locator(parentElement.getSelector()).locator(selector);
    }

    protected Page getCurrentPageFromBrowser() {
        return browserService.getBrowser()
                .getCurrentBrowserWindow()
                .getCurrentBrowserTab()
                .getPage();
    }
}
