package org.jcd2052.browser.services.interfaces;

import org.jcd2052.elements.interfaces.IElement;

import java.util.List;

public interface IElementFactory {
    <T extends IElement> T createChildElement(
            Class<T> elementClass,
            IElement parentElement,
            String selector,
            String name);

    <T extends IElement> List<T> createChildElements(
            Class<T> elementClass,
            IElement parentElement,
            String selector,
            String name);

    <T extends IElement> List<T> createElements(String selector, String name, Class<T> clazz);

    <T extends IElement> T createCustomElement(Class<T> clazz, String locator, String name);

    IElementFinderService getElementFinderService();

    default <T extends IElement> T createCustomElement(
            IElementSupplier<T> elementSupplier,
            String locator,
            String name) {
        return elementSupplier.get(locator, name, this);
    }
}
