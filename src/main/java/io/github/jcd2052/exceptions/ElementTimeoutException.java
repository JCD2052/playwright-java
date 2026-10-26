package io.github.jcd2052.exceptions;

/**
 * Thrown when Playwright times out on an element: an action whose element never became actionable (visible,
 * stable, enabled), or a wait or collection count that wasn't reached in time.
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