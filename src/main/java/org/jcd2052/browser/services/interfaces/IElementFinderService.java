package org.jcd2052.browser.services.interfaces;

import com.microsoft.playwright.Locator;
import org.jcd2052.elements.interfaces.IElement;

import java.util.List;

public interface IElementFinderService {
    Locator findElement(String selector);

    Locator findChildElement(IElement parentElement, String selector);

    default List<Locator> findChildElements(IElement parentElement, String selector) {
        return findChildElement(parentElement, selector).all();
    }

    default List<Locator> findElements(String selector) {
        return findElement(selector).all();
    }
}
