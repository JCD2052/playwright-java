package org.jcd2052.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;
import org.jcd2052.browser.services.interfaces.IElementFactory;
import org.jcd2052.elements.interfaces.IDropdownElement;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DropdownElement extends AbstractElement implements IDropdownElement {
    private static final String SELECTOR_FOR_SELECTED_OPTION = "option:checked";

    protected DropdownElement(String selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    @Override
    public String getSelectedOption() {
        return getLocator().locator(SELECTOR_FOR_SELECTED_OPTION).innerText();
    }

    @Override
    public List<String> getTexts() {
        return getLocator()
                .locator("option")
                .all()
                .stream()
                .map(Locator::textContent)
                .collect(Collectors.toList());
    }

    @Override
    public void selectByValue(String option) {
        selectByOption(selectOption -> selectOption.setValue(option));
    }

    @Override
    public void selectByIndex(int index) {
        selectByOption(selectOption -> selectOption.setIndex(index));
    }

    @Override
    public void selectByText(String text) {
        selectByOption(selectOption -> selectOption.setLabel(text));
    }

    private void selectByOption(Function<SelectOption, SelectOption> selectOption) {
        getLocator().selectOption(selectOption.apply(new SelectOption()));
    }
}
