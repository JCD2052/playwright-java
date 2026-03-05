package org.jcd2052.elements;

import com.microsoft.playwright.Locator;
import lombok.Getter;
import org.jcd2052.browser.services.interfaces.IElementFactory;
import org.jcd2052.elements.interfaces.IElement;

import java.util.Collection;
import java.util.List;

public abstract class AbstractElement implements IElement {
    @Getter
    private final String name;
    @Getter
    private final String selector;
    private final IElementFactory elementFactory;

    protected AbstractElement(String selector, String name, IElementFactory elementFactory) {
        this.elementFactory = elementFactory;
        this.name = name;
        this.selector = selector;
    }

    @Override
    public void scrollToElement() {
        getLocator().scrollIntoViewIfNeeded();
    }

    @Override
    public void click() {
        Locator locator = getLocator();
        locator.evaluate("el => el.style.border='3px solid red';");
        locator.click();
    }

    @Override
    public void waitForLoading() {
        getLocator().waitFor();
    }

    @Override
    public Collection<String> getAllTexts() {
        List<String> textValues = getLocator().allInnerTexts();
        return textValues;
    }

    @Override
    public String getText() {
        String text = getLocator().textContent();
        return text;
    }

    @Override
    public String getAttribute(String name) {
        return getLocator().getAttribute(name, new Locator.GetAttributeOptions());
    }

    @Override
    public boolean isVisible() {
        return getLocator().isVisible();
    }

    @Override
    public Locator getLocator() {
        return elementFactory.getElementFinderService().findElement(selector);
    }

    @Override
    public <T extends IElement> T createChildElement(Class<T> clazz, String selector, String name) {
        return elementFactory.createChildElement(clazz, this, selector, name);
    }

    @Override
    public byte[] getScreenshot() {
        return getLocator().screenshot();
    }
}
