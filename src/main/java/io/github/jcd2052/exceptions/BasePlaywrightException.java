package io.github.jcd2052.exceptions;

/**
 * Base runtime exception for all framework-level automation errors.
 */
public abstract class BasePlaywrightException extends RuntimeException {
    public BasePlaywrightException(String message) {
        super(message);
    }

    public BasePlaywrightException(String message, Throwable cause) {
        super(message, cause);
    }
}
