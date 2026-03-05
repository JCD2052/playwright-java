package org.jcd2052.elements.interfaces;

import com.microsoft.playwright.Locator;

import java.util.Collection;

public interface IElement {
    String getName();

    String getText();

    Collection<String> getAllTexts();

    String getAttribute(String name);

    String getSelector();

    Locator getLocator();

    <T extends IElement> T createChildElement(Class<T> clazz, String selector, String name);

    byte[] getScreenshot();

    boolean isVisible();

    void click();

    void waitForLoading();

    void scrollToElement();

    default <T extends IElement> T createChildElement(Class<T> clazz, String selector) {
        return createChildElement(clazz, selector, "Child of " + getName());
    }
}
