package org.jcd2052.core.elements.interfaces;

/**
 * Represents a standard checkbox element on a web page.
 * <p>
 * This interface extends {@link ICheckableElement}, inheriting the ability to
 * query the checked state, and adds methods to explicitly set the checkbox
 * to a checked or unchecked state.
 */
public interface ICheckBoxElement extends ICheckableElement {

    /**
     * Ensures that the checkbox is checked.
     * <p>
     * If the checkbox is already checked, this method typically performs no action.
     * If it is unchecked, it will perform the necessary click or toggle action.
     */
    void check();

    /**
     * Ensures that the checkbox is unchecked.
     * <p>
     * If the checkbox is already unchecked, this method typically performs no action.
     * If it is checked, it will perform the necessary click or toggle action to clear it.
     */
    void uncheck();
}