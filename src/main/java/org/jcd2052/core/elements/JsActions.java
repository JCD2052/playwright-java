package org.jcd2052.core.elements;

import com.microsoft.playwright.Locator;
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
        getLocator().evaluate("node => node.click()");
    }

    /**
     * Smoothly scrolls the element into the center of the browser's viewport.
     * <p>Utilizes the JavaScript {@code scrollIntoView} API with smooth behavior
     * and center alignment.</p>
     */
    public void scrollIntoView() {
        getLocator().evaluate("el => el.scrollIntoView({ block: 'center', inline: 'center' })");
    }

    /**
     * Sets or modifies an HTML attribute directly on the DOM node.
     *
     * @param attributeName The name of the attribute to set (e.g., "value", "class").
     * @param value         The string value to assign to the attribute.
     */
    public void setAttribute(String attributeName, String value) {
        getLocator().evaluate("(node, data) => node.setAttribute(data.attr, data.val)",
                Map.of("attr", attributeName, "val", value));
    }

    /**
     * Visually highlights the element on the page by applying a solid red CSS border.
     * <p>This is primarily used for debugging and visual tracking during test execution.</p>
     */
    public void highlight() {
        getLocator().evaluate("node => node.style.border = '3px solid red'");
    }

    /**
     * Waits for the underlying web page's document to reach a fully loaded state.
     * <p>This method evaluates the {@code document.readyState === 'complete'}
     * JavaScript expression on the current page context, pausing execution
     * until the condition is met.</p>
     */
    @Override
    public void waitForReadyState() {
        getLocator().page().waitForFunction("() => document.readyState === 'complete'");
    }

    /**
     * Checks if the element is unclickable due to its CSS pointer-events style.
     *
     * @return {@code true} if the computed CSS style for {@code pointer-events}
     * is strictly equal to {@code 'none'}; {@code false} otherwise.
     */
    public boolean isPointerEventsDisabled() {
        return (boolean) getLocator().evaluate("node => window.getComputedStyle(node).pointerEvents === 'none'");
    }

    /**
     * Retrieves the underlying Playwright {@link Locator} from the bound element.
     * <p>Since {@code IElement} is an interface, this method enforces that the
     * provided element implementation is an {@link AbstractElement} to access
     * the protected locator method.</p>
     *
     * @return The Playwright {@code Locator}.
     * @throws IllegalArgumentException if the bound element is not an instance of {@code AbstractElement}.
     */
    private Locator getLocator() {
        if (element instanceof AbstractElement) {
            return ((AbstractElement) element).getLocator();
        } else {
            throw new IllegalArgumentException("IElement must be an instance of AbstractElement to use JsActions");
        }
    }
}