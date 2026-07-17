package org.jcd2052.core.browser.services.interfaces;

import com.microsoft.playwright.Locator;

/**
 * Service interface responsible for locating web elements on the active page.
 * <p>
 * This service abstracts the underlying Playwright element discovery mechanism,
 * providing a centralized way to translate string-based selector strategies
 * into Playwright {@link Locator} objects.
 */
public interface IElementFinderService {

    /**
     * Finds an element (or a collection of elements) on the current active page
     * based on the provided selector string.
     *
     * @param selector The locator strategy string (e.g., XPath, CSS selector, Playwright specific selectors).
     * @return A Playwright {@link Locator} object that points to the target element(s).
     */
    Locator findElement(String selector);
}