package io.github.jcd2052.exceptions;

/**
 * Base runtime exception for all framework-level automation errors.
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
