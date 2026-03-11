package org.jcd2052.core.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.browser.services.interfaces.IElementSupplier;
import org.jcd2052.core.elements.interfaces.IElement;
import org.jcd2052.core.elements.interfaces.IElementCollection;
import org.jcd2052.core.elements.interfaces.IJsActions;
import org.jcd2052.core.logger.LoggerProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;import java.util.stream.Collectors;

/**
 * The core abstract base class for all web elements in the framework.
 * <p>This class implements the {@link IElement} interface and provides standard wrappers
 * around Playwright's native {@link Locator} interactions. It handles common element behaviors
 * including clicking, typing, state evaluation, and dynamic child element creation.</p>
 * <p>Additionally, it integrates built-in logging, optional visual highlighting, and
 * JavaScript-based action fallbacks via {@link IJsActions}.</p>
 */
public abstract class AbstractElement implements IElement {
    private final String name;
    /**
     * The selector string (e.g., CSS, XPath) used to locate this element in the DOM.
     */
    @Getter
    private final String selector;
    protected final IElementFactory elementFactory;
    /**
     * Provides access to JavaScript-based element interactions.
     */
    @Getter
    protected final IJsActions jsActions;

    /**
     * Constructs a new {@code AbstractElement}.
     *
     * @param selector       The Playwright selector used to locate the element.
     * @param name           A human-readable name for the element, used in logging.
     * @param elementFactory The factory responsible for creating and locating elements.
     */
    protected AbstractElement(String selector, String name, IElementFactory elementFactory) {
        this.elementFactory = elementFactory;
        this.name = name;
        this.selector = selector;
        this.jsActions = new JsActions(this);
    }

    /**
     * Scrolls the element into the visible viewport if it is not already visible.
     */
    @Override
    public void scrollToElement() {
        getLocator().scrollIntoViewIfNeeded();
    }

    /**
     * Performs a standard click on the element.
     * <p>This method delegates to a centralized click handler, ensuring the page
     * is in a ready state before and after the action, and applies a slight delay
     * for enhanced stability.</p>
     */
    @Override
    public void click() {
        LoggerProvider.getLogger().debugElementAction(getElementType(), getName(), "was clicked");
        clickWithOptions(false);
    }

    /**
     * Performs a click on the element using JavaScript injection.
     * <p>This is useful for bypassing elements that intercept native clicks.</p>
     */
    @Override
    public void clickWithJs() {
        LoggerProvider.getLogger().debugElementAction(getElementType(), getName(), "was clicked with JavaScript");
        jsActions.click();
    }

    /**
     * Forces a native click on the element, bypassing Playwright's actionability checks
     * (e.g., visibility, bounding box, intercepting elements).
     * <p>Like standard clicks, this ensures the DOM is ready and applies a brief delay
     * for stability.</p>
     */
    @Override
    public void forceClick() {
        LoggerProvider.getLogger().debugElementAction(getElementType(), getName(), "was force clicked");
        clickWithOptions(true);
    }

    @Override
    public void unfocus() {
        getLocator().blur();
    }

    /**
     * Waits for the element to reach a specific state within the DOM.
     *
     * @param state   The expected state to wait for (e.g., VISIBLE, HIDDEN).
     * @param timeout The maximum time to wait in milliseconds. If null, the default timeout is used.
     */
    @Override
    public void waitForState(WaitForSelectorState state, Double timeout) {
        Locator.WaitForOptions waitOptions = state == null && timeout == null ? null : new Locator.WaitForOptions();
        if (waitOptions != null) {
            waitOptions.setState(state);
            Optional.ofNullable(timeout).ifPresent(waitOptions::setTimeout);
        }
        getLocator().waitFor(waitOptions);
    }

    /**
     * Waits for the underlying web page's document to reach a fully loaded or ready state.
     * <p>This method delegates to the underlying {@link IJsActions} to ensure any
     * asynchronous DOM updates related to this element's interaction have completed.</p>
     */
    @Override
    public void waitForReadyState() {
        jsActions.waitForReadyState();
    }

    /**
     * Simulates pressing a specific keyboard key on the element.
     *
     * @param key The key to press (e.g., "Enter", "ArrowDown").
     */
    @Override
    public void press(String key) {
        LoggerProvider.getLogger().debugElementAction(getElementType(), getName(), "pressed key '%s'", key);
        getLocator().press(key);
    }

    @Override
    public void hover() {
        getLocator().hover();
        LoggerProvider.getLogger().debugElementAction(getElementType(), getName(), "was successfully hovered over");
    }

    /**
     * Retrieves the inner text of all matching elements found by the locator.
     *
     * @return A collection of inner text strings.
     */
    @Override
    public List<String> getAllTexts() {
        highlightElementIfNeeded();
        return Arrays.stream(getLocator().innerText().split(StringUtils.LF)).collect(Collectors.toList());
    }

    /**
     * Retrieves the visible text content of the element.
     *
     * @return The text content of the element.
     */
    @Override
    public String getText() {
        highlightElementIfNeeded();
        String text = getLocator().textContent();
        LoggerProvider.getLogger().debugElementAction(
                getElementType(),
                getName(),
                "text was retrieved. Result: '%s'",
                text);
        return text;
    }

    /**
     * Retrieves the value of a specified HTML attribute from the element.
     *
     * @param name The name of the attribute (e.g., "href", "class").
     * @return The value of the attribute, or null if it does not exist.
     */
    @Override
    public String getAttribute(String name) {
        String attribute = getLocator().getAttribute(name, new Locator.GetAttributeOptions());
        LoggerProvider.getLogger().debugElementAction(
                getElementType(),
                getName(),
                "attribute '%s' was retrieved. Result: '%s'", name, attribute);
        return attribute;
    }

    /**
     * Checks if the element is currently visible in the DOM.
     *
     * @return {@code true} if visible; {@code false} otherwise.
     */
    @Override
    public boolean isVisible() {
        boolean visible = getLocator().isVisible();
        LoggerProvider.getLogger().debugElementAction(
                getElementType(),
                getName(),
                "checked visibility. Result: %b",
                visible);
        return visible;
    }

    /**
     * Checks if the element is currently enabled (i.e., not explicitly disabled).
     *
     * @return {@code true} if enabled; {@code false} otherwise.
     */
    @Override
    public boolean isEnabled() {
        highlightElementIfNeeded();
        boolean enabled = getLocator().isEnabled();
        LoggerProvider.getLogger().debugElementAction(
                getElementType(),
                getName(),
                "checked if enabled. Result: %b",
                enabled);
        return enabled;
    }

    /**
     * Dynamically creates a child element scoped to this parent element.
     *
     * @param clazz    The specific class type of the child element to create.
     * @param selector The relative selector for the child element.
     * @param name     The human-readable name of the child element.
     * @param <T>      The type of the child element, extending {@link IElement}.
     * @return A new instance of the specified child element type.
     */
    @Override
    public <T extends IElement> T createChildElement(Class<T> clazz, String selector, String name) {
        return elementFactory.createChildElement(clazz, this, selector, name);
    }

    /**
     * Dynamically creates a child element using a provided supplier, scoped to this parent element.
     *
     * @param elementSupplier The functional interface supplying the element instance.
     * @param selector        The relative selector for the child element.
     * @param name            The human-readable name of the child element.
     * @param <T>             The type of the child element, extending {@link IElement}.
     * @return A new instance of the specified child element type.
     */
    @Override
    public <T extends IElement> T createChildElement(
            IElementSupplier<T> elementSupplier,
            String selector,
            String name) {
        return elementFactory.createChildElement(elementSupplier, this, selector, name);
    }

    /**
     * Dynamically creates a collection of child elements scoped to this parent element.
     *
     * @param clazz         The specific class type of the child elements.
     * @param selector      The relative selector matching multiple child elements.
     * @param name          The base human-readable name for the elements.
     * @param expectedCount The expected number of elements (used for validation/waiting).
     * @param <T>           The type of the child elements, extending {@link IElement}.
     * @return An {@link IElementCollection} containing the resolved child elements.
     */
    @Override
    public <T extends IElement> IElementCollection<T> createChildElements(
            Class<T> clazz,
            String selector,
            String name,
            ExpectedCount expectedCount) {
        return elementFactory.createChildElementsCollection(clazz, this, selector, name, expectedCount);
    }

    /**
     * Captures a visual screenshot of the specific element.
     *
     * @return A byte array representing the screenshot image data.
     */
    @Override
    public byte[] getScreenshot() {
        return getLocator().screenshot();
    }

    /**
     * Gets the trimmed human-readable name of the element.
     *
     * @return The element's name.
     */
    @Override
    public String getName() {
        return name.trim();
    }

    /**
     * Triggers a visual highlight of the element on the page, provided highlighting
     * is enabled in the framework configuration via the {@code elementFactory}.
     */
    protected void highlightElementIfNeeded() {
        if (elementFactory.isHighlightEnabled()) {
            jsActions.highlight();
        }
    }

    /**
     * Retrieves the simple class name of the concrete element type (e.g., "Button", "TextInput").
     *
     * @return The simple name of the class.
     */
    protected String getElementType() {
        return this.getClass().getSimpleName();
    }

    /**
     * Resolves and retrieves the Playwright {@link Locator} instance for this element.
     * <p>This defers the actual DOM lookup to the {@link IElementFactory}'s finder service,
     * ensuring dynamic resolution when the element is actually interacted with.</p>
     *
     * @return The Playwright {@code Locator} instance.
     */
    protected Locator getLocator() {
        return elementFactory.getElementFinderService().findElement(selector);
    }

    /**
     * A centralized helper method to execute click interactions with specific Playwright options.
     * <p>This ensures a consistent workflow for all native clicks: waiting for the document
     * to be ready, highlighting the element (if enabled), performing the click with a slight
     * delay (100ms) for enhanced stability, and finally waiting for the document ready state again.</p>
     *
     * @param isForce {@code true} to force the click, bypassing Playwright's actionability checks;
     *                {@code false} to perform a standard, safe click.
     */
    private void clickWithOptions(boolean isForce) {
        waitForReadyState();
        highlightElementIfNeeded();
        getLocator().click(new Locator.ClickOptions().setForce(isForce).setDelay(100));
        waitForReadyState();
    }
}