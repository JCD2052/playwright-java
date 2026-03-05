package org.jcd2052.pages;

import com.microsoft.playwright.Page;
import lombok.Getter;
import org.jcd2052.browser.services.ElementFactory;
import org.jcd2052.elements.interfaces.ILabelElement;

@Getter
public abstract class AbstractPage implements IPage {
    private final String name;
    private final ILabelElement pageLabel;
    private final ElementFactory elementFactory;

    protected AbstractPage(String locatorStrategy, String name, ElementFactory elementFactory) {
        this.name = name;
        this.elementFactory = elementFactory;
        this.pageLabel = elementFactory.createLabelElement(locatorStrategy, name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ILabelElement getLabel() {
        return pageLabel;
    }

    protected Page getPage() {
        return pageLabel.getLocator().page();
    }
}
