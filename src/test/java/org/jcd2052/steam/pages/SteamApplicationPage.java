package org.jcd2052.steam.pages;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.elements.interfaces.ILabelElement;
import org.jcd2052.core.pages.AbstractForm;
import org.springframework.stereotype.Component;

@Component
public class SteamApplicationPage extends AbstractForm {
    private final ILabelElement applicationNameLabel = getElementFactory().createLabelElement(
            Selector.bySelector("#appHubAppName"),
            "Application Name");

    protected SteamApplicationPage(IElementFactory elementFactory) {
        super(Selector.bySelector("//div[contains(@class, 'SiteInfo')]"), "Application Page", elementFactory);
    }

    public String getApplicationName() {
        return applicationNameLabel.getText();
    }
}
