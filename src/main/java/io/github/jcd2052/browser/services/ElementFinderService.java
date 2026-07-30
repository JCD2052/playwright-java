package io.github.jcd2052.browser.services;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.jcd2052.browser.services.interfaces.IBrowserService;
import io.github.jcd2052.browser.services.interfaces.IElementFinderService;
import io.github.jcd2052.elements.selector.Selector;

/**
 * Implementation of the element finder service that interacts with the ThreadLocal browser instance.
 */
public class ElementFinderService implements IElementFinderService {
    private final IBrowserService<?> browserService;

    /**
     * Instantiates the element finder service.
     *
     * @param browserService the browser service supplying the active page
     */
    public ElementFinderService(IBrowserService<?> browserService) {
        this.browserService = browserService;
    }

    /**
     * Evaluates the Selector strategy dynamically against the currently active thread's page.
     *
     * @param selector the locator strategy to evaluate
     * @return the resolved Playwright Locator
     */
    @Override
    public Locator findElement(Selector selector) {
        Page currentPage = browserService.getBrowser().getCurrentBrowserWindow().getCurrentBrowserTab().getPage();
        return selector.evaluate(currentPage);
    }
}