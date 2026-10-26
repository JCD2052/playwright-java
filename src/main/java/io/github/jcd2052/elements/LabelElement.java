package io.github.jcd2052.elements;

import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.ILabelElement;

/**
 * The built-in {@link ILabelElement}: any element read for its text, attributes or state, with the common
 * element API from {@link AbstractElement}. Also the usual type for containers that child elements are looked
 * up in.
 */
public class LabelElement extends AbstractElement implements ILabelElement {
    /**
     * Constructs a new {@code LabelElement}.
     *
     * @param selector       The selector the element is found with.
     * @param name           A human-readable name for the label (e.g., "Error Message", "Username Label"), used in log messages and errors.
     * @param elementFactory The factory creating the element.
     */
    protected LabelElement(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }
}