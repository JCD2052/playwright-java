package io.github.jcd2052.elements;

import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.ICheckableElement;
import io.github.jcd2052.logger.i18n.LogAction;

/**
 * Base class for elements with a checked state, such as checkboxes, radio buttons and toggle switches.
 * It provides reading the checked state; elements that can also be checked and unchecked directly (like
 * {@link CheckBoxElement}) add that themselves.
 */
public abstract class AbstractCheckableElement extends AbstractElement implements ICheckableElement {
    /**
     * Creates a checkable element.
     *
     * @param selector       The selector the element is found with.
     * @param name           The element's name, used in log messages and errors.
     * @param elementFactory The factory creating the element.
     */
    protected AbstractCheckableElement(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    /**
     * Checks whether the element is checked right now, without waiting.
     *
     * @return {@code true} if the element is currently checked, {@code false} otherwise.
     */
    @Override
    public boolean isChecked() {
        highlightElementIfNeeded();
        return executeActionReturning(() -> getLocator().isChecked(), LogAction.ELEMENT_IS_CHECKED);
    }
}