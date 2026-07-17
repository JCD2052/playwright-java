package org.jcd2052.core.elements;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.interfaces.ICheckBoxElement;import org.jcd2052.core.logger.LoggerProvider;

/**
 * A concrete representation of a checkbox element within the web application.
 * <p>This class extends {@link AbstractCheckableElement} to inherit the base state-checking
 * capabilities (like {@code isChecked()}) and implements {@link ICheckBoxElement} to provide
 * the specific actions required to interact with checkboxes, namely checking and unchecking.</p>
 */
public class CheckBoxElement extends AbstractCheckableElement implements ICheckBoxElement {
    /**
     * Constructs a new {@code CheckBoxElement}.
     *
     * @param selector       The Playwright selector (e.g., CSS, XPath) used to locate the checkbox in the DOM.
     * @param name           A human-readable name for the checkbox (e.g., "Accept Terms"), used for logging and reporting.
     * @param elementFactory The {@link IElementFactory} responsible for creating and locating the element.
     */
    protected CheckBoxElement(String selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    /**
     * Checks the checkbox element.
     * <p>This method ensures the checkbox is selected. If it is already checked, this action
     * typically does nothing, delegating the state management to Playwright's native
     * {@code Locator.check()} method. The action is logged prior to execution.</p>
     */
    @Override
    public void check() {
        getLocator().check();
          LoggerProvider.getLogger().debugElementAction(getElementType(), getName(), "was successfully checked");
    }

    /**
     * Unchecks the checkbox element.
     * <p>This method ensures the checkbox is deselected. If it is already unchecked, this action
     * typically does nothing, delegating the state management to Playwright's native
     * {@code Locator.uncheck()} method. The action is logged prior to execution.</p>
     */
    @Override
    public void uncheck() {
        getLocator().uncheck();
          LoggerProvider.getLogger().debugElementAction(getElementType(), getName(), "was successfully unchecked");
    }
}