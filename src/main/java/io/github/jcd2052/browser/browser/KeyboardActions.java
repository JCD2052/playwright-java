package io.github.jcd2052.browser.browser;

import com.microsoft.playwright.Keyboard;
import io.github.jcd2052.browser.browser.interfaces.IKeyboardActions;
import io.github.jcd2052.logger.LoggerProvider;

/**
 * Default {@link IKeyboardActions} implementation, wrapping Playwright's page-level {@link Keyboard}.
 */
public class KeyboardActions implements IKeyboardActions {
    private final Keyboard keyboard;

    /**
     * Wraps the given page's keyboard.
     *
     * @param keyboard the Playwright {@link Keyboard} instance to delegate to
     */
    public KeyboardActions(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    @Override
    public void press(String key) {
        LoggerProvider.getLogger().info("Page-level key press: '%s'", key);
        keyboard.press(key);
    }

    @Override
    public void type(String text) {
        LoggerProvider.getLogger().info("Page-level typing: '%s'", text);
        keyboard.type(text);
    }

    @Override
    public void insertText(String text) {
        LoggerProvider.getLogger().info("Page-level text insert: '%s'", text);
        keyboard.insertText(text);
    }

    @Override
    public void down(String key) {
        keyboard.down(key);
    }

    @Override
    public void up(String key) {
        keyboard.up(key);
    }
}
