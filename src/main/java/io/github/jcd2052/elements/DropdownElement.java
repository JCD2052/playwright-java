package io.github.jcd2052.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;
import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.IDropdownElement;
import io.github.jcd2052.logger.i18n.LogAction;

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
     * @param selector       The selector the element is found with.
     * @param name           A human-readable name for the dropdown (e.g., "Country Selector"), used in log messages and errors.
     * @param elementFactory The factory creating the element.
     */
    protected DropdownElement(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    /**
     * Gets the rendered text of the selected option.
     *
     * @return The text of the selected option.
     */
    @Override
    public String getSelectedOption() {
        return executeActionReturning(
                () -> getLocator().locator(SELECTOR_FOR_SELECTED_OPTION).innerText(),
                LogAction.DROPDOWN_OPTION_READ);
    }

    /**
     * Gets the text of every option, in order.
     *
     * @return A list of strings representing the text of every {@code <option>} element.
     */
    @Override
    public List<String> getTexts() {
        return executeActionReturning(
                () -> getLocator()
                        .locator("option")
                        .all()
                        .stream()
                        .map(Locator::textContent)
                        .collect(Collectors.toList()),
                LogAction.DROPDOWN_OPTIONS_READ);
    }

    /**
     * Selects the option whose {@code value} attribute matches exactly.
     *
     * @param option The option's {@code value}.
     */
    @Override
    public void selectByValue(String option) {
        executeAction(
                () -> selectByOption(selectOption -> selectOption.setValue(option)),
                LogAction.OPTION_SELECTED_BY_VALUE,
                option);
    }

    /**
     * Selects the option at the given position.
     *
     * @param index The zero-based index of the option to select.
     */
    @Override
    public void selectByIndex(int index) {
        executeAction(
                () -> selectByOption(selectOption -> selectOption.setIndex(index)),
                LogAction.OPTION_SELECTED_BY_INDEX,
                index);
    }

    /**
     * Selects the option whose visible text (label) matches exactly.
     *
     * @param text The option's text.
     */
    @Override
    public void selectByText(String text) {
        executeAction(
                () -> selectByOption(selectOption -> selectOption.setLabel(text)),
                LogAction.OPTION_SELECTED_BY_TEXT,
                text);
    }

    /**
     * Selects the option described by the given {@link SelectOption}.
     *
     * @param selectOption Configures which option to select.
     */
    private void selectByOption(Function<SelectOption, SelectOption> selectOption) {
        getLocator().selectOption(selectOption.apply(new SelectOption()));
    }
}