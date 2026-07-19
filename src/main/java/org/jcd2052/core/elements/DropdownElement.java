package org.jcd2052.core.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;
import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.elements.interfaces.IDropdownElement;
import org.jcd2052.core.logger.LoggerProvider;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A concrete representation of a standard HTML select dropdown element.
 * <p>This class extends {@link AbstractElement} to inherit core element behaviors
 * and implements {@link IDropdownElement} to provide specific methods for reading
 * and selecting options within a {@code <select>} element using Playwright's native
 * {@link SelectOption} API.</p>
 */
public class DropdownElement extends AbstractElement implements IDropdownElement {
    /**
     * The CSS pseudo-class selector used to identify the currently selected option.
     */
    private static final String SELECTOR_FOR_SELECTED_OPTION = "option:checked";

    /**
     * Constructs a new {@code DropdownElement}.
     *
     * @param selector       The Playwright Selector locator strategy used to find the element.
     * @param name           A human-readable name for the dropdown (e.g., "Country Selector"), used for logging.
     * @param elementFactory The {@link IElementFactory} responsible for creating and locating the element.
     */
    protected DropdownElement(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    /**
     * Retrieves the visible inner text of the currently selected option in the dropdown.
     *
     * @return The text of the selected option.
     */
    @Override
    public String getSelectedOption() {
        return getLocator().locator(SELECTOR_FOR_SELECTED_OPTION).innerText();
    }

    /**
     * Retrieves the visible text content of all available options within the dropdown.
     *
     * @return A list of strings representing the text of every {@code <option>} element.
     */
    @Override
    public List<String> getTexts() {
        return getLocator()
                .locator("option")
                .all()
                .stream()
                .map(Locator::textContent)
                .collect(Collectors.toList());
    }

    /**
     * Selects an option in the dropdown based on its HTML {@code value} attribute.
     *
     * @param option The exact string value of the {@code value} attribute to select.
     */
    @Override
    public void selectByValue(String option) {
        selectByOption(selectOption -> selectOption.setValue(option));
        LoggerProvider.getLogger().debugElementAction(
                getElementType(),
                getName(),
                "selecting option by value: '%s'",
                option);
    }

    /**
     * Selects an option in the dropdown based on its zero-based index.
     *
     * @param index The zero-based index of the option to select.
     */
    @Override
    public void selectByIndex(int index) {
        selectByOption(selectOption -> selectOption.setIndex(index));
        LoggerProvider.getLogger().debugElementAction(
                getElementType(),
                getName(),
                "selecting option by index: %d",
                index);
    }

    /**
     * Selects an option in the dropdown based on its visible text (label).
     *
     * @param text The exact visible text of the option to select.
     */
    @Override
    public void selectByText(String text) {
        selectByOption(selectOption -> selectOption.setLabel(text));
        LoggerProvider.getLogger().debugElementAction(
                getElementType(),
                getName(),
                "selecting option by text: '%s'",
                text);
    }

    /**
     * A private helper method that applies the configured {@link SelectOption}
     * to the underlying Playwright locator.
     *
     * @param selectOption A function that configures and returns a {@code SelectOption} instance.
     */
    private void selectByOption(Function<SelectOption, SelectOption> selectOption) {
        getLocator().selectOption(selectOption.apply(new SelectOption()));
    }
}