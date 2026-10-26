package io.github.jcd2052.elements.interfaces;

import java.util.List;

/**
 * Represents a dropdown or select element on a web page.
 * <p>
 * It works with native HTML {@code <select>} elements: options are read from their {@code <option>} children and
 * selected through Playwright's {@code selectOption}. Custom dropdowns built from other elements need a custom
 * element.
 */
public interface IDropdownElement extends IElement {

    /**
     * Gets the text of the selected option.
     *
     * @return The selected option's visible text.
     */
    String getSelectedOption();

    /**
     * Gets the text of every option, in order.
     *
     * @return The options' texts.
     */
    List<String> getTexts();

    /**
     * Selects the option whose {@code value} attribute matches exactly.
     *
     * @param option The option's {@code value}.
     */
    void selectByValue(String option);

    /**
     * Selects the option at the given position.
     *
     * @param index The zero-based option index.
     */
    void selectByIndex(int index);

    /**
     * Selects the option whose visible text (label) matches exactly.
     *
     * @param text The option's text.
     */
    void selectByText(String text);
}