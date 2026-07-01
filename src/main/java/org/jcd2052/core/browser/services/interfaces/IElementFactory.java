package org.jcd2052.core.browser.services.interfaces;

import org.jcd2052.core.elements.ExpectedCount;
import org.jcd2052.core.elements.interfaces.IButtonElement;
import org.jcd2052.core.elements.interfaces.ICheckBoxElement;
import org.jcd2052.core.elements.interfaces.IDropdownElement;
import org.jcd2052.core.elements.interfaces.IElement;
import org.jcd2052.core.elements.interfaces.IElementCollection;
import org.jcd2052.core.elements.interfaces.ILabelElement;
import org.jcd2052.core.elements.interfaces.ILinkElement;
import org.jcd2052.core.elements.interfaces.IRadioButtonElement;
import org.jcd2052.core.elements.interfaces.ITextBoxElement;
import org.jcd2052.core.elements.interfaces.IUploadBox;

/**
 * Factory interface responsible for the creation and instantiation of all UI elements and collections.
 * <p>
 * This factory abstracts the underlying instantiation logic, allowing developers to create typed
 * components (like buttons, text boxes, and custom elements) cleanly by providing a selector and a name.
 * </p>
 */
public interface IElementFactory {
    <T extends IElement> IElementCollection<T> createElementsCollection(
            String selector,
            String name,
            IElementSupplier<T> supplier,
            ExpectedCount expectedCount);

    <T extends IElement> IElementCollection<T> createChildElementsCollection(
            IElementSupplier<T> supplier,
            IElement parentElement,
            String selector,
            String name,
            ExpectedCount expectedCount);

    <T extends IElement> IElementCollection<T> createElementsCollection(
            String selector,
            String name,
            Class<T> clazz,
            ExpectedCount expectedCount);

    /**
     * Creates a collection of child UI elements located within a specific parent element.
     *
     * @param elementClass  The class type of the child elements to instantiate.
     * @param parentElement The parent {@link IElement} to search within.
     * @param selector      The relative locator strategy for the child elements.
     * @param name          The logical name of the child element collection.
     * @param expectedCount The expected count condition to wait for.
     * @param <T>           The type of the child elements.
     * @return An {@link IElementCollection} containing the instantiated child elements.
     */
    <T extends IElement> IElementCollection<T> createChildElementsCollection(
            Class<T> elementClass,
            IElement parentElement,
            String selector,
            String name,
            ExpectedCount expectedCount);

    /**
     * Creates a custom single UI element based on the provided class and locator.
     *
     * @param clazz   The class type or interface of the element to instantiate.
     * @param locator The locator strategy to find the element.
     * @param name    The logical name of the element.
     * @param <T>     The specific element type.
     * @return The instantiated element.
     */
    <T extends IElement> T createCustomElement(Class<T> clazz, String locator, String name);

    /**
     * Retrieves the underlying service responsible for locating elements via Playwright.
     *
     * @return The {@link IElementFinderService} instance.
     */
    IElementFinderService getElementFinderService();

    /**
     * Checks if visual highlighting is enabled for elements during interaction.
     *
     * @return {@code true} if element highlighting is enabled, {@code false} otherwise.
     */
    boolean isHighlightEnabled();

    /**
     * Convenience method to create an element collection without specifying an expected count
     * (defaults to {@link ExpectedCount#ANY}).
     *
     * @param selector The locator strategy.
     * @param name     The logical name.
     * @param clazz    The element class type.
     * @param <T>      The specific element type.
     * @return An {@link IElementCollection}.
     */
    default <T extends IElement> IElementCollection<T> createElementsCollection(
            String selector, String name, Class<T> clazz) {
        return createElementsCollection(selector, name, clazz, ExpectedCount.ANY);
    }

    /**
     * Convenience method to create a child element collection without specifying an expected count
     * (defaults to {@link ExpectedCount#ANY}).
     *
     * @param elementClass  The child element class type.
     * @param parentElement The parent element.
     * @param selector      The relative locator strategy.
     * @param name          The logical name.
     * @param <T>           The specific element type.
     * @return An {@link IElementCollection}.
     */
    default <T extends IElement> IElementCollection<T> createChildElementsCollection(
            Class<T> elementClass, IElement parentElement, String selector, String name) {
        return createChildElementsCollection(elementClass, parentElement, selector, name, ExpectedCount.ANY);
    }

    /**
     * Creates a single child element located within a specific parent element.
     *
     * @param elementClass  The child element class type.
     * @param parentElement The parent element.
     * @param selector      The relative locator strategy.
     * @param name          The logical name.
     * @param <T>           The specific element type.
     * @return The instantiated child element.
     */
    default <T extends IElement> T createChildElement(
            Class<T> elementClass,
            IElement parentElement,
            String selector,
            String name) {
        return createCustomElement(
                elementClass,
                combineSelectors(parentElement, selector),
                name);
    }

    /**
     * Creates a custom element using an explicit element supplier instead of reflection.
     *
     * @param elementSupplier The supplier providing the instantiation logic.
     * @param locator         The locator strategy.
     * @param name            The logical name.
     * @param <T>             The specific element type.
     * @return The instantiated element.
     */
    default <T extends IElement> T createCustomElement(
            IElementSupplier<T> elementSupplier,
            String locator,
            String name) {
        return elementSupplier.get(locator, name, this);
    }

    /**
     * Creates a child element using an explicit element supplier.
     *
     * @param elementSupplier The supplier providing the instantiation logic.
     * @param parentElement   The parent element.
     * @param selector        The relative locator strategy.
     * @param name            The logical name.
     * @param <T>             The specific element type.
     * @return The instantiated child element.
     */
    default <T extends IElement> T createChildElement(
            IElementSupplier<T> elementSupplier,
            IElement parentElement,
            String selector,
            String name) {
        return createCustomElement(elementSupplier, combineSelectors(parentElement, selector), name);
    }

    /**
     * Convenience method to create an {@link IButtonElement}.
     *
     * @param locatorStrategy The locator strategy.
     * @param name            The logical name.
     * @return The instantiated button element.
     */
    default IButtonElement createButtonElement(String locatorStrategy, String name) {
        return createCustomElement(IButtonElement.class, locatorStrategy, name);
    }

    /**
     * Convenience method to create an {@link ILabelElement}.
     *
     * @param locatorStrategy The locator strategy.
     * @param name            The logical name.
     * @return The instantiated label element.
     */
    default ILabelElement createLabelElement(String locatorStrategy, String name) {
        return createCustomElement(ILabelElement.class, locatorStrategy, name);
    }

    /**
     * Convenience method to create an {@link ITextBoxElement}.
     *
     * @param locatorStrategy The locator strategy.
     * @param name            The logical name.
     * @return The instantiated text box element.
     */
    default ITextBoxElement createTextBoxElement(String locatorStrategy, String name) {
        return createCustomElement(ITextBoxElement.class, locatorStrategy, name);
    }

    /**
     * Convenience method to create an {@link ICheckBoxElement}.
     *
     * @param locatorStrategy The locator strategy.
     * @param name            The logical name.
     * @return The instantiated check box element.
     */
    default ICheckBoxElement createCheckBoxElement(String locatorStrategy, String name) {
        return createCustomElement(ICheckBoxElement.class, locatorStrategy, name);
    }

    /**
     * Convenience method to create an {@link IDropdownElement}.
     *
     * @param locatorStrategy The locator strategy.
     * @param name            The logical name.
     * @return The instantiated dropdown element.
     */
    default IDropdownElement createDropdownElement(String locatorStrategy, String name) {
        return createCustomElement(IDropdownElement.class, locatorStrategy, name);
    }

    /**
     * Convenience method to create an {@link ILinkElement}.
     *
     * @param locatorStrategy The locator strategy.
     * @param name            The logical name.
     * @return The instantiated link element.
     */
    default ILinkElement createLinkElement(String locatorStrategy, String name) {
        return createCustomElement(ILinkElement.class, locatorStrategy, name);
    }

    /**
     * Convenience method to create an {@link IRadioButtonElement}.
     *
     * @param locatorStrategy The locator strategy.
     * @param name            The logical name.
     * @return The instantiated radio button element.
     */
    default IRadioButtonElement createRadioButtonElement(String locatorStrategy, String name) {
        return createCustomElement(IRadioButtonElement.class, locatorStrategy, name);
    }

    /**
     * Convenience method to create an {@link IUploadBox}.
     *
     * @param locatorStrategy The locator strategy.
     * @param name            The logical name.
     * @return The instantiated upload box element.
     */
    default IUploadBox createUploadBoxElement(String locatorStrategy, String name) {
        return createCustomElement(IUploadBox.class, locatorStrategy, name);
    }

    /**
     * Combines a parent element's selector with a child selector string.
     *
     * @param parentElement The parent element.
     * @param selector      The relative child selector.
     * @return The combined absolute selector.
     */
    default String combineSelectors(IElement parentElement, String selector) {
        return combineSelectors(parentElement.getSelector(), selector);
    }

    /**
     * Combines two selector strings using Playwright's descendent selector syntax ({@code >>}).
     *
     * @param parentSelector The parent selector string.
     * @param selector       The child selector string.
     * @return The combined selector.
     */
    default String combineSelectors(String parentSelector, String selector) {
        return parentSelector + ">>" + selector;
    }
}