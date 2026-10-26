package io.github.jcd2052.logger.i18n;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Keeps the bundled locale files in sync with {@link LogAction}: a missing key makes the element action
 * that logs it throw (not just log badly), and an unused key is dead text nobody maintains.
 */
public class LogActionTranslationsTest {
    /**
     * The locales bundled with the framework.
     */
    @DataProvider
    public Object[][] bundledLocales() {
        return new Object[][]{{"en"}, {"uk"}};
    }

    /**
     * Verifies that every {@link LogAction} has a message in the given bundled locale.
     */
    @Test(dataProvider = "bundledLocales", description = "Every log action has a translation")
    public void everyLogActionHasATranslation(String languageCode) {
        Map<String, String> translations = new LocalizationReader().loadLocale(languageCode, null);

        List<String> missingKeys = Arrays.stream(LogAction.values())
                .map(LogAction::getKey)
                .filter(key -> !translations.containsKey(key))
                .toList();

        Assert.assertTrue(missingKeys.isEmpty(), "Missing '" + languageCode + "' translations: " + missingKeys);
    }

    /**
     * Verifies that every message in the given bundled locale belongs to a {@link LogAction}, so no unused text
     * is left behind.
     */
    @Test(dataProvider = "bundledLocales", description = "Every translation belongs to a log action")
    public void everyTranslationBelongsToALogAction(String languageCode) {
        Set<String> knownKeys = Arrays.stream(LogAction.values()).map(LogAction::getKey).collect(Collectors.toSet());

        List<String> unusedKeys = new LocalizationReader().loadLocale(languageCode, null).keySet().stream()
                .filter(key -> !knownKeys.contains(key))
                .sorted()
                .toList();

        Assert.assertTrue(unusedKeys.isEmpty(), "Unused '" + languageCode + "' translations: " + unusedKeys);
    }
}

