package io.github.jcd2052.logger.i18n;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.testng.Assert.assertTrue;

/**
 * Keeps the bundled locale files in sync with {@link LogAction}: a missing key makes the element action
 * that logs it throw (not just log badly), and an unused key is dead text nobody maintains.
 */
public class LogActionTranslationsTest {

    @DataProvider
    public Object[][] bundledLocales() {
        return new Object[][]{{"en"}, {"uk"}};
    }

    @Test(dataProvider = "bundledLocales")
    public void everyLogActionHasATranslation(String languageCode) {
        Map<String, String> translations = new LocalizationReader().loadLocale(languageCode, null);

        List<String> missingKeys = Arrays.stream(LogAction.values())
                .map(LogAction::getKey)
                .filter(key -> !translations.containsKey(key))
                .collect(Collectors.toList());

        assertTrue(missingKeys.isEmpty(), "Missing '" + languageCode + "' translations: " + missingKeys);
    }

    @Test(dataProvider = "bundledLocales")
    public void everyTranslationBelongsToALogAction(String languageCode) {
        Set<String> knownKeys = Arrays.stream(LogAction.values()).map(LogAction::getKey).collect(Collectors.toSet());

        List<String> unusedKeys = new LocalizationReader().loadLocale(languageCode, null).keySet().stream()
                .filter(key -> !knownKeys.contains(key))
                .sorted()
                .collect(Collectors.toList());

        assertTrue(unusedKeys.isEmpty(), "Unused '" + languageCode + "' translations: " + unusedKeys);
    }
}

