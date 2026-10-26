package io.github.jcd2052.exceptions;

/**
 * Thrown when an element action fails for a reason other than a timeout, e.g. the element was detached or its
 * page was closed during the action, or the selector matched several elements where one was expected.
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