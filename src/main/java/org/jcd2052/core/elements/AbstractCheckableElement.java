package org.jcd2052.core.elements;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.interfaces.ICheckableElement;
import org.jcd2052.core.logger.LoggerProvider;

/**
 * An abstract base class representing a web element that maintains a checkable state,
 * such as a checkbox, radio button, or toggle switch.
 * <p>This class extends {@link AbstractElement} to inherit core Playwright locator
 * management and implements {@link ICheckableElement} to define standard behaviors
 * for elements that can be checked or unchecked.</p>
 */
public abstract class AbstractCheckableElement extends AbstractElement implements ICheckableElement {
    /**
     * Constructs a new {@code AbstractCheckableElement}.
     *
     * @param selector       The Playwright selector (e.g., CSS, XPath) used to locate the element in the DOM.
     * @param name           A human-readable name for the element, utilized for logging and test reporting.
     * @param elementFactory The {@link IElementFactory} responsible for instantiating and managing locators.
     */
    protected AbstractCheckableElement(String selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    /**
     * Determines whether the element is currently in a checked state.
     * <p>This method performs an evaluation against the DOM using Playwright's native
     * {@code Locator.isChecked()} method. The action is logged prior to execution.</p>
     *
     * @return {@code true} if the element is checked; {@code false} otherwise.
     */
    @Override
    public boolean isChecked() {
        boolean checked = getLocator().isChecked();
        LoggerProvider.getLogger().debugElementAction(
                getElementType(),
                getName(),
                "checked if is checked. Result: %b",
                checked);
        return checked;
    }
}