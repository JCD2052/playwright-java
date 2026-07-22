package org.jcd2052.core.exceptions;

/**
 * Thrown when an interaction with an element fails (e.g., element is detached, intercepted by another element).
 */
public class ElementActionException extends BasePlaywrightException {
    public ElementActionException(String message) {
        super(message);
    }
}