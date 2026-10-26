package io.github.jcd2052.browser.services.interfaces;

import com.microsoft.playwright.Locator;
import io.github.jcd2052.elements.selector.Selector;

/**
 * Resolves selectors into Playwright locators on the current tab, at the moment an element acts, so elements
 * always work with whatever tab is current.
 */
public interface IElementFinderService {
    /**
     * Resolves the selector against the current tab's page.
     *
     * @param selector the selector to resolve
     * @return the resolved Playwright locator
     * @throws IllegalStateException if no window or tab is open
     */
    Locator findElement(Selector selector);
}