package org.jcd2052.core.elements.interfaces;

/**
 * Defines a set of interactions performed via direct JavaScript execution in the browser context.
 * <p>
 * This interface is used to bypass standard driver-level limitations, such as
 * element interceptability or visibility checks, by executing scripts directly
 * on the DOM nodes.
 * </p>
 */
public interface IJsActions {

    /**
     * Performs a click interaction by dispatching a MouseEvent or calling the
     * HTMLElement.click() method directly via JavaScript.
     */
    void click();

    /**
     * Scrolls the element into the visible area of the browser window using
     * the JavaScript {@code element.scrollIntoView()} method.
     */
    void scrollIntoView();

    /**
     * Sets a specific attribute value on the element's DOM node.
     *
     * @param attributeName The name of the attribute to modify (e.g., "style", "class").
     * @param value         The value to assign to the attribute.
     */
    void setAttribute(String attributeName, String value);

    /**
     * Checks if the element has the CSS property {@code pointer-events: none} applied,
     * which would prevent standard mouse interactions.
     *
     * @return {@code true} if pointer events are disabled, {@code false} otherwise.
     */
    boolean isPointerEventsDisabled();

    /**
     * Visually highlights the element in the browser, typically by modifying
     * its CSS outline or background color. This is primarily used for debugging
     * and automated test demonstration.
     */
    void highlight();

    /**
     * Waits for the document or element context to reach a fully loaded or ready state.
     * <p>
     * This typically involves executing JavaScript to evaluate {@code document.readyState === 'complete'},
     * or waiting for any pending asynchronous DOM updates related to the element to finish.
     * </p>
     */
    void waitForReadyState();
}