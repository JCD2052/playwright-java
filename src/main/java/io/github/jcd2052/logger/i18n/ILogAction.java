package io.github.jcd2052.logger.i18n;

/**
 * A logged action, identified by its message key in the localization files. The framework's own actions are
 * the {@link LogAction} constants; custom elements can log their own with {@link #of(String)}.
 */
public interface ILogAction {
    /**
     * Gets the action's message key in the localization files.
     *
     * @return The message key, e.g. {@code "element_clicked"}.
     */
    String getKey();

    /**
     * Gets a short, readable name of the action, shown in error messages, e.g. "Failed to perform 'click' on
     * element 'Save button'".
     *
     * @return The action name; by default the message key.
     */
    default String getActionName() {
        return getKey();
    }

    /**
     * Creates an action for an arbitrary message key, without adding a {@link LogAction} constant, e.g. for a
     * custom element's own action. The loaded locale file must have a message for the key; its action name in
     * error messages is the key.
     *
     * @param key The message key.
     * @return An action with that key.
     */
    static ILogAction of(String key) {
        return () -> key;
    }
}
