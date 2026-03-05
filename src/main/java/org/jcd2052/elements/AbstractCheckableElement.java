package org.jcd2052.elements;

import org.jcd2052.browser.services.interfaces.IElementFactory;
import org.jcd2052.elements.interfaces.ICheckableElement;

public abstract class AbstractCheckableElement extends AbstractElement implements ICheckableElement {
    protected AbstractCheckableElement(String selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    @Override
    public boolean isChecked() {
        return getLocator().isChecked();
    }
}
