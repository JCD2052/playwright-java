package org.jcd2052.elements;

import org.jcd2052.browser.services.interfaces.IElementFactory;
import org.jcd2052.elements.interfaces.ITextBoxElement;

public class TextBoxElement extends AbstractElement implements ITextBoxElement {
    protected TextBoxElement(String locatorValue, String name, IElementFactory elementFactory) {
        super(locatorValue, name, elementFactory);
    }

    @Override
    public void fillText(String text) {
        getLocator().fill(text);
    }

    @Override
    public void clearText() {
        getLocator().clear();
    }
}
