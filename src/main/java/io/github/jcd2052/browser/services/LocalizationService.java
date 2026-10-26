package io.github.jcd2052.browser.services;

import io.github.jcd2052.browser.services.interfaces.ILocalizationService;
import io.github.jcd2052.exceptions.LocalizationException;
import io.github.jcd2052.logger.i18n.ILocalizationReader;
import io.github.jcd2052.logger.i18n.ILogAction;

import java.nio.file.Path;
import java.util.Map;

/**
 * Default {@link ILocalizationService}: loads one language's messages once, when it is created, and formats
 * them with {@link String#format(String, Object...)}.
 */
public class LocalizationService implements ILocalizationService {
    /**
     * The loaded messages, by message key.
     */
    private final Map<String, String> translations;

    /**
     * Loads the messages of the given language.
     *
     * @param localizationReader The reader that loads the language's message file.
     * @param languageCode       The language to load, e.g. {@code "en"} or {@code "uk"}.
     * @param customLocalesDir   A directory with the project's own message files, searched before the
     *                           classpath; {@code null} to use only the classpath.
     * @throws LocalizationException if the language's message file cannot be found or read.
     */
    public LocalizationService(
            ILocalizationReader localizationReader,
            String languageCode,
            Path customLocalesDir) {
        this.translations = localizationReader.loadLocale(languageCode, customLocalesDir);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(ILogAction action, Object... args) {
        String template = translations.get(action.getKey());

        if (template == null) {
            throw new LocalizationException("Missing translation key '" + action.getKey() + "' in current locale.");
        }

        return String.format(template, args);
    }
}