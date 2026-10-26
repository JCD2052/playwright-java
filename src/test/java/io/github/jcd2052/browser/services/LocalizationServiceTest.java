package io.github.jcd2052.browser.services;

import io.github.jcd2052.exceptions.LocalizationException;
import io.github.jcd2052.logger.i18n.ILocalizationReader;
import io.github.jcd2052.logger.i18n.ILogAction;
import io.github.jcd2052.logger.i18n.LogAction;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Unit tests for {@link LocalizationService} and for the action names of {@link ILogAction}s.
 */
public class LocalizationServiceTest {

    /**
     * Verifies that a custom action created with {@link ILogAction#of(String)} is formatted from the loaded
     * messages, like the framework's own actions.
     */
    @Test(description = "A custom ILogAction key is formatted from the loaded messages")
    public void aCustomLogActionKeyIsFormattedFromTheLoadedMessages() {
        LocalizationService service = serviceWith(Map.of("element_zoomed", "Zoomed '%1$s' to %2$s%%"));

        String message = service.getMessage(ILogAction.of("element_zoomed"), "Map", 150);

        Assert.assertEquals(message, "Zoomed 'Map' to 150%");
    }

    /**
     * Verifies that asking for an action the loaded language has no message for fails with a
     * {@link LocalizationException} naming the missing key.
     */
    @Test(description = "A missing message key throws a LocalizationException naming the key")
    public void aMissingMessageKeyThrowsALocalizationExceptionNamingTheKey() {
        LocalizationService service = serviceWith(Map.of());

        LocalizationException exception = Assert.expectThrows(LocalizationException.class,
                () -> service.getMessage(ILogAction.of("element_zoomed"), "Map"));

        Assert.assertTrue(exception.getMessage().contains("element_zoomed"));
    }

    /**
     * Verifies that a custom action's readable name, shown in error messages, is its key.
     */
    @Test(description = "A custom ILogAction's action name is its key")
    public void aCustomLogActionsActionNameIsItsKey() {
        Assert.assertEquals(ILogAction.of("element_zoomed").getActionName(), "element_zoomed");
    }

    /**
     * Verifies that every framework action has a short, readable name for error messages, rather than its
     * underscored message key.
     */
    @Test(description = "Every LogAction has a readable action name")
    public void everyLogActionHasAReadableActionName() {
        for (LogAction action : LogAction.values()) {
            Assert.assertFalse(action.getActionName().isBlank(), action + " has no action name");
            Assert.assertFalse(action.getActionName().contains("_"), action + " uses its key as action name");
        }
        Assert.assertEquals(LogAction.ELEMENT_CLICKED.getActionName(), "click");
    }

    /**
     * Creates a service whose reader returns the given messages for any language.
     */
    private static LocalizationService serviceWith(Map<String, String> messages) {
        ILocalizationReader reader = Mockito.mock(ILocalizationReader.class);
        Mockito.when(reader.loadLocale("en", null)).thenReturn(messages);
        return new LocalizationService(reader, "en", null);
    }
}
