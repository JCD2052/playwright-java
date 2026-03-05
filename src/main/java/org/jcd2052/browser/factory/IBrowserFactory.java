package org.jcd2052.browser.factory;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;

public interface IBrowserFactory {
    Browser createBrowser(Playwright playwright);
}
