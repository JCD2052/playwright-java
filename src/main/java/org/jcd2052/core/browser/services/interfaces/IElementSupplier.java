package org.jcd2052.core.browser.services.interfaces;

import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.elements.interfaces.IElement;

/**
 * Represents a supplier for instantiating UI elements.
 * <p>
 * This functional interface allows for the custom instantiation of elements
 * using a specific Just-In-Time (JIT) locator strategy, a logical name,
 * and the element factory.
 * </p>
 *
 * @param <T> the specific type of the element being supplied, which must extend {@link IElement}
 */
@FunctionalInterface
public interface IElementSupplier<T extends IElement> {
    /**
     * Instantiates or retrieves a new UI element based on the provided locator strategy.
     *
     * @param selector the Just-In-Time Selector locator strategy used to find the element
     * @param name     the descriptive, logical name of the element
     * @param factory  the element factory, useful if the element needs to construct its own children
     * @return the instantiated UI element of type {@code T}
     */
    T get(Selector selector, String name, IElementFactory factory);
}