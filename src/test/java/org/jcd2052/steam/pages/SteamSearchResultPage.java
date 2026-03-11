package org.jcd2052.steam.pages;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.interfaces.ILabelElement;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SteamSearchResultPage extends BaseSteamPage {
    private final ILabelElement searchLabelTags;

    protected SteamSearchResultPage(IElementFactory elementFactory) {
        super("Steam Search Result Page", elementFactory);
        this.searchLabelTags = getElementFactory().createLabelElement("#searchtag_tmpl", "Search Tags");
    }

    public Collection<String> getSearchTags() {
        return searchLabelTags.getAllTexts();
    }
}
