package org.jcd2052.elements;

import org.jcd2052.browser.services.interfaces.IElementFactory;
import org.jcd2052.elements.interfaces.ICheckBoxElement;

public class CheckBoxElement extends AbstractCheckableElement implements ICheckBoxElement {
    protected CheckBoxElement(String selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    @Override
    public void check() {
        getLocator().check();
    }

    @Override
    public void uncheck() {
        getLocator().uncheck();
    }
}
