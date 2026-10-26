package io.github.jcd2052.elements.interfaces;

/**
 * Represents a text input element on a web page, such as an {@code <input>} or {@code <textarea>}.
 * <p>
 * This interface extends {@link IElement} and provides specialized methods for
 * retrieving, entering, and clearing text content within interactive fields.
 */
public interface ITextBoxElement extends IElement {

    /**
     * Gets the field's current value.
     * <p>
     * Unlike {@link IElement#getText()}, which reads the element's text content, this reads the input's
     * {@code value} property, which is what the user typed.
     *
     * @return The current string value of the input.
     */
    String getInputValue();

    /**
     * Sets the field's value to the given text, replacing what it held.
     * <p>
     * The built-in implementation uses Playwright's {@code fill()}, which sets the value in one go and fires an
     * {@code input} event, rather than typing key by key.
     * </p>
     *
     * @param text The text to enter.
     */
    void fillText(String text);

    /**
     * Empties the field.
     */
    void clearText();

    /**
     * Replaces the field's value with the given text. By default this clears the field and then fills it;
     * implementations may do it in one step.
     *
     * @param text The text to enter.
     */
    default void clearAndFillText(String text) {
        clearText();
        fillText(text);
    }
}