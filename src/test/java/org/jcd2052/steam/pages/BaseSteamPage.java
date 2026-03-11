package org.jcd2052.steam.pages;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.interfaces.IButtonElement;
import org.jcd2052.core.pages.AbstractForm;

public abstract class BaseSteamPage extends AbstractForm {
    private static final String SUB_MENU_XPATH_TEMPLATE = "//a[contains(@data-tooltip-content, '%s')]";

    protected BaseSteamPage(String name, IElementFactory elementFactory) {
        super("//div[id='global_header']", name, elementFactory);
    }

    public void clickToSubmenu(String submenuName) {
        getFormLabel()
                .createChildElement(
                        IButtonElement.class,
                        String.format(SUB_MENU_XPATH_TEMPLATE, submenuName),
                        submenuName + " navigate button")
                .click();
    }
}
