package io.github.jcd2052.exceptions;

import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import io.github.jcd2052.elements.selector.Selector;

/**
 * Turns Playwright's verbose exceptions into compact framework exceptions that name the element, the action and
 * the selector.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaywrightExceptionParser {
    /**
     * Translates a Playwright failure into a framework exception.
     * <p>
     * The message has the form "Failed to perform '&lt;action&gt;' on element '&lt;name&gt;'", followed by the
     * reason (Playwright's message without its call log) and the selector. A timeout becomes an
     * {@link ElementTimeoutException}; anything else an {@link ElementActionException}, with a hint when the element
     * was detached or its page closed during the action. The original exception is kept as the cause.
     * </p>
     *
     * @param e           The Playwright exception.
     * @param elementName The element's name.
     * @param selector    The element's selector, or {@code null} if unknown.
     * @param actionName  A short, readable name of the action that failed, e.g. "click".
     * @return The framework exception, ready to throw.
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
     * Keeps only the primary reason of a Playwright error message: the text before its "Call log:" section, without
     * a leading "Error:".
     *
     * @param fullMessage The Playwright error message.
     * @return The primary reason, or a placeholder if there is no message.
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