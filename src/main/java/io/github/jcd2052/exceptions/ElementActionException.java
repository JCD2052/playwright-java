package io.github.jcd2052.exceptions;

/**
 * Thrown when an interaction with an element fails (e.g., element is detached, intercepted by another element).
 */
public class ElementActionException extends BasePlaywrightException {
    /**
     * Constructs a new exception with the given message.
     *
     * @param message the detail message
     */
    public ElementActionException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the given message and the original underlying cause.
     *
     * @param message the detail message
     * @param cause   the original Playwright exception that triggered this one
     */
    public ElementActionException(String message, Throwable cause) {
        super(message, cause);
    }
}