package io.github.jcd2052.browser.services.interfaces;

import com.microsoft.playwright.Locator;
import io.github.jcd2052.elements.selector.Selector;

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