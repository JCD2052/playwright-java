package io.github.jcd2052.exceptions;

/**
 * Base class of the framework's unchecked exceptions: {@link ElementTimeoutException} and
 * {@link ElementActionException} for failed element actions, and {@link LocalizationException} for log messages.
 * Catch it to handle any of them.
 */
public abstract class BasePlaywrightException extends RuntimeException {
    /**
     * Constructs a new exception with the given message.
     *
     * @param message the detail message
     */
    public BasePlaywrightException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the given message and the original underlying cause.
     *
     * @param message the detail message
     * @param cause   the original exception that triggered this one
     */
    public BasePlaywrightException(String message, Throwable cause) {
        super(message, cause);
    }
}
