package org.jcd2052.core.elements.interfaces;

import java.util.List;

/**
 * Represents a dropdown or select element on a web page.
 * <p>
 * This interface provides a comprehensive set of methods to interact with
 * standard HTML {@code <select>} elements or custom dropdown components,
 * allowing for selection via multiple strategies and data retrieval.
 */
public interface IDropdownElement extends IElement {

    /**
     * Retrieves the text of the currently selected option in the dropdown.
     *
     * @return The visible text of the active selection.
     */
    String getSelectedOption();

    /**
     * Retrieves a list of all available options (visible texts) within the dropdown.
     *
     * @return A {@link List} of strings representing all selectable options.
     */
    List<String> getTexts();

    /**
     * Selects an option based on its "value" attribute.
     *
     * @param option The value attribute string of the option to select.
     */
    void selectByValue(String option);

    /**
     * Selects an option based on its zero-based index in the list.
     *
     * @param index The integer index of the option to select.
     */
    void selectByIndex(int index);

    /**
     * Selects an option based on its visible inner text.
     *
     * @param text The visible text string of the option to select.
     */
    void selectByText(String text);
}