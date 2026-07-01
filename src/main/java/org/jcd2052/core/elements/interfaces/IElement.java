package org.jcd2052.core.elements.interfaces;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.jcd2052.core.browser.services.interfaces.IElementSupplier;
import org.jcd2052.core.elements.ExpectedCount;

import java.util.List;
import java.util.Optional;

/**
 * The base interface for all UI elements in the framework.
 * <p>
 * Defines the core contract for metadata (name, selector), state checks (visibility, enablement),
 * and interactions (clicking, typing). It also enables hierarchical element discovery
 * by providing methods to create child elements relative to the current one.
 * </p>
 */
public interface IElement {
    /**
     * @return The logical name of the element used for logging and reporting.
     */
    String getName();

    /**
     * @return The visible inner text of the element.
     */
    String getText();

    /**
     * @return A collection of strings representing the text of all elements matching the selector.
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
     * @return The raw Playwright selector string associated with this element.
     */
    String getSelector();

    Locator getLocator();

    IJsActions getJsActions();

    /**
     * Creates a single child element relative to this element's selector.
     *
     * @param clazz    The class or interface type of the child element.
     * @param selector The relative selector string.
     * @param name     The logical name for the child element.
     * @return The instantiated child element.
     */
    <T extends IElement> T createChildElement(Class<T> clazz, String selector, String name);

    /**
     * Creates a collection of child elements relative to this element's selector.
     *
     * @param clazz         The type of elements in the collection.
     * @param selector      The relative selector string.
     * @param name          The logical name for the collection.
     * @param expectedCount The count condition to wait for.
     * @return An {@link IElementCollection} of child elements.
     */
    <T extends IElement> IElementCollection<T> createChildElements(
            Class<T> clazz,
            String selector,
            String name,
            ExpectedCount expectedCount);

    /**
     * Creates a single child element using a custom supplier.
     *
     * @param elementSupplier The supplier defining the instantiation logic.
     * @param selector        The relative selector string.
     * @param name            The logical name for the child element.
     * @return The instantiated child element.
     */
    <T extends IElement> T createChildElement(IElementSupplier<T> elementSupplier, String selector, String name);

    /**
     * @return {@code true} if the element is currently visible in the DOM.
     */
    boolean isVisible();

    /**
     * @return {@code true} if the element is not disabled.
     */
    boolean isEnabled();

    /**
     * Simulates pressing a specific keyboard key (e.g., "Enter", "Control+A").
     *
     * @param key The key or key combination to press.
     */
    void press(String key);

    /**
     * Performs a standard Playwright click on the element.
     */
    void click();

    /** Performs a right-click (context menu) on the element. */
    void rightClick();

    /** Performs a middle-click (auxiliary click) on the element. */
    void middleClick();

    /**
     * Clicks at specific coordinates relative to the top-left corner of the element's padding box.
     * * @param x The X coordinate relative to the element.
     * @param y The Y coordinate relative to the element.
     */
    void click(double x, double y);

    /**
     * Performs a forced click, bypassing actionability checks if necessary.
     */
    void forceClick();

    default void unfocus() {
        getLocator().blur();
    }

    /**
     * Performs a click interaction using JavaScript execution in the browser context.
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

    default void dragTo(IElement targetElement) {
        getLocator().dragTo(targetElement.getLocator());
    }

    default void hover() {
        getLocator().hover();
    }

    /**
     * @return A byte array representing the screenshot of the element.
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
     */
    default boolean waitForLoading() {
        return waitForLoading(null, null);
    }

    /**
     * Waits for the element to be removed from the DOM.
     */
    default boolean waitToBeDetached(Double timeout) {
        return waitForLoading(WaitForSelectorState.DETACHED, timeout);
    }

    /**
     * Waits for the element to be removed from the DOM using default timeout.
     */
    default boolean waitToBeDetached() {
        return waitToBeDetached(null);
    }

    /**
     * Waits for the element to become visible.
     */
    default boolean waitToBeVisible(Double timeout) {
        return waitForLoading(WaitForSelectorState.VISIBLE, timeout);
    }

    /**
     * Waits for the element to become visible using default timeout.
     */
    default boolean waitToBeVisible() {
        return waitToBeVisible(null);
    }

    /**
     * Convenience method for creating a child element with a default logical name.
     */
    default <T extends IElement> T createChildElement(Class<T> clazz, String selector) {
        return createChildElement(clazz, selector, "Child of " + getName());
    }

    /**
     * Convenience method for creating a child element via supplier with a default logical name.
     */
    default <T extends IElement> T createChildElement(
            IElementSupplier<T> elementSupplier,
            String selector) {
        return createChildElement(elementSupplier, selector, "Child of " + getName());
    }

    /**
     * Convenience method for creating a child collection with an 'ANY' expected count.
     */
    default <T extends IElement> IElementCollection<T> createChildElements(
            Class<T> clazz,
            String selector,
            String name) {
        return createChildElements(clazz, selector, name, ExpectedCount.ANY);
    }
}