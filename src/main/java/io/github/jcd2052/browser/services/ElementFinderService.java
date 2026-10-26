package io.github.jcd2052.browser.services;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.jcd2052.browser.services.interfaces.IBrowserService;
import io.github.jcd2052.browser.services.interfaces.IElementFinderService;
import io.github.jcd2052.elements.selector.Selector;

/**
 * Default {@link IElementFinderService}: resolves selectors against the current tab of the calling thread's
 * browser, taken from the browser service on every call.
 */
public class ElementFinderService implements IElementFinderService {
    private final IBrowserService<?> browserService;

    /**
     * Creates a finder that reads the current tab from the given browser service.
     *
     * @param browserService the browser service whose current tab elements act on
     */
    public ElementFinderService(IBrowserService<?> browserService) {
        this.browserService = browserService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locator findElement(Selector selector) {
        Page currentPage = browserService.getBrowser().requireCurrentBrowserTab().getPage();
        return selector.evaluate(currentPage);
    }
}