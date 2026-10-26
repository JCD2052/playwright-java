package io.github.jcd2052.elements;

import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.IRadioButtonElement;

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
     * @param selector       The selector the element is found with.
     * @param name           A human-readable name for the radio button (e.g., "Credit Card Option", "Gender: Male"), used in log messages and errors.
     * @param elementFactory The factory creating the element.
     */
    protected RadioButtonElement(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }
}