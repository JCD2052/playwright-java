package org.jcd2052.core.exceptions;

import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jcd2052.core.elements.selector.Selector;

/**
 * Utility class to parse verbose Playwright exceptions into compact, readable framework exceptions.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaywrightExceptionParser {
    /**
     * Parses a raw PlaywrightException and returns a compact Custom Exception.
     *
     * @param e           The raw Playwright exception.
     * @param elementName The logical name of the element.
     * @param selector    The JIT Selector used to find the element.
     * @param actionName  The name of the action being attempted (e.g., "click", "getText").
     * @return A mapped AutomationException.
     */
    public static BasePlaywrightException parse(
            PlaywrightException e,
            String elementName,
            Selector selector,
            String actionName) {

        String compactMessage = extractCompactMessage(e.getMessage());

        String formattedMessage = String.format(
                "Failed to perform '%s' on element '%s'.\nReason: %s\nLocator Strategy: %s",
                actionName,
                elementName,
                compactMessage,
                selector != null ? selector.toString() : "Unknown"
        );

        if (e instanceof TimeoutError || compactMessage.contains("Timeout")) {
            return new ElementTimeoutException(formattedMessage, e);
        }

        if (compactMessage.contains("Target closed") || compactMessage.contains("detached")) {
            return new ElementActionException(
                    formattedMessage + "\n(The element was likely removed from the DOM during the action).", e);
        }

        return new ElementActionException(formattedMessage, e);
    }

    /**
     * Extracts only the primary error reason from a Playwright exception message,
     * stripping out the massive "Call log:" sections.
     *
     * @param fullMessage The raw Playwright error message.
     * @return The first meaningful line of the error.
     */
    private static String extractCompactMessage(String fullMessage) {
        if (fullMessage == null) {
            return "Unknown Playwright error.";
        }

        String[] parts = fullMessage.split("Call log:");
        String primaryError = parts[0].trim();

        if (primaryError.startsWith("Error:")) {
            primaryError = primaryError.replaceFirst("Error:", "").trim();
        }

        return primaryError;
    }
}