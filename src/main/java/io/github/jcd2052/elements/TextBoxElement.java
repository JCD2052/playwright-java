package io.github.jcd2052.elements;

import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.ITextBoxElement;
import io.github.jcd2052.logger.i18n.LogAction;

/**
 * A concrete representation of a text input field or text area within the web application.
 * <p>This class extends {@link AbstractElement} to inherit standard interactions
 * (such as clicking and state evaluation) and implements {@link ITextBoxElement}
 * to provide specific methods for reading, entering, and clearing text within
 * form controls like {@code <input type="text">} or {@code <textarea>}.</p>
 */
public class TextBoxElement extends AbstractElement implements ITextBoxElement {
    /**
     * Constructs a new {@code TextBoxElement}.
     *
     * @param selector       The selector the element is found with.
     * @param name           A human-readable name for the text box (e.g., "Username Input", "Search Bar"), used in log messages and errors.
     * @param elementFactory The factory creating the element.
     */
    protected TextBoxElement(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    /**
     * Gets the text box's current value. Unlike {@code getText()}, which reads the element's text content, this
     * reads the input's {@code value} property, which is what the user typed.
     *
     * @return The current value.
     */
    @Override
    public String getInputValue() {
        highlightElementIfNeeded();
        return executeActionReturning(() -> getLocator().inputValue(), LogAction.INPUT_VALUE_READ);
    }

    /**
     * Sets the text box's value to the given text, replacing what it held. The value is set in one go and an
     * {@code input} event is fired; to send individual key presses, use {@code press(...)} or the tab's
     * keyboard.
     *
     * @param text The text to enter.
     */
    @Override
    public void fillText(String text) {
        highlightElementIfNeeded();
        executeAction(() -> getLocator().fill(text), LogAction.TEXT_ENTERED, text);
    }

    /**
     * Empties the text box.
     */
    @Override
    public void clearText() {
        highlightElementIfNeeded();
        executeAction(() -> getLocator().clear(), LogAction.TEXT_CLEARED);
    }

    /**
     * Replaces the text box's value with the given text, logged as clearing and filling.
     *
     * @param text The text to enter.
     */
    @Override
    public void clearAndFillText(String text) {
        highlightElementIfNeeded();
        // Locator.fill() already replaces the current value, so no separate clear() is needed.
        executeAction(() -> getLocator().fill(text), LogAction.TEXT_CLEARED_AND_FILLED, text);
    }
}