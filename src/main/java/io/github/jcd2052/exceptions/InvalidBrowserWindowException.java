package io.github.jcd2052.exceptions;

/**
 * Thrown when a browser window is used with a browser it does not belong to, or after it has been closed.
 */
public class InvalidBrowserWindowException extends BasePlaywrightException {
    /**
     * Creates the exception with a message.
     *
     * @param message What went wrong.
     */
    public InvalidBrowserWindowException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the failure that caused it.
     *
     * @param message What went wrong.
     * @param cause   The underlying failure.
     */
    public InvalidBrowserWindowException(String message, Throwable cause) {
        super(message, cause);
    }
}
