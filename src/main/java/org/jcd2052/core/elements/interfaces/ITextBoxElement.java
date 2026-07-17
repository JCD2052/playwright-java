package org.jcd2052.core.elements.interfaces;

/**
 * Represents a text input element on a web page, such as an {@code <input>} or {@code <textarea>}.
 * <p>
 * This interface extends {@link IElement} and provides specialized methods for
 * retrieving, entering, and clearing text content within interactive fields.
 */
public interface ITextBoxElement extends IElement {

    /**
     * Retrieves the current value typed into the input field.
     * <p>
     * Unlike {@link IElement#getText()}, which typically returns inner text,
     * this method retrieves the live "value" property of the input element.
     *
     * @return The current string value of the input.
     */
    String getInputValue();

    /**
     * Enters the specified text into the field.
     * <p>
     * <b>Implementation Note:</b> This typically utilizes Playwright's {@code fill()}
     * mechanism, which focuses the element and sends a series of input events.
     *
     * @param text The string to be entered into the text box.
     */
    void fillText(String text);

    /**
     * Clears all existing text from the input field.
     */
    void clearText();

    /**
     * A convenience method that first clears the field and then enters the new text.
     * <p>
     * This is the recommended approach for updating form fields to ensure
     * that new data is not simply appended to existing stale text.
     *
     * @param text The new string to be entered.
     */
    default void clearAndFillText(String text) {
        clearText();
        fillText(text);
    }
}