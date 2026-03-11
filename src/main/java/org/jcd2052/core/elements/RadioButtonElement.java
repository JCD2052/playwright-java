package org.jcd2052.core.elements;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.interfaces.IRadioButtonElement;

/**
 * A concrete representation of a radio button element within the web application.
 * <p>This class extends {@link AbstractCheckableElement} to inherit the base
 * state-checking capabilities (such as evaluating if the radio button is selected
 * via {@code isChecked()}) and implements {@link IRadioButtonElement} to fulfill
 * any specific behavioral contracts defined by the framework for radio inputs.</p>
 */
public class RadioButtonElement extends AbstractCheckableElement implements IRadioButtonElement {

    /**
     * Constructs a new {@code RadioButtonElement}.
     *
     * @param selector       The Playwright selector (e.g., CSS, XPath) used to locate the radio button in the DOM.
     * @param name           A human-readable name for the radio button (e.g., "Credit Card Option", "Gender: Male"), utilized for logging.
     * @param elementFactory The {@link IElementFactory} responsible for managing and resolving locators.
     */
    protected RadioButtonElement(String selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }
}