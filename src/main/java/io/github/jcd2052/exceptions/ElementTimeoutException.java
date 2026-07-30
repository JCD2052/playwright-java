package io.github.jcd2052.exceptions;

/**
 * Thrown when an element fails to reach an actionable state (visible, enabled) within the timeout.
 */
public class ElementTimeoutException extends BasePlaywrightException {
    public ElementTimeoutException(String message) {
        super(message);
    }

    public ElementTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}