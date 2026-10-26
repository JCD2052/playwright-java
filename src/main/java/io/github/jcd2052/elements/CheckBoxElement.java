package io.github.jcd2052.elements;

import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.ICheckBoxElement;
import io.github.jcd2052.logger.i18n.LogAction;

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
     * @param selector       The selector the element is found with.
     * @param name           A human-readable name for the checkbox (e.g., "Accept Terms"), used in log messages and errors.
     * @param elementFactory The factory creating the element.
     */
    protected CheckBoxElement(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    /**
     * Checks the checkbox. Does nothing if it is already checked.
     */
    @Override
    public void check() {
        highlightElementIfNeeded();
        executeAction(() -> getLocator().check(), LogAction.ELEMENT_CHECKED);
    }

    /**
     * Unchecks the checkbox. Does nothing if it is already unchecked.
     */
    @Override
    public void uncheck() {
        highlightElementIfNeeded();
        executeAction(() -> getLocator().uncheck(), LogAction.ELEMENT_UNCHECKED);
    }
}