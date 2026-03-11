package org.jcd2052.core.elements.interfaces;

import java.util.List;

/**
 * Represents a collection of UI elements matching a common selector.
 * <p>
 * This interface provides a way to interact with multiple similar elements on a page,
 * such as list items, table rows, or multiple buttons. It is generic, ensuring that
 * the elements within the collection are of a specific type (e.g., {@link IButtonElement}).
 *
 * @param <T> The type of {@link IElement} contained within this collection.
 */
public interface IElementCollection<T extends IElement> {

    /**
     * Retrieves all elements in the collection as a {@link List}.
     * <p>
     * Implementation note: This method typically triggers the element discovery process
     * using the underlying selector to find all matching instances currently present
     * on the page.
     *
     * @return A list of instantiated elements of type {@code T}.
     */
    List<T> getElements();
}