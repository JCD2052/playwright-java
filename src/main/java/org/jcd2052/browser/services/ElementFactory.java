package org.jcd2052.browser.services;

import com.microsoft.playwright.Locator;
import lombok.Getter;
import org.jcd2052.browser.services.interfaces.IElementFactory;
import org.jcd2052.browser.services.interfaces.IElementFinderService;
import org.jcd2052.browser.services.interfaces.IElementSupplier;
import org.jcd2052.elements.ButtonElement;
import org.jcd2052.elements.CheckBoxElement;
import org.jcd2052.elements.DropdownElement;
import org.jcd2052.elements.LinkElement;
import org.jcd2052.elements.RadioButtonElement;
import org.jcd2052.elements.interfaces.IButtonElement;
import org.jcd2052.elements.interfaces.ICheckBoxElement;
import org.jcd2052.elements.interfaces.IDropdownElement;
import org.jcd2052.elements.interfaces.IElement;
import org.jcd2052.elements.interfaces.ILabelElement;
import org.jcd2052.elements.interfaces.ILinkElement;
import org.jcd2052.elements.interfaces.IRadioButtonElement;
import org.jcd2052.elements.interfaces.ITextBoxElement;
import org.jcd2052.elements.LabelElement;
import org.jcd2052.elements.TextBoxElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ElementFactory implements IElementFactory {
    @Getter
    private final IElementFinderService elementFinderService;

    @Autowired
    public ElementFactory(IElementFinderService elementFinderService) {
        this.elementFinderService = elementFinderService;
    }

    public IButtonElement createButtonElement(String locatorStrategy, String name) {
        return createCustomElement(IButtonElement.class, locatorStrategy, name);
    }

    public ILabelElement createLabelElement(String locatorStrategy, String name) {
        return createCustomElement(ILabelElement.class, locatorStrategy, name);
    }

    public ITextBoxElement createTextBoxElement(String locatorStrategy, String name) {
        return createCustomElement(ITextBoxElement.class, locatorStrategy, name);
    }

    public ICheckBoxElement createCheckBoxElement(String locatorStrategy, String name) {
        return createCustomElement(ICheckBoxElement.class, locatorStrategy, name);
    }

    public IDropdownElement createDropdownElement(String locatorStrategy, String name) {
        return createCustomElement(IDropdownElement.class, locatorStrategy, name);
    }

    public ILabelElement createLinkElement(String locatorStrategy, String name) {
        return createCustomElement(ILabelElement.class, locatorStrategy, name);
    }

    public IRadioButtonElement createRadioButtonElement(String locatorStrategy, String name) {
        return createCustomElement(IRadioButtonElement.class, locatorStrategy, name);
    }

    public <T extends IElement> T createChildElement(
            Class<T> elementClass,
            IElement parentElement,
            String selector,
            String name) {
        return createCustomElement(
                elementClass,
                getSelectorFromLocator(elementFinderService.findChildElement(parentElement, selector)),
                name);
    }

    public <T extends IElement> List<T> createElements(
            String selector,
            String name,
            Class<T> clazz) {
        List<Locator> locators = elementFinderService.findElements(selector);
        return createListOfElements(clazz, locators, name);
    }

    public <T extends IElement> List<T> createChildElements(
            Class<T> elementClass,
            IElement parentElement,
            String selector,
            String name) {
        List<Locator> locators = elementFinderService.findChildElements(parentElement, selector);
        return createListOfElements(elementClass, locators, name);
    }

    public <T extends IElement> T createCustomElement(Class<T> clazz, String locator, String name) {
        IElementSupplier<T> elementSupplier = getDefaultElementSupplier(clazz);
        return createCustomElement(elementSupplier, locator, name);
    }

    protected <T extends IElement> List<T> createListOfElements(
            Class<T> elementClass,
            List<Locator> locators,
            String name) {
        List<T> elementList = new ArrayList<>();

        for (int i = 0; i < locators.size(); i++) {
            elementList.add(
                    createCustomElement(elementClass,
                            getSelectorFromLocator(locators.get(i)),
                            String.format("%s %d", name, i + 1)));
        }

        return elementList;
    }

    protected <T extends IElement> IElementSupplier<T> getDefaultElementSupplier(Class<T> clazz) {
        return (locator, name, elementFactory) -> {
            try {
                Constructor<T> ctor = resolveElementClass(clazz)
                        .getDeclaredConstructor(String.class, String.class, IElementFactory.class);
                ctor.setAccessible(true);
                T instance = ctor.newInstance(locator, name, this);
                ctor.setAccessible(false);
                return instance;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new IllegalArgumentException("Something went wrong during element casting");
            }
        };
    }

    protected <T extends IElement> Class<T> resolveElementClass(Class<T> clazz) {
        if (clazz.isInterface() && !getElementTypesMap().containsKey(clazz)) {
            throw new IllegalArgumentException(
                    String.format("Interface %1$s is not found in getElementTypesMap()", clazz));
        }

        //noinspection unchecked
        return clazz.isInterface() ? (Class<T>) getElementTypesMap().get(clazz) : clazz;
    }

    protected Map<Class<? extends IElement>, Class<? extends IElement>> getElementTypesMap() {
        Map<Class<? extends IElement>, Class<? extends IElement>> typesMap = new HashMap<>();
        typesMap.put(IButtonElement.class, ButtonElement.class);
        typesMap.put(ILabelElement.class, LabelElement.class);
        typesMap.put(ITextBoxElement.class, TextBoxElement.class);
        typesMap.put(IDropdownElement.class, DropdownElement.class);
        typesMap.put(ILinkElement.class, LinkElement.class);
        typesMap.put(IRadioButtonElement.class, RadioButtonElement.class);
        typesMap.put(ICheckBoxElement.class, CheckBoxElement.class);
        return typesMap;
    }

    private static String getSelectorFromLocator(Locator targetLocator) {
        return targetLocator.toString().replace("Locator@", "");
    }
}