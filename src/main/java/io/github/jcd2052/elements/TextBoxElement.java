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
     * @param selector       The Playwright Selector locator strategy used to find the element.
     * @param name           A human-readable name for the text box (e.g., "Username Input", "Search Bar"), utilized for logging.
     * @param elementFactory The {@link IElementFactory} responsible for managing and resolving locators.
     */
    protected TextBoxElement(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    /**
     * Retrieves the current text value entered into the text box.
     * <p>Unlike {@code getText()}, which retrieves the inner text of an element,
     * this method accurately retrieves the underlying HTML {@code value} property
     * of the input field using Playwright's native {@code Locator.inputValue()}.</p>
     *
     * @return The current string value present in the text box.
     */
    @Override
    public String getInputValue() {
        highlightElementIfNeeded();
        return executeActionReturning(() -> getLocator().inputValue(), LogAction.INPUT_VALUE_READ);
    }

    /**
     * Enters the specified text into the text box.
     * <p>This method utilizes Playwright's native {@code Locator.fill()} method,
     * which typically focuses the element, clears any existing text, and then
     * simulates typing the new value. The action and the entered text are logged.</p>
     *
     * @param text The string value to enter into the text box.
     */
    @Override
    public void fillText(String text) {
        highlightElementIfNeeded();
        executeAction(() -> getLocator().fill(text), LogAction.TEXT_ENTERED, text);
    }

    /**
     * Empties the text box of any current input.
     * <p>This utilizes Playwright's native {@code Locator.clear()} method to ensure
     * the input field is completely empty. The action is logged prior to execution.</p>
     */
    @Override
    public void clearText() {
        highlightElementIfNeeded();
        executeAction(() -> getLocator().clear(), LogAction.TEXT_CLEARED);
    }

    /**
     * Clears any existing text and enters the specified text into the text box.
     *
     * @param text The string value to enter after clearing.
     */
    @Override
    public void clearAndFillText(String text) {
        highlightElementIfNeeded();
        // Locator.fill() already replaces the current value, so no separate clear() is needed.
        executeAction(() -> getLocator().fill(text), LogAction.TEXT_CLEARED_AND_FILLED, text);
    }
}