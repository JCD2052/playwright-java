package org.jcd2052.core.elements;

import org.jcd2052.core.elements.interfaces.IElement;
import org.jcd2052.core.elements.interfaces.IJsActions;

import java.util.Map;

/**
 * Provides JavaScript-based interactions and evaluations for web elements.
 * <p>This class implements {@link IJsActions} to execute raw JavaScript directly
 * on the underlying DOM node of an {@link IElement}. These methods are typically
 * used as fallbacks when native Playwright interactions are intercepted or fail
 * due to strict actionability checks (e.g., elements visually obscured by overlaps).</p>
 */
public class JsActions implements IJsActions {
    private final IElement element;

    /**
     * Constructs a new {@code JsActions} instance bound to a specific element.
     *
     * @param element The framework element to perform JavaScript actions upon.
     *                Must be an instance of {@link AbstractElement}.
     */
    public JsActions(IElement element) {
        this.element = element;
    }

    /**
     * Clicks the element using the native JavaScript {@code click()} method.
     * <p>This bypasses Playwright's built-in actionability checks, such as verifying
     * if the element is visible, stable, or unobscured by other elements.</p>
     */
    public void click() {
        element.getLocator().evaluate("node => node.click()");
    }

    /**
     * Smoothly scrolls the element into the center of the browser's viewport.
     * <p>Utilizes the JavaScript {@code scrollIntoView} API with smooth behavior
     * and center alignment.</p>
     */
    public void scrollIntoView() {
        element.getLocator().evaluate("el => el.scrollIntoView({ block: 'center', inline: 'center' })");
    }

    /**
     * Sets or modifies an HTML attribute directly on the DOM node.
     *
     * @param attributeName The name of the attribute to set (e.g., "value", "class").
     * @param value         The string value to assign to the attribute.
     */
    public void setAttribute(String attributeName, String value) {
        element.getLocator().evaluate("(node, data) => node.setAttribute(data.attr, data.val)",
                Map.of("attr", attributeName, "val", value));
    }

    /**
     * Checks if the element is unclickable due to its CSS pointer-events style.
     *
     * @return {@code true} if the computed CSS style for {@code pointer-events}
     * is strictly equal to {@code 'none'}; {@code false} otherwise.
     */
    public boolean isPointerEventsDisabled() {
        return (boolean) element.getLocator()
                .evaluate("node => window.getComputedStyle(node).pointerEvents === 'none'");
    }
}