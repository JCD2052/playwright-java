package org.jcd2052.core.elements;

/**
 * Defines the expected state or quantity of elements when resolving an {@link ElementCollection}.
 * <p>This enum dictates the synchronization and waiting strategy applied before a collection
 * of web elements is interacted with or returned. It ensures the DOM is in the correct state
 * for the intended test assertion or action.</p>
 */
public enum ExpectedCount {
    /**
     * Expects exactly zero matching elements in the DOM.
     * <p>When applied, the framework will explicitly assert that the locator count is 0,
     * making it ideal for verifying the absence or disappearance of elements.</p>
     */
    ZERO,
    /**
     * Expects at least one matching element to be present in the DOM.
     * <p>When applied, the framework will wait for the first matching element to reach
     * the {@code ATTACHED} state before proceeding. This is the standard expectation
     * when you intend to interact with elements in the collection.</p>
     */
    MORE_THAN_ZERO,
    /**
     * Bypasses explicit wait conditions or assertions regarding the element count.
     * <p>When applied, the framework will immediately evaluate the collection,
     * regardless of whether 0, 1, or multiple elements currently match the locator.</p>
     */
    ANY
}