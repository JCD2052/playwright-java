package org.jcd2052.core.browser.services.interfaces;


import org.jcd2052.core.elements.interfaces.IElement;

/**
 * A functional interface used to supply instances of specific UI elements.
 * <p>
 * This supplier acts as a factory method for creating custom implementations of {@link IElement}.
 * It allows developers to define explicitly how an element should be instantiated,
 * bypassing default reflection-based creation in the element factory.
 *
 * @param <T> The specific type of the element being supplied, which must extend {@link IElement}.
 */
@FunctionalInterface
public interface IElementSupplier<T extends IElement> {

    /**
     * Instantiates and returns a new web element of type {@code T}.
     *
     * @param locator        The locator strategy (e.g., XPath, CSS selector) used to find the element.
     * @param name           The logical name of the element, primarily used for logging and reporting.
     * @param elementFactory The {@link IElementFactory} instance injecting dependencies into the element.
     * @return A newly created instance of the specific element type {@code T}.
     */
    T get(String locator, String name, IElementFactory elementFactory);
}