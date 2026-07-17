package org.jcd2052.steam.pages;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.pages.AbstractForm;

public abstract class BaseSteamPage extends AbstractForm {
    protected BaseSteamPage(String name, IElementFactory elementFactory) {
        super("//div[@id='global_header']", name, elementFactory);
    }
}
