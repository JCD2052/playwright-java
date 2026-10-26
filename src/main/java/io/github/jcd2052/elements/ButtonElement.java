package io.github.jcd2052.elements;

import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.IButtonElement;

/**
 * The built-in {@link IButtonElement}: a clickable element, with the common element API from
 * {@link AbstractElement}.
 */
public class ButtonElement extends AbstractElement implements IButtonElement {
    /**
     * Constructs a new {@code ButtonElement}.
     *
     * @param selector       The selector the element is found with.
     * @param name           A human-readable name for the button (e.g., "Submit Button", "Login"), used in log messages and errors.
     * @param elementFactory The factory creating the element.
     */
    protected ButtonElement(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }
}