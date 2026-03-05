package org.jcd2052.pages.steam;

import org.jcd2052.browser.services.ElementFactory;
import org.jcd2052.elements.interfaces.ILabelElement;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SteamSearchResultPage extends BaseSteamPage {
    private final ILabelElement searchLabelTags;

    protected SteamSearchResultPage(ElementFactory elementFactory) {
        super("Steam Search Result Page", elementFactory);
        this.searchLabelTags = getElementFactory().createLabelElement("#searchtag_tmpl", "Search Tags");
    }

    public Collection<String> getSearchTags() {
        return searchLabelTags.getAllTexts();
    }
}
