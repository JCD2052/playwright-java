package org.jcd2052.steam.pages;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.elements.interfaces.ILabelElement;
import org.jcd2052.core.elements.interfaces.ITextBoxElement;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SteamSearchResultPage extends BaseSteamPage {
    private final ILabelElement searchLabelTags;
    private final ITextBoxElement storeSearchBox;

    protected SteamSearchResultPage(IElementFactory elementFactory) {
        super("Steam Search Result Page", elementFactory);
        this.searchLabelTags = getElementFactory().createLabelElement(
                Selector.bySelector("//div[contains(@class, 'searchtag tag_dynamic')]"),
                "Search Tags");
        this.storeSearchBox = getElementFactory().createTextBoxElement(
                Selector.bySelector("//input[@id='term']"),
                "Store search box");
    }

    public String getValueFromSearch() {
        return storeSearchBox.getInputValue();
    }

    public List<String> getSearchTags() {
        return searchLabelTags.getAllTexts();
    }
}
