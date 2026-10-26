package io.github.jcd2052.exceptions;

/**
 * Thrown when log messages cannot be loaded (the language file is missing or invalid) or a message is missing
 * for an action.
 */
public class LocalizationException extends BasePlaywrightException {
    /**
     * Creates the exception with a message.
     *
     * @param message What went wrong.
     */
    public LocalizationException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the failure that caused it.
     *
     * @param message What went wrong.
     * @param cause   The underlying failure, e.g. an I/O or JSON parsing error.
     */
    public LocalizationException(String message, Throwable cause) {
        super(message, cause);
    }
}