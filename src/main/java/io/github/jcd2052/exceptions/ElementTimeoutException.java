package io.github.jcd2052.exceptions;

/**
 * Thrown when an element fails to reach an actionable state (visible, enabled) within the timeout.
 */
public class ElementTimeoutException extends BasePlaywrightException {
    /**
     * Constructs a new exception with the given message.
     *
     * @param message the detail message
     */
    public ElementTimeoutException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the given message and the original underlying cause.
     *
     * @param message the detail message
     * @param cause   the original Playwright {@code TimeoutError} that triggered this one
     */
    public ElementTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}