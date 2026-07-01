package org.jcd2052.core.browser.browser;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Page;
import org.jcd2052.core.browser.browser.interfaces.IAlert;
import org.jcd2052.core.logger.LoggerProvider;

public class BrowserAlert implements IAlert {
    private final Page page;

    public BrowserAlert(Page page) {
        this.page = page;
    }

    @Override
    public String getMessage() {
        StringBuffer stringBuffer = new StringBuffer();
        page.onDialog(dialog -> stringBuffer.append(dialog.message()));
        return stringBuffer.toString();
    }

    @Override
    public void accept() {
        LoggerProvider.getLogger().info("Accepting alert with message: '%s'", getMessage());
        page.onDialog(Dialog::accept);
    }

    @Override
    public void decline() {
        LoggerProvider.getLogger().info("Declining alert with message: '%s'", getMessage());
        page.onDialog(Dialog::dismiss);
    }

    @Override
    public void fillText(String text) {
        LoggerProvider.getLogger().info("Filling alert prompt with text: '%s'", text);
        page.onDialog(dialog -> dialog.accept(text));
    }
}
