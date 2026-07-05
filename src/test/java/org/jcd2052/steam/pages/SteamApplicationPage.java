package org.jcd2052.steam.pages;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.interfaces.ILabelElement;
import org.jcd2052.core.pages.AbstractForm;
import org.springframework.stereotype.Component;

@Component
public class SteamApplicationPage extends AbstractForm {
    private final ILabelElement applicationNameLabel = getElementFactory().createLabelElement(
            "#appHubAppName",
            "Application Name");

    protected SteamApplicationPage(IElementFactory elementFactory) {
        super("//div[contains(@class, 'SiteInfo')]", "Application Page", elementFactory);
    }

    public String getApplicationName() {
        return applicationNameLabel.getText();
    }
}
