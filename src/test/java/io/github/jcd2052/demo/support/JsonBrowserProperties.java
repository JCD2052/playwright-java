package io.github.jcd2052.demo.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jcd2052.browser.configuration.BrowserProperties;
import io.github.jcd2052.browser.configuration.IBrowserProperties;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * Loads browser settings from a JSON file on the classpath, as an example of a configuration source other
 * than Spring properties.
 * <p>
 * The file's keys are the field names of {@link BrowserProperties} (e.g. {@code "name"}, {@code "headless"},
 * {@code "pageLoadTimeout"}, {@code "locale"}); settings left out keep their defaults, and an unknown key
 * fails the load, so a typo doesn't go unnoticed.
 * </p>
 */
public final class JsonBrowserProperties {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonBrowserProperties() {
    }

    /**
     * Reads the settings from a JSON file on the classpath.
     *
     * @param resourcePath The file's path on the classpath, e.g. {@code "browser-properties.json"}.
     * @return The settings read from the file.
     * @throws IllegalArgumentException if there is no such file.
     * @throws UncheckedIOException     if the file can't be read or isn't valid JSON for {@link BrowserProperties}.
     */
    public static IBrowserProperties load(String resourcePath) {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IllegalArgumentException("No browser properties file on the classpath: " + resourcePath);
            }
            return MAPPER.readValue(stream, BrowserProperties.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read browser properties from " + resourcePath, e);
        }
    }
}
