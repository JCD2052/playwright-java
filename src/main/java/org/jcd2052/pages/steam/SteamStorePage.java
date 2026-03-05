package org.jcd2052.pages.steam;

import org.jcd2052.browser.services.ElementFactory;
import org.jcd2052.elements.interfaces.IButtonElement;
import org.jcd2052.elements.interfaces.ITextBoxElement;
import org.springframework.stereotype.Component;

@Component
public class SteamStorePage extends BaseSteamPage {
    private final ITextBoxElement searchBox;
    private final IButtonElement searchButton;

    protected SteamStorePage(ElementFactory elementFactory) {
        super("Store page", elementFactory);
        this.searchBox = getElementFactory().createTextBoxElement("#store_nav_search_term", "Search box");
        this.searchButton = getElementFactory().createButtonElement("#store_search_link", "Search button");
    }

    public void performSearch(String searchValue) {
        searchBox.clearAndFillText(searchValue);
        searchButton.click();
    }

    public String getValueFromSearch() {
        return searchBox.getText();
    }
}
