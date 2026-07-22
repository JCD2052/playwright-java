package org.jcd2052.core.exceptions;

/**
 * Thrown when an element fails to reach an actionable state (visible, enabled) within the timeout.
 */
public class ElementTimeoutException extends BasePlaywrightException {
    public ElementTimeoutException(String message) {
        super(message);
    }
}