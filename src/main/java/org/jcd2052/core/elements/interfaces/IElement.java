package org.jcd2052.core.elements.interfaces;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.jcd2052.core.browser.browser.interfaces.IMouseActions;
import org.jcd2052.core.browser.services.interfaces.IElementSupplier;
import org.jcd2052.core.elements.ExpectedCount;
import org.jcd2052.core.elements.selector.Selector;

import java.util.List;
import java.util.Optional;

/**
 * The base interface for all UI elements in the framework.
 * <p>
 * Defines the core contract for metadata (name, Selector locator strategy), state checks (visibility, enablement),
 * and interactions (clicking, typing). It also enables hierarchical element discovery
 * by providing methods to create child elements relative to the current one.
 * </p>
 */
public interface IElement {
    /**
     * Retrieves the logical name of the element used for logging and reporting.
     *
     * @return The logical name of the element.
     */
    String getName();

    /**
     * Retrieves the visible inner text of the element.
     *
     * @return The text content of the element.
     */
    String getText();

    /**
     * Retrieves a collection of strings representing the text of all elements matching the current strategy.
     *
     * @return A list of text strings.
     */
    List<String> getAllTexts();

    /**
     * Retrieves the value of the specified attribute.
     *
     * @param name The name of the attribute (e.g., "href", "value", "class").
     * @return The attribute value as a string.
     */
    String getAttribute(String name);

    /**
     * Retrieves the Just-In-Time locator strategy assigned to this element.
     *
     * @return The {@link Selector} locator strategy wrapper.
     */
    Selector getSelector();

    /**
     * Dynamically resolves and evaluates the underlying Playwright locator.
     *
     * @return The live Playwright {@link Locator} instance.
     */
    Locator getLocator();

    /**
     * Retrieves the JavaScript actions context bounded to this element.
     *
     * @return The JavaScript actions execution interface.
     */
    IJsActions getJsActions();

    /**
     * Retrieves the raw, page-level mouse API from the context of this element.
     * <p><b>WARNING:</b> The IMouseActions API uses absolute screen coordinates (X/Y),
     * NOT coordinates relative to this specific element!</p>
     *
     * @return The IMouseActions instance for complex mouse movements.
     */
    IMouseActions getMouseActions();

    /**
     * Creates a single child element relative to this element's locator strategy.
     *
     * @param clazz    The class or interface type of the child element.
     * @param selector The relative child Selector locator strategy.
     * @param name     The logical name for the child element.
     * @param <T>      The specific type of the element.
     * @return The instantiated child element.
     */
    <T extends IElement> T createChildElement(Class<T> clazz, Selector selector, String name);

    /**
     * Creates a collection of child elements relative to this element's locator strategy.
     *
     * @param clazz         The type of elements in the collection.
     * @param selector      The relative child Selector locator strategy.
     * @param name          The logical name for the collection.
     * @param expectedCount The count condition to wait for.
     * @param <T>           The specific type of the elements.
     * @return An {@link IElementCollection} of child elements.
     */
    <T extends IElement> IElementCollection<T> createChildElements(
            Class<T> clazz,
            Selector selector,
            String name,
            ExpectedCount expectedCount);

    /**
     * Creates a single child element using a custom supplier and a deferred Selector strategy.
     *
     * @param elementSupplier The supplier defining the instantiation logic.
     * @param selector        The relative child Selector locator strategy.
     * @param name            The logical name for the child element.
     * @param <T>             The specific type of the element.
     * @return The instantiated child element.
     */
    <T extends IElement> T createChildElement(IElementSupplier<T> elementSupplier, Selector selector, String name);

    /**
     * Checks if the element is currently visible in the DOM.
     *
     * @return {@code true} if visible, {@code false} otherwise.
     */
    boolean isVisible();

    /**
     * Checks if the element is currently enabled and interactive.
     *
     * @return {@code true} if enabled, {@code false} otherwise.
     */
    boolean isEnabled();

    /**
     * Simulates pressing a specific keyboard key or keyboard shortcut combination.
     *
     * @param key The key identifier or key combination (e.g., "Enter", "Control+A").
     */
    void press(String key);

    /**
     * Performs a standard Playwright click on the element.
     */
    void click();

    /**
     * Performs a double-click on the element.
     */
    void doubleClick();

    /**
     * Performs a right-click context interaction on the element.
     */
    void rightClick();

    /**
     * Performs a middle-click auxiliary interaction on the element.
     */
    void middleClick();

    /**
     * Clicks at specific layout coordinates relative to the top-left edge of the element bounding box.
     *
     * @param x The horizontal coordinate offset.
     * @param y The vertical coordinate offset.
     */
    void click(double x, double y);

    /**
     * Performs a forced action click that bypasses standard Playwright actionability checks.
     */
    void forceClick();

    /**
     * Performs a drag-and-drop operation using simulated, stepped mouse movements instead of Playwright's
     * native {@code dragTo()}. This is more reliable for modern drag-and-drop libraries (e.g. React-Beautiful-DnD
     * or SortableJS) that rely on precise, incremental {@code mousemove} events.
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
     * Performs a click interaction using raw JavaScript execution in the client browser context.
     */
    default void clickWithJs() {
        getJsActions().click();
    }

    /**
     * Scrolls the element into the browser's viewport.
     */
    default void scrollToElement() {
        getLocator().scrollIntoViewIfNeeded();
    }

    /**
     * Performs a native Playwright drag-and-drop action onto the target element.
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
     * Captures a screenshot of the element.
     *
     * @return A byte array representing the screenshot image.
     */
    default byte[] getScreenshot() {
        return getLocator().screenshot();
    }

    /**
     * Waits for the element to reach a specific state (e.g., VISIBLE, HIDDEN, ATTACHED).
     *
     * @param state   The desired {@link WaitForSelectorState}.
     * @param timeout The maximum time to wait in milliseconds.
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
     * Safe wait method that returns a boolean instead of throwing an exception on timeout.
     *
     * @param state   The desired state.
     * @param timeout The timeout in milliseconds.
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
     * Waits for the default state and timeout.
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
     * Waits for the element to be removed from the DOM using the default timeout.
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
     * Waits for the element to become visible using the default timeout.
     *
     * @return {@code true} if the element became visible, {@code false} if a timeout occurred.
     */
    default boolean waitToBeVisible() {
        return waitToBeVisible(null);
    }

    /**
     * Convenience method for creating a child element with a default logical name.
     *
     * @param clazz    The class or interface type of the child element.
     * @param selector The relative child Selector locator strategy.
     * @param <T>      The specific type of the element.
     * @return The instantiated child element.
     */
    default <T extends IElement> T createChildElement(Class<T> clazz, Selector selector) {
        return createChildElement(clazz, selector, "Child of " + getName());
    }

    /**
     * Convenience method for creating a child element via supplier with a default logical name.
     *
     * @param elementSupplier The supplier defining the instantiation logic.
     * @param selector        The relative child Selector locator strategy.
     * @param <T>             The specific type of the element.
     * @return The instantiated child element.
     */
    default <T extends IElement> T createChildElement(
            IElementSupplier<T> elementSupplier,
            Selector selector) {
        return createChildElement(elementSupplier, selector, "Child of " + getName());
    }

    /**
     * Convenience method for creating a child collection with an 'ANY' expected count.
     *
     * @param clazz    The type of elements in the collection.
     * @param selector The relative child Selector locator strategy.
     * @param name     The logical name for the collection.
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