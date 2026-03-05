package org.jcd2052.elements;

import org.jcd2052.browser.services.interfaces.IElementFactory;
import org.jcd2052.elements.interfaces.IButtonElement;

public class ButtonElement extends AbstractElement implements IButtonElement {
    protected ButtonElement(String locatorValue, String name, IElementFactory elementFactory) {
        super(locatorValue, name, elementFactory);
    }
}
