package org.jcd2052.elements;

import org.jcd2052.browser.services.interfaces.IElementFactory;
import org.jcd2052.elements.interfaces.ILinkElement;

public class LinkElement extends AbstractElement implements ILinkElement {
    private static final String HREF_ATTRIBUTE_NAME = "href";

    protected LinkElement(String selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    @Override
    public String getHref() {
        return getAttribute(HREF_ATTRIBUTE_NAME);
    }
}
