package org.jcd2052.core.elements.interfaces;

/**
 * Defines the contract for UI elements that possess a binary checked or selected state.
 * <p>
 * This interface is typically implemented by components such as checkboxes and
 * radio buttons, providing a standard mechanism to query their current toggle status.
 */
public interface ICheckableElement extends IElement {

    /**
     * Determines whether the element is currently in a checked or selected state.
     * <p>
     * For a checkbox, this indicates if the checkmark is present. For a radio button,
     * it indicates if the option is currently selected.
     *
     * @return {@code true} if the element is checked/selected, {@code false} otherwise.
     */
    boolean isChecked();
}