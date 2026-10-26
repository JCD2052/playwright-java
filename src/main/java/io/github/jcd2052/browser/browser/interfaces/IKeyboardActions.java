package io.github.jcd2052.browser.browser.interfaces;

/**
 * Exposes raw, page-level keyboard controls, sent to whichever element currently has focus.
 * Key names follow Playwright's conventions, e.g. {@code "Enter"}, {@code "ArrowDown"}, {@code "Control+A"}.
 */
public interface IKeyboardActions {
    /**
     * Presses and releases a key or key combination.
     *
     * @param key The key or combination, e.g. {@code "Enter"} or {@code "Shift+Tab"}.
     */
    void press(String key);

    /**
     * Types the text character by character, firing key events for each character.
     *
     * @param text The text to type.
     */
    void type(String text);

    /**
     * Inserts the text in one go, without firing key events, like pasting.
     *
     * @param text The text to insert.
     */
    void insertText(String text);

    /**
     * Holds a key down without releasing it, e.g. {@code "Shift"} for a multi-select.
     *
     * @param key The key to hold down.
     */
    void down(String key);

    /**
     * Releases a key held down with {@link #down(String)}.
     *
     * @param key The key to release.
     */
    void up(String key);
}
