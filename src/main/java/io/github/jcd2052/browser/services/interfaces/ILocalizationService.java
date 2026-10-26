package io.github.jcd2052.browser.services.interfaces;

import io.github.jcd2052.logger.i18n.ILogAction;
import io.github.jcd2052.logger.i18n.LogAction;

/**
 * Turns a logged action into a human-readable message in the configured language. Works for the framework's
 * {@link LogAction}s and for custom actions created with {@link ILogAction#of(String)}.
 */
public interface ILocalizationService {
    /**
     * Builds the message for an action from its localized template.
     *
     * @param action The action to describe; its key must have a message in the loaded language.
     * @param args   The template arguments; for element actions, the first is the element's name.
     * @return The formatted message.
     * @throws io.github.jcd2052.exceptions.LocalizationException if the language has no message for the action.
     */
    String getMessage(ILogAction action, Object... args);
}
