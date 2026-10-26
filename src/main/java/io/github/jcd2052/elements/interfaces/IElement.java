package io.github.jcd2052.elements.interfaces;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.github.jcd2052.browser.browser.interfaces.IMouseActions;
import io.github.jcd2052.browser.services.interfaces.IElementSupplier;
import io.github.jcd2052.elements.ExpectedCount;
import io.github.jcd2052.elements.selector.Selector;

import java.util.List;
import java.util.Optional;

/**
 * The base interface for all of the framework's elements.
 * <p>
 * An element is a name plus a {@link Selector}. The selector is resolved on the current tab every time the
 * element acts, so an element never goes stale. The interface covers reading state (text, attributes,
 * visibility), interactions (clicks, keys, drag and drop, hover, scrolling), waits, and creating child
 * elements scoped to this one.
 * </p>
 * <p>
 * The default methods here act on the Playwright locator directly. {@code AbstractElement}, which every
 * built-in element extends, overrides them to add logging and to translate Playwright errors into framework
 * exceptions.
 * </p>
 */
public interface IElement {
    /**
     * Gets the element's name, used in log messages and errors.
     *
     * @return The element's name.
     */
    String getName();

    /**
     * Gets the element's text content, including text that is hidden with CSS.
     *
     * @return The element's text.
     */
    String getText();

    /**
     * Gets the rendered (inner) text of every element matching this element's selector.
     *
     * @return One text per matching element, in page order.
     */
    List<String> getAllTexts();

    /**
     * Gets the value of one of the element's HTML attributes.
     *
     * @param name The attribute name, e.g. "href" or "class".
     * @return The attribute value, or {@code null} if the element has no such attribute.
     */
    String getAttribute(String name);

    /**
     * Gets the selector this element finds itself with.
     *
     * @return The element's selector.
     */
    Selector getSelector();

    /**
     * Resolves the element's selector on the current tab, for Playwright features this interface doesn't cover.
     *
     * @return The Playwright locator.
     */
    Locator getLocator();

    /**
     * Gets actions that run JavaScript on this element, for cases normal interactions can't reach.
     *
     * @return The element's JavaScript actions.
     */
    IJsActions getJsActions();

    /**
     * Gets the raw mouse of the page this element is on.
     * <p>The mouse works with viewport coordinates, not coordinates relative to this element.</p>
     *
     * @return The page's mouse actions.
     */
    IMouseActions getMouseActions();

    /**
     * Creates an element that is looked up inside this one.
     *
     * @param clazz    The child's element interface or class.
     * @param selector The child's selector, relative to this element.
     * @param name     The child's name.
     * @param <T>      The specific type of the element.
     * @return The instantiated child element.
     */
    <T extends IElement> T createChildElement(Class<T> clazz, Selector selector, String name);

    /**
     * Creates a collection of the elements matching a selector inside this one.
     *
     * @param clazz         The elements' interface or class.
     * @param selector      The elements' selector, relative to this element.
     * @param name          The collection's name; each element is named after it with its position.
     * @param expectedCount The count condition to wait for before the elements are read.
     * @param <T>           The specific type of the elements.
     * @return An {@link IElementCollection} of child elements.
     */
    <T extends IElement> IElementCollection<T> createChildElements(
            Class<T> clazz,
            Selector selector,
            String name,
            ExpectedCount expectedCount);

    /**
     * Creates an element that is looked up inside this one, using a custom supplier to create it.
     *
     * @param elementSupplier Creates the child element.
     * @param selector        The child's selector, relative to this element.
     * @param name            The child's name.
     * @param <T>             The specific type of the element.
     * @return The instantiated child element.
     */
    <T extends IElement> T createChildElement(IElementSupplier<T> elementSupplier, Selector selector, String name);

    /**
     * Checks whether the element is visible on the page right now, without waiting.
     *
     * @return {@code true} if visible, {@code false} otherwise.
     */
    boolean isVisible();

    /**
     * Checks whether the element is enabled (not {@code disabled}) right now, without waiting.
     *
     * @return {@code true} if enabled, {@code false} otherwise.
     */
    boolean isEnabled();

    /**
     * Focuses the element and presses a key or key combination on it.
     *
     * @param key The key identifier or key combination (e.g., "Enter", "Control+A").
     */
    void press(String key);

    /**
     * Clicks the element, after Playwright's actionability checks (visible, stable, enabled, receives events).
     */
    void click();

    /**
     * Performs a double-click on the element.
     */
    void doubleClick();

    /**
     * Right-clicks the element.
     */
    void rightClick();

    /**
     * Clicks the element with the middle mouse button.
     */
    void middleClick();

    /**
     * Clicks the element at an offset from the top-left corner of its box.
     *
     * @param x The horizontal offset in pixels.
     * @param y The vertical offset in pixels.
     */
    void click(double x, double y);

    /**
     * Clicks the element without waiting for Playwright's actionability checks, e.g. for an element that is
     * always moving.
     */
    void forceClick();

    /**
     * Drags this element onto the target by moving the mouse in steps, instead of Playwright's native
     * {@link #dragTo(IElement)}. Useful for drag-and-drop libraries that react to incremental {@code mousemove}
     * events. Both elements are scrolled into view before the mouse moves.
     *
     * @param targetElement The element to drop onto.
     * @param steps         The number of intermediate mouse-move steps between source and target (e.g. 10-20).
     */
    void dragAndDropTo(IElement targetElement, int steps);

    /**
     * Removes focus from the element.
     */
    default void unfocus() {
        getLocator().blur();
    }

    /**
     * Clicks the element through JavaScript ({@code element.click()}), which reaches elements a real click can't,
     * such as ones with {@code pointer-events: none}.
     */
    default void clickWithJs() {
        getJsActions().click();
    }

    /**
     * Scrolls the element into view, if it isn't already.
     */
    default void scrollToElement() {
        getLocator().scrollIntoViewIfNeeded();
    }

    /**
     * Drags this element onto the target with Playwright's native drag and drop.
     *
     * @param targetElement The element to drop onto.
     */
    default void dragTo(IElement targetElement) {
        getLocator().dragTo(targetElement.getLocator());
    }

    /**
     * Hovers the mouse pointer over the element.
     */
    default void hover() {
        getLocator().hover();
    }

    /**
     * Takes a screenshot of the element.
     *
     * @return The PNG image.
     */
    default byte[] getScreenshot() {
        return getLocator().screenshot();
    }

    /**
     * Waits for the element to reach a state, failing if it doesn't in time.
     *
     * @param state   The state to wait for, e.g. VISIBLE, HIDDEN, ATTACHED or DETACHED; {@code null} for VISIBLE.
     * @param timeout The maximum time to wait in milliseconds; {@code null} for the window's default timeout.
     */
    default void waitForState(WaitForSelectorState state, Double timeout) {
        Locator.WaitForOptions waitOptions = state == null && timeout == null ? null : new Locator.WaitForOptions();
        if (waitOptions != null) {
            waitOptions.setState(state);
            Optional.ofNullable(timeout).ifPresent(waitOptions::setTimeout);
        }
        getLocator().waitFor(waitOptions);
    }

    /**
     * Waits for the element to reach a state, returning {@code false} instead of throwing if it doesn't in time.
     *
     * @param state   The state to wait for; {@code null} for VISIBLE.
     * @param timeout The maximum time to wait in milliseconds; {@code null} for the window's default timeout.
     * @return {@code true} if the state was reached, {@code false} if a {@link TimeoutError} occurred.
     */
    default boolean waitForLoading(WaitForSelectorState state, Double timeout) {
        try {
            waitForState(state, timeout);
            return true;
        } catch (TimeoutError e) {
            return false;
        }
    }

    /**
     * Waits, up to the window's default timeout, for the element to become visible.
     *
     * @return {@code true} if the state was reached, {@code false} if a timeout occurred.
     */
    default boolean waitForLoading() {
        return waitForLoading(null, null);
    }

    /**
     * Waits for the element to be removed from the DOM.
     *
     * @param timeout The maximum time to wait in milliseconds.
     * @return {@code true} if the element was detached, {@code false} if a timeout occurred.
     */
    default boolean waitToBeDetached(Double timeout) {
        return waitForLoading(WaitForSelectorState.DETACHED, timeout);
    }

    /**
     * Waits, up to the window's default timeout, for the element to be removed from the DOM.
     *
     * @return {@code true} if the element was detached, {@code false} if a timeout occurred.
     */
    default boolean waitToBeDetached() {
        return waitToBeDetached(null);
    }

    /**
     * Waits for the element to become visible.
     *
     * @param timeout The maximum time to wait in milliseconds.
     * @return {@code true} if the element became visible, {@code false} if a timeout occurred.
     */
    default boolean waitToBeVisible(Double timeout) {
        return waitForLoading(WaitForSelectorState.VISIBLE, timeout);
    }

    /**
     * Waits, up to the window's default timeout, for the element to become visible.
     *
     * @return {@code true} if the element became visible, {@code false} if a timeout occurred.
     */
    default boolean waitToBeVisible() {
        return waitToBeVisible(null);
    }

    /**
     * Creates an element that is looked up inside this one, named {@code "Child of <this element's name>"}.
     *
     * @param clazz    The child's element interface or class.
     * @param selector The child's selector, relative to this element.
     * @param <T>      The specific type of the element.
     * @return The instantiated child element.
     */
    default <T extends IElement> T createChildElement(Class<T> clazz, Selector selector) {
        return createChildElement(clazz, selector, "Child of " + getName());
    }

    /**
     * Creates an element that is looked up inside this one using a custom supplier, named
     * {@code "Child of <this element's name>"}.
     *
     * @param elementSupplier Creates the child element.
     * @param selector        The child's selector, relative to this element.
     * @param <T>             The specific type of the element.
     * @return The instantiated child element.
     */
    default <T extends IElement> T createChildElement(
            IElementSupplier<T> elementSupplier,
            Selector selector) {
        return createChildElement(elementSupplier, selector, "Child of " + getName());
    }

    /**
     * Creates a collection of the elements matching a selector inside this one, without waiting for any count
     * ({@link ExpectedCount#ANY}).
     *
     * @param clazz    The elements' interface or class.
     * @param selector The elements' selector, relative to this element.
     * @param name     The collection's name; each element is named after it with its position.
     * @param <T>      The specific type of the elements.
     * @return An {@link IElementCollection} of child elements.
     */
    default <T extends IElement> IElementCollection<T> createChildElements(
            Class<T> clazz,
            Selector selector,
            String name) {
        return createChildElements(clazz, selector, name, ExpectedCount.ANY);
    }
}