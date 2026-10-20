package io.github.jcd2052.logger.i18n;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jcd2052.exceptions.LocalizationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class LocalizationReader implements ILocalizationReader {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String DEFAULT_LOCALIZATION_DIR = "localization/";

    @Override
    public Map<String, String> loadLocale(String languageCode, Path customDirectory) {
        String fileName = languageCode.toLowerCase() + ".json";
        InputStream stream = null;

        if (customDirectory != null) {
            Path customFilePath = customDirectory.resolve(fileName);
            if (Files.exists(customFilePath)) {
                try {
                    stream = Files.newInputStream(customFilePath);
                } catch (IOException e) {
                    throw new LocalizationException("Failed to read client locale file at: " + customFilePath, e);
                }
            }
        }

        if (stream == null) {
            String resourcePath = DEFAULT_LOCALIZATION_DIR + fileName;
            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        }

        if (stream == null) {
            String internalPath = DEFAULT_LOCALIZATION_DIR + fileName;
            stream = LocalizationReader.class.getClassLoader().getResourceAsStream(internalPath);
        }

        if (stream == null) {
            throw new LocalizationException(
                    String.format(
                            "Localization file '%s' was not found in client resources, custom path, or framework defaults.",
                            fileName)
            );
        }

        try (InputStream is = stream) {
            return MAPPER.readValue(is, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new LocalizationException("Failed to parse localization JSON for language: " + languageCode, e);
        }
    }
}