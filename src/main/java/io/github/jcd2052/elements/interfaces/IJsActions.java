package io.github.jcd2052.elements.interfaces;

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
     * Clicks the element by calling {@code element.click()} in the page, skipping Playwright's actionability
     * checks and the real mouse.
     */
    void click();

    /**
     * Scrolls the element to the center of the viewport with {@code element.scrollIntoView()}.
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
     * Checks whether the element is entirely inside the viewport right now, judging by its bounding box.
     *
     * @return {@code true} if the whole element is in view, {@code false} if any part of it is outside.
     */
    boolean isInViewport();
}