package io.github.jcd2052.elements;

import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.ICheckableElement;
import io.github.jcd2052.logger.LoggerProvider;

/**
 * Base abstract implementation for UI elements that can hold a checked state
 * (e.g., checkboxes, radio buttons, toggle switches).
 * <p>
 * Provides standard interactions for checking, unchecking, and state evaluation,
 * integrated with framework-level logging, highlighting, and JIT element resolution.
 * </p>
 */
public abstract class AbstractCheckableElement extends AbstractElement implements ICheckableElement {
    /**
     * Initializes the checkable element.
     *
     * @param selector       The Just-In-Time Selector locator strategy used to find the element.
     * @param name           The human-readable name of the element used in logging.
     * @param elementFactory The factory responsible for resolving element dependencies and locators.
     */
    protected AbstractCheckableElement(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    /**
     * Evaluates the current checked state of the element in the DOM.
     *
     * @return {@code true} if the element is currently checked, {@code false} otherwise.
     */
    @Override
    public boolean isChecked() {
        highlightElementIfNeeded();
        boolean checked = executeActionReturning(() -> getLocator().isChecked(), "isChecked");
        LoggerProvider.getLogger().debugElementAction(
                getElementType(),
                getName(),
                "evaluated 'isChecked' state. Result: %b",
                checked);
        return checked;
    }
}