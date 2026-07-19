package org.jcd2052.core.browser.services.interfaces;

import com.microsoft.playwright.Locator;
import org.jcd2052.core.elements.selector.Selector;

/**
 * Service responsible for resolving Just-In-Time (JIT) locators against the active Playwright page.
 */
public interface IElementFinderService {
    /**
     * Evaluates the provided Selector strategy against the current active browser page.
     *
     * @param selector the locator strategy to evaluate
     * @return the resolved Playwright Locator
     */
    Locator findElement(Selector selector);
}