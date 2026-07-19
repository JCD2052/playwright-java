package org.jcd2052.steam.pages;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.elements.interfaces.IButtonElement;
import org.jcd2052.core.elements.interfaces.ITextBoxElement;
import org.springframework.stereotype.Component;

@Component
public class SteamStorePage extends BaseSteamPage {
    private static final String SEARCH_BASE_XPATH = "//form[contains(@action, 'store')]";
    private final ITextBoxElement generalSearchBox;
    private final IButtonElement searchButton;

    protected SteamStorePage(IElementFactory elementFactory) {
        super("Store page", elementFactory);
        this.generalSearchBox = getElementFactory().createTextBoxElement(
                Selector.bySelector(SEARCH_BASE_XPATH + "//input[@role='combobox']"),
                "Search box");
        this.searchButton = getElementFactory().createButtonElement(
                Selector.bySelector(SEARCH_BASE_XPATH + "//button[@type='submit']"),
                "Search button");
    }

    public void performSearch(String searchValue) {
        generalSearchBox.clearAndFillText(searchValue);
        searchButton.click();
    }

    public String getValueFromSearch() {
        return generalSearchBox.getInputValue();
    }
}
