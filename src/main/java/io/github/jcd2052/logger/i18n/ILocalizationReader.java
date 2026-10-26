package io.github.jcd2052.logger.i18n;

import java.nio.file.Path;
import java.util.Map;

/**
 * Loads one language's log messages: a JSON object mapping message keys to message templates.
 */
public interface ILocalizationReader {
    /**
     * Loads the messages of the given language.
     *
     * @param languageCode    The language to load, e.g. {@code "en"}; the file is {@code <languageCode>.json}.
     * @param customDirectory A directory to look in before the classpath, or {@code null}.
     * @return The messages, by message key.
     * @throws io.github.jcd2052.exceptions.LocalizationException if the file cannot be found, read or parsed.
     */
    Map<String, String> loadLocale(String languageCode, Path customDirectory);
}
