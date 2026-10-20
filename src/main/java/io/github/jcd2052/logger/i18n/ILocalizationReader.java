package io.github.jcd2052.logger.i18n;

import java.nio.file.Path;
import java.util.Map;

public interface ILocalizationReader {
    Map<String, String> loadLocale(String languageCode, Path customDirectory);
}
