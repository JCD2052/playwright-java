package org.jcd2052.browser.services.interfaces;

import org.jcd2052.elements.interfaces.IElement;

public interface IElementSupplier<T extends IElement> {
    T get(String locator, String name, IElementFactory elementFactory);
}
