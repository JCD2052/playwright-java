package io.github.jcd2052.exceptions;

public class LocalizationException extends BasePlaywrightException {
    public LocalizationException(String message) {
        super(message);
    }

    public LocalizationException(String message, Throwable cause) {
        super(message, cause);
    }
}