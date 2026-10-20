package io.github.jcd2052.browser.services;

import io.github.jcd2052.browser.services.interfaces.ILocalizationService;
import io.github.jcd2052.exceptions.LocalizationException;
import io.github.jcd2052.logger.i18n.ILocalizationReader;
import io.github.jcd2052.logger.i18n.LogAction;

import java.nio.file.Path;
import java.util.Map;

public class LocalizationService implements ILocalizationService {
    private final Map<String, String> translations;

    public LocalizationService(
            ILocalizationReader localizationReader,
            String languageCode,
            Path customLocalesDir) {
        this.translations = localizationReader.loadLocale(languageCode, customLocalesDir);
    }

    /**
     * Fetches the localized template and formats it with the provided arguments.
     */
    @Override
    public String getMessage(LogAction action, Object... args) {
        String template = translations.get(action.getKey());

        if (template == null) {
            throw new LocalizationException("Missing translation key '" + action.getKey() + "' in current locale.");
        }

        return String.format(template, args);
    }
}