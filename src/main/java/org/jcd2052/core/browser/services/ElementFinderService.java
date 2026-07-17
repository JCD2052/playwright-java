package org.jcd2052.core.browser.services;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.jcd2052.core.browser.services.interfaces.IBrowserService;
import org.jcd2052.core.browser.services.interfaces.IElementFinderService;

/**
 * Concrete implementation of the {@link IElementFinderService}.
 * <p>
 * This Spring service acts as the bridge between the framework's custom elements
 * and Playwright's native DOM querying mechanism. It dynamically retrieves the
 * currently active page from the {@link BrowserService} to locate elements.
 */
public class ElementFinderService implements IElementFinderService {
    /**
     * Service used to access the current state of the browser, including active windows and tabs.
     */
    private final IBrowserService browserService;
    /**
     * Constructs a new {@code ElementFinderService}.
     *
     * @param browserService The injected service used to retrieve the active Playwright {@link Page}.
     */
    public ElementFinderService(IBrowserService browserService) {
        this.browserService = browserService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Implementation Note:</b> This method fetches the current active page dynamically
     * and uses Playwright's {@link Page#locator(String)} method to create the locator.
     */
    @Override
    public Locator findElement(String selector) {
        return getCurrentPageFromBrowser().locator(selector);
    }

    /**
     * Helper method to safely navigate the browser hierarchy and retrieve the currently active tab's page.
     *
     * @return The active Playwright {@link Page} object.
     * @throws NullPointerException if there is no active browser, window, or tab.
     */
    protected Page getCurrentPageFromBrowser() {
        return browserService.getBrowser()
                .getCurrentBrowserWindow()
                .getCurrentBrowserTab()
                .getPage();
    }
}