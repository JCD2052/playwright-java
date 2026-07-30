package io.github.jcd2052.browser.browser;

import com.microsoft.playwright.Dialog;
import io.github.jcd2052.browser.browser.interfaces.IAlert;
import io.github.jcd2052.logger.LoggerProvider;

/**
 * Default {@link IAlert} implementation, wrapping a Playwright {@link Dialog} (JavaScript
 * {@code alert}, {@code confirm}, or {@code prompt}) intercepted via a page's dialog handler.
 */
public class BrowserAlert implements IAlert {
    private final Dialog dialog;

    /**
     * Wraps a native Playwright dialog.
     *
     * @param dialog the intercepted {@link Dialog} instance to delegate to
     */
    public BrowserAlert(Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public String getMessage() {
        return dialog.message();
    }

    @Override
    public void accept() {
        LoggerProvider.getLogger().info("Accepting alert with message: '%s'", getMessage());
        dialog.accept();
    }

    @Override
    public void decline() {
        LoggerProvider.getLogger().info("Declining alert with message: '%s'", getMessage());
        dialog.dismiss();
    }

    @Override
    public void fillText(String text) {
        LoggerProvider.getLogger().info("Filling alert prompt with text: '%s'", text);
        dialog.accept(text);
    }
}
