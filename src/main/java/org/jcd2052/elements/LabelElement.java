package org.jcd2052.elements;

import org.jcd2052.browser.services.interfaces.IElementFactory;
import org.jcd2052.elements.interfaces.ILabelElement;

public class LabelElement extends AbstractElement implements ILabelElement {
    protected LabelElement(String locatorValue, String name, IElementFactory elementFactory) {
        super(locatorValue, name, elementFactory);
    }
}
