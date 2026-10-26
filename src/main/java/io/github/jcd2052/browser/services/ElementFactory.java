package io.github.jcd2052.browser.services;

import io.github.jcd2052.browser.services.interfaces.ILocalizationService;
import lombok.Getter;
import io.github.jcd2052.browser.configuration.IBrowserProperties;
import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.browser.services.interfaces.IElementFinderService;
import io.github.jcd2052.browser.services.interfaces.IElementSupplier;
import io.github.jcd2052.elements.ButtonElement;
import io.github.jcd2052.elements.CheckBoxElement;
import io.github.jcd2052.elements.DropdownElement;
import io.github.jcd2052.elements.ElementCollection;
import io.github.jcd2052.elements.ExpectedCount;
import io.github.jcd2052.elements.LabelElement;
import io.github.jcd2052.elements.LinkElement;
import io.github.jcd2052.elements.RadioButtonElement;
import io.github.jcd2052.elements.TextBoxElement;
import io.github.jcd2052.elements.UploadBox;
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Default {@link IElementFactory}.
 * <p>
 * Element interfaces are mapped to implementations in a registry that starts with the built-in elements
 * ({@link IButtonElement} to {@link ButtonElement}, and so on) and can be extended with
 * {@link #registerElement(Class, Class)}. Elements are created by reflection through their
 * {@code (Selector, String, IElementFactory)} constructor.
 * </p>
 */
public class ElementFactory implements IElementFactory {
    /**
     * Resolves the elements' selectors on the current tab.
     */
    @Getter
    private final IElementFinderService elementFinderService;
    /**
     * The configuration, read for the highlight setting.
     */
    @Getter
    private final IBrowserProperties browserProperties;
    /**
     * Builds the elements' localized log messages.
     */
    @Getter
    private final ILocalizationService localizationService;
    /**
     * Element interfaces mapped to the implementation created for them.
     */
    private final Map<Class<? extends IElement>, Class<? extends IElement>> elementRegistry = new HashMap<>();

    /**
     * Creates a factory with the built-in elements registered.
     *
     * @param elementFinderService Resolves the elements' selectors on the current tab.
     * @param browserProperties    The configuration, read for the highlight setting.
     * @param localizationService  Builds the elements' localized log messages.
     */
    public ElementFactory(
            IElementFinderService elementFinderService,
            IBrowserProperties browserProperties,
            ILocalizationService localizationService) {
        this.elementFinderService = elementFinderService;
        this.browserProperties = browserProperties;
        this.localizationService = localizationService;
        registerDefaultElements();
    }

    @Override
    public <T extends IElement> IElementCollection<T> createElementsCollection(
            Selector selector,
            String name,
            IElementSupplier<T> supplier,
            ExpectedCount expectedCount) {
        return new ElementCollection<>(selector, name, this, supplier, expectedCount);
    }

    @Override
    public <T extends IElement> IElementCollection<T> createElementsCollection(
            Selector selector,
            String name,
            Class<T> clazz,
            ExpectedCount expectedCount) {
        return createElementsCollection(selector, name, getDefaultElementSupplier(clazz), expectedCount);
    }

    @Override
    public <T extends IElement> IElementCollection<T> createChildElementsCollection(
            IElementSupplier<T> supplier,
            IElement parentElement,
            Selector selector,
            String name,
            ExpectedCount expectedCount) {
        Selector combinedSelector = combineSelectors(parentElement, selector);
        return new ElementCollection<>(combinedSelector, name, this, supplier, expectedCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IElement> IElementCollection<T> createChildElementsCollection(
            Class<T> elementClass,
            IElement parentElement,
            Selector selector,
            String name,
            ExpectedCount expectedCount) {
        return createChildElementsCollection(
                getDefaultElementSupplier(elementClass),
                parentElement,
                selector,
                name,
                expectedCount);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The element is created by reflection through its {@code (Selector, String, IElementFactory)} constructor.
     *
     * @throws IllegalArgumentException if an interface has no registered implementation, or the implementation
     *                                  has no such constructor or fails to construct.
     */
    @Override
    public <T extends IElement> T createCustomElement(Class<T> clazz, Selector locator, String name) {
        IElementSupplier<T> elementSupplier = getDefaultElementSupplier(clazz);
        return createCustomElement(elementSupplier, locator, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHighlightEnabled() {
        return browserProperties.isHighlight();
    }

    /**
     * Maps an element interface to the implementation the factory creates for it, replacing any existing
     * mapping (including a built-in one). The implementation needs a {@code (Selector, String, IElementFactory)}
     * constructor.
     *
     * @param interfaceClass      The element interface.
     * @param implementationClass The class to create for it.
     */
    public void registerElement(
            Class<? extends IElement> interfaceClass,
            Class<? extends IElement> implementationClass) {
        elementRegistry.put(interfaceClass, implementationClass);
    }

    /**
     * Creates a supplier that creates elements of the given type by reflection.
     * <p>
     * Searches for a constructor matching the signature: {@code (Selector locator, String name, IElementFactory factory)}.
     * </p>
     *
     * @param clazz The interface or concrete class of the element to create.
     * @param <T>   The element type.
     * @return An {@link IElementSupplier} capable of instantiating the requested element type.
     */
    private <T extends IElement> IElementSupplier<T> getDefaultElementSupplier(Class<T> clazz) {
        return (locator, name, elementFactory) -> {
            try {
                Constructor<T> ctor = resolveElementClass(clazz)
                        .getDeclaredConstructor(Selector.class, String.class, IElementFactory.class);
                ctor.setAccessible(true);
                T instance = ctor.newInstance(locator, name, this);
                ctor.setAccessible(false);
                return instance;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new IllegalArgumentException("Something went wrong during element casting: " + e.getMessage(), e);
            }
        };
    }

    /**
     * Resolves an element interface to its concrete implementation class.
     * <p>
     * If a concrete class is passed, it is returned directly. If an interface is passed,
     * it looks up the mapped implementation in the registry.
     * </p>
     *
     * @param clazz The class or interface to resolve.
     * @param <T>   The element type.
     * @return The concrete class that implements the requested interface.
     * @throws IllegalArgumentException if an interface is requested that is not mapped in the registry.
     */
    private <T extends IElement> Class<T> resolveElementClass(Class<T> clazz) {
        if (clazz.isInterface() && !elementRegistry.containsKey(clazz)) {
            throw new IllegalArgumentException(
                    String.format("No element implementation is registered for %1$s. "
                            + "Register one with ElementFactory.registerElement(...).", clazz.getName()));
        }

        //noinspection unchecked
        return clazz.isInterface() ? (Class<T>) elementRegistry.get(clazz) : clazz;
    }

    /**
     * Maps the built-in element interfaces to their implementations.
     */
    private void registerDefaultElements() {
        elementRegistry.put(IButtonElement.class, ButtonElement.class);
        elementRegistry.put(ILabelElement.class, LabelElement.class);
        elementRegistry.put(ITextBoxElement.class, TextBoxElement.class);
        elementRegistry.put(IDropdownElement.class, DropdownElement.class);
        elementRegistry.put(ILinkElement.class, LinkElement.class);
        elementRegistry.put(IRadioButtonElement.class, RadioButtonElement.class);
        elementRegistry.put(ICheckBoxElement.class, CheckBoxElement.class);
        elementRegistry.put(IUploadBox.class, UploadBox.class);
    }
}