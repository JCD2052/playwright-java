package io.github.jcd2052.elements.interfaces;

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
     * Finds the elements currently matching the collection's selector, after waiting for its expected count.
     * <p>
     * Each returned element is bound to its position among the matches, so read the collection again after
     * the page changes.
     * </p>
     *
     * @return A list of instantiated elements of type {@code T}.
     */
    List<T> getElements();
}