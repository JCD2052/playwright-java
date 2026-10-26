package io.github.jcd2052.browser.services.interfaces;

import io.github.jcd2052.elements.ExpectedCount;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.IButtonElement;
import io.github.jcd2052.elements.interfaces.ICheckBoxElement;
import io.github.jcd2052.elements.interfaces.IDropdownElement;
import io.github.jcd2052.elements.interfaces.IElement;
import io.github.jcd2052.elements.interfaces.IElementCollection;
import io.github.jcd2052.elements.interfaces.ILabelElement;
import io.github.jcd2052.elements.interfaces.ILinkElement;
import io.github.jcd2052.elements.interfaces.IRadioButtonElement;
import io.github.jcd2052.elements.interfaces.ITextBoxElement;
import io.github.jcd2052.elements.interfaces.IUploadBox;

/**
 * Creates the framework's typed elements and element collections from a {@link Selector} and a name.
 * <p>
 * Elements only store their selector: the selector is resolved on the current tab each time an element acts,
 * so an element created once (e.g. as a page-object field) keeps working across pages and tabs. The name is
 * used in log messages and errors. Child elements and collections are scoped to a parent element by chaining
 * the parent's selector with the child's.
 * </p>
 */
public interface IElementFactory {
    /**
     * Creates a collection of the elements matching a selector, created with the given supplier.
     *
     * @param selector      The selector matching every element of the collection.
     * @param name          The collection's name; each element is named after it with its position, e.g. "Tag 1".
     * @param supplier      Creates each element of the collection.
     * @param expectedCount The count condition to wait for before the elements are read.
     * @param <T>           The element type.
     * @return The collection.
     */
    <T extends IElement> IElementCollection<T> createElementsCollection(
            Selector selector,
            String name,
            IElementSupplier<T> supplier,
            ExpectedCount expectedCount);

    /**
     * Creates a collection of the elements matching a selector inside a parent element, created with the given
     * supplier.
     *
     * @param supplier      Creates each element of the collection.
     * @param parentElement The element to search inside.
     * @param selector      The selector matching the elements, relative to the parent.
     * @param name          The collection's name; each element is named after it with its position.
     * @param expectedCount The count condition to wait for before the elements are read.
     * @param <T>           The element type.
     * @return The collection.
     */
    <T extends IElement> IElementCollection<T> createChildElementsCollection(
            IElementSupplier<T> supplier,
            IElement parentElement,
            Selector selector,
            String name,
            ExpectedCount expectedCount);

    /**
     * Creates a collection of the elements matching a selector, each created as the given element type.
     *
     * @param selector      The selector matching every element of the collection.
     * @param name          The collection's name; each element is named after it with its position, e.g. "Tag 1".
     * @param clazz         The element type, resolved as in {@link #createCustomElement(Class, Selector, String)}.
     * @param expectedCount The count condition to wait for before the elements are read.
     * @param <T>           The element type.
     * @return The collection.
     */
    <T extends IElement> IElementCollection<T> createElementsCollection(
            Selector selector,
            String name,
            Class<T> clazz,
            ExpectedCount expectedCount);

    /**
     * Creates a collection of the elements matching a selector inside a parent element.
     *
     * @param elementClass  The element type, resolved as in {@link #createCustomElement(Class, Selector, String)}.
     * @param parentElement The element to search inside.
     * @param selector      The selector matching the elements, relative to the parent.
     * @param name          The collection's name; each element is named after it with its position.
     * @param expectedCount The count condition to wait for before the elements are read.
     * @param <T>           The type of the child elements.
     * @return An {@link IElementCollection} containing the instantiated child elements.
     */
    <T extends IElement> IElementCollection<T> createChildElementsCollection(
            Class<T> elementClass,
            IElement parentElement,
            Selector selector,
            String name,
            ExpectedCount expectedCount);

    /**
     * Creates an element of the given type.
     * <p>
     * An element interface (e.g. {@code IButtonElement.class}) is created as the implementation registered for
     * it; a concrete class is created directly. Either way the implementation needs a
     * {@code (Selector, String, IElementFactory)} constructor, which may be non-public.
     * </p>
     *
     * @param clazz   The element interface or class.
     * @param locator The selector that finds the element.
     * @param name    The element's name, used in log messages and errors.
     * @param <T>     The specific element type.
     * @return The instantiated element.
     */
    <T extends IElement> T createCustomElement(Class<T> clazz, Selector locator, String name);

    /**
     * Gets the service that resolves element selectors on the current tab.
     *
     * @return The element finder service.
     */
    IElementFinderService getElementFinderService();

    /**
     * Gets the service that builds the elements' localized log messages.
     *
     * @return The localization service.
     */
    ILocalizationService getLocalizationService();

    /**
     * Checks whether elements should be outlined in red before some actions, for debugging.
     *
     * @return {@code true} if element highlighting is enabled, {@code false} otherwise.
     */
    boolean isHighlightEnabled();

    /**
     * Convenience method to create an element collection without specifying an expected count
     * (defaults to {@link ExpectedCount#ANY}).
     *
     * @param selector The selector.
     * @param name     The element's name.
     * @param clazz    The element class type.
     * @param <T>      The specific element type.
     * @return An {@link IElementCollection}.
     */
    default <T extends IElement> IElementCollection<T> createElementsCollection(
            Selector selector, String name, Class<T> clazz) {
        return createElementsCollection(selector, name, clazz, ExpectedCount.ANY);
    }

    /**
     * Convenience method to create a child element collection without specifying an expected count
     * (defaults to {@link ExpectedCount#ANY}).
     *
     * @param elementClass  The child element class type.
     * @param parentElement The parent element.
     * @param selector      The relative selector.
     * @param name          The element's name.
     * @param <T>           The specific element type.
     * @return An {@link IElementCollection}.
     */
    default <T extends IElement> IElementCollection<T> createChildElementsCollection(
            Class<T> elementClass, IElement parentElement, Selector selector, String name) {
        return createChildElementsCollection(elementClass, parentElement, selector, name, ExpectedCount.ANY);
    }

    /**
     * Creates a single child element located within a specific parent element.
     *
     * @param elementClass  The child element class type.
     * @param parentElement The parent element.
     * @param selector      The relative selector.
     * @param name          The element's name.
     * @param <T>           The specific element type.
     * @return The instantiated child element.
     */
    default <T extends IElement> T createChildElement(
            Class<T> elementClass,
            IElement parentElement,
            Selector selector,
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
     * @param locator         The selector.
     * @param name            The element's name.
     * @param <T>             The specific element type.
     * @return The instantiated element.
     */
    default <T extends IElement> T createCustomElement(
            IElementSupplier<T> elementSupplier,
            Selector locator,
            String name) {
        return elementSupplier.get(locator, name, this);
    }

    /**
     * Creates a child element using an explicit element supplier.
     *
     * @param elementSupplier The supplier providing the instantiation logic.
     * @param parentElement   The parent element.
     * @param selector        The relative selector.
     * @param name            The element's name.
     * @param <T>             The specific element type.
     * @return The instantiated child element.
     */
    default <T extends IElement> T createChildElement(
            IElementSupplier<T> elementSupplier,
            IElement parentElement,
            Selector selector,
            String name) {
        return createCustomElement(elementSupplier, combineSelectors(parentElement, selector), name);
    }

    /**
     * Convenience method to create an {@link IButtonElement}.
     *
     * @param locatorStrategy The selector.
     * @param name            The element's name.
     * @return The instantiated button element.
     */
    default IButtonElement createButtonElement(Selector locatorStrategy, String name) {
        return createCustomElement(IButtonElement.class, locatorStrategy, name);
    }

    /**
     * Convenience method to create an {@link ILabelElement}.
     *
     * @param locatorStrategy The selector.
     * @param name            The element's name.
     * @return The instantiated label element.
     */
    default ILabelElement createLabelElement(Selector locatorStrategy, String name) {
        return createCustomElement(ILabelElement.class, locatorStrategy, name);
    }

    /**
     * Convenience method to create an {@link ITextBoxElement}.
     *
     * @param locatorStrategy The selector.
     * @param name            The element's name.
     * @return The instantiated text box element.
     */
    default ITextBoxElement createTextBoxElement(Selector locatorStrategy, String name) {
        return createCustomElement(ITextBoxElement.class, locatorStrategy, name);
    }

    /**
     * Convenience method to create an {@link ICheckBoxElement}.
     *
     * @param locatorStrategy The selector.
     * @param name            The element's name.
     * @return The instantiated check box element.
     */
    default ICheckBoxElement createCheckBoxElement(Selector locatorStrategy, String name) {
        return createCustomElement(ICheckBoxElement.class, locatorStrategy, name);
    }

    /**
     * Convenience method to create an {@link IDropdownElement}.
     *
     * @param locatorStrategy The selector.
     * @param name            The element's name.
     * @return The instantiated dropdown element.
     */
    default IDropdownElement createDropdownElement(Selector locatorStrategy, String name) {
        return createCustomElement(IDropdownElement.class, locatorStrategy, name);
    }

    /**
     * Convenience method to create an {@link ILinkElement}.
     *
     * @param locatorStrategy The selector.
     * @param name            The element's name.
     * @return The instantiated link element.
     */
    default ILinkElement createLinkElement(Selector locatorStrategy, String name) {
        return createCustomElement(ILinkElement.class, locatorStrategy, name);
    }

    /**
     * Convenience method to create an {@link IRadioButtonElement}.
     *
     * @param locatorStrategy The selector.
     * @param name            The element's name.
     * @return The instantiated radio button element.
     */
    default IRadioButtonElement createRadioButtonElement(Selector locatorStrategy, String name) {
        return createCustomElement(IRadioButtonElement.class, locatorStrategy, name);
    }

    /**
     * Convenience method to create an {@link IUploadBox}.
     *
     * @param locatorStrategy The selector.
     * @param name            The element's name.
     * @return The instantiated upload box element.
     */
    default IUploadBox createUploadBoxElement(Selector locatorStrategy, String name) {
        return createCustomElement(IUploadBox.class, locatorStrategy, name);
    }

    /**
     * Scopes a child selector to a parent element by chaining it after the parent's selector.
     *
     * @param parentElement The parent element.
     * @param selector      The child selector, relative to the parent.
     * @return The combined selector.
     */
    default Selector combineSelectors(IElement parentElement, Selector selector) {
        return combineSelectors(parentElement.getSelector(), selector);
    }

    /**
     * Scopes a child selector to a parent selector by chaining it after the parent.
     *
     * @param parentSelector The parent selector, or {@code null} for no parent.
     * @param selector       The child selector, relative to the parent.
     * @return The combined selector, or the child selector itself when there is no parent.
     */
    default Selector combineSelectors(Selector parentSelector, Selector selector) {
        if (parentSelector == null) {
            return selector;
        }
        return parentSelector.chain(selector);
    }
}