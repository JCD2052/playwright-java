package org.jcd2052.core.elements;

import com.microsoft.playwright.Locator;
import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.interfaces.ITextBoxElement;
import org.jcd2052.core.logger.LoggerProvider;

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
     * @param locatorValue   The Playwright selector (e.g., CSS, XPath) used to locate the text box in the DOM.
     * @param name           A human-readable name for the text box (e.g., "Username Input", "Search Bar"), utilized for logging.
     * @param elementFactory The {@link IElementFactory} responsible for managing and resolving locators.
     */
    protected TextBoxElement(String locatorValue, String name, IElementFactory elementFactory) {
        super(locatorValue, name, elementFactory);
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
        String value = getLocator().inputValue();
        LoggerProvider.getLogger().debugElementAction(
                getElementType(),
                getName(),
                "retrieved input value. Result: '%s'",
                value);
        return value;
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
        getLocator().fill(text, new Locator.FillOptions().setForce(true));
        LoggerProvider.getLogger().debugElementAction(getElementType(), getName(), "was filled with text: '%s'", text);
    }

    /**
     * Empties the text box of any current input.
     * <p>This utilizes Playwright's native {@code Locator.clear()} method to ensure
     * the input field is completely empty. The action is logged prior to execution.</p>
     */
    @Override
    public void clearText() {
        getLocator().clear();
        LoggerProvider.getLogger().debugElementAction(getElementType(), getName(), "was cleared");
    }
}