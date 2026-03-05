package org.jcd2052.elements;

import org.jcd2052.browser.services.interfaces.IElementFactory;
import org.jcd2052.elements.interfaces.IRadioButtonElement;

public class RadioButtonElement extends AbstractCheckableElement implements IRadioButtonElement {
    protected RadioButtonElement(String selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }
}
