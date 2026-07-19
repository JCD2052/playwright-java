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
     * Performs a standard Just-In-Time evaluation and click on the element.
     */
    void click();

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
     * Performs a stepped drag-and-drop workflow simulation to interact with complex dynamic scripts.
     *
     * @param targetElement The target landing element drop destination.
     * @param steps         The mouse moving precision granularity layout updates framework.
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
     * Scrolls the current document context container viewports until this element becomes visible.
     */
    default void scrollToElement() {
        getLocator().scrollIntoViewIfNeeded();
    }

    /**
     * Performs a native drag and drop action to a targeted drop destination element layout node.
     *
     * @param targetElement The target element block layer.
     */
    default void dragTo(IElement targetElement) {
        getLocator().dragTo(targetElement.getLocator());
    }

    /**
     * Hovers the mouse pointer tracking scope over the element.
     */
    default void hover() {
        getLocator().hover();
    }

    /**
     * Captures a precise runtime image screenshot buffer block of this localized element space.
     *
     * @return A binary byte array capturing the layout graphic view.
     */
    default byte[] getScreenshot() {
        return getLocator().screenshot();
    }

    /**
     * Dynamic synchronized waiter evaluating active element states against specific rules criteria.
     *
     * @param state   The element lifecycle layout target state requirement.
     * @param timeout The maximum allotted timeout threshold tracking milliseconds scope.
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
     * A safe wait polling wrapper that converts hard TimeoutErrors into soft boolean returns.
     *
     * @param state   The layout lifecycle node sync state requirement.
     * @param timeout The timeout threshold in milliseconds.
     * @return {@code true} if reached target layout conditions, {@code false} on error timeout boundaries.
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
     * Wait for standard state constraints with standard duration timeouts configurations.
     *
     * @return {@code true} if element state resolved, {@code false} otherwise.
     */
    default boolean waitForLoading() {
        return waitForLoading(null, null);
    }

    /**
     * Soft failure safe wait evaluating until element handles drop from active page visibility lifecycles.
     *
     * @param timeout Timeout tracking duration in milliseconds.
     * @return {@code true} if detached safely, {@code false} on timeout intervals.
     */
    default boolean waitToBeDetached(Double timeout) {
        return waitForLoading(WaitForSelectorState.DETACHED, timeout);
    }

    /**
     * Soft failure safe wait tracking absolute node removal lifecycles over standard intervals.
     *
     * @return {@code true} if detached safely, {@code false} on timeout intervals.
     */
    default boolean waitToBeDetached() {
        return waitToBeDetached(null);
    }

    /**
     * Soft failure safe wait blocking until element enters visibility layout contexts.
     *
     * @param timeout Timeout bounds configuration represented in milliseconds.
     * @return {@code true} if visible, {@code false} if tracking timeline runs out.
     */
    default boolean waitToBeVisible(Double timeout) {
        return waitForLoading(WaitForSelectorState.VISIBLE, timeout);
    }

    /**
     * Soft failure safe wait blocking until element enters visibility layout over standard intervals.
     *
     * @return {@code true} if visible, {@code false} if tracking timeline runs out.
     */
    default boolean waitToBeVisible() {
        return waitToBeVisible(null);
    }

    /**
     * Convenience factory helper establishing single nested children components referencing standard logical labels.
     *
     * @param clazz    The subclass wrapper blueprint mapping target implementations.
     * @param selector The localized child layout search query.
     * @param <T>      The element component type boundary.
     * @return An instantiated relative sub-component layout wrapper.
     */
    default <T extends IElement> T createChildElement(Class<T> clazz, Selector selector) {
        return createChildElement(clazz, selector, "Child of " + getName());
    }

    /**
     * Convenience factory helper establishing single nested elements using localized custom suppliers.
     *
     * @param elementSupplier Custom instance blueprint creation instructions block.
     * @param selector        The localized relative child search criteria.
     * @param <T>             The element component type boundary.
     * @return An instantiated relative sub-component layout wrapper.
     */
    default <T extends IElement> T createChildElement(
            IElementSupplier<T> elementSupplier,
            Selector selector) {
        return createChildElement(elementSupplier, selector, "Child of " + getName());
    }

    /**
     * Convenience collection creation mapper evaluating any item appearance counts.
     *
     * @param clazz    The model class format.
     * @param selector The relative matching locator query.
     * @param name     The identifier tracking string.
     * @param <T>      The target element implementation contract constraint.
     * @return A grouped custom list reference structure.
     */
    default <T extends IElement> IElementCollection<T> createChildElements(
            Class<T> clazz,
            Selector selector,
            String name) {
        return createChildElements(clazz, selector, name, ExpectedCount.ANY);
    }
}