package org.jcd2052.pages.steam;

import org.jcd2052.browser.services.ElementFactory;
import org.jcd2052.elements.interfaces.IButtonElement;
import org.jcd2052.pages.AbstractPage;

public abstract class BaseSteamPage extends AbstractPage {
    private static final String SUB_MENU_XPATH_TEMPLATE = "//a[contains(@data-tooltip-content, '%s')]";

    protected BaseSteamPage(String name, ElementFactory elementFactory) {
        super("//div[id='global_header']", name, elementFactory);
    }

    public void clickToSubmenu(String submenuName) {
        getLabel()
                .createChildElement(
                        IButtonElement.class,
                        String.format(SUB_MENU_XPATH_TEMPLATE, submenuName),
                        submenuName + " navigate button")
                .click();
    }
}
