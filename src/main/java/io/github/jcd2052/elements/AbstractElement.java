package io.github.jcd2052.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.github.jcd2052.browser.services.interfaces.ILocalizationService;
import io.github.jcd2052.exceptions.BasePlaywrightException;
import io.github.jcd2052.logger.i18n.ILogAction;
import io.github.jcd2052.logger.i18n.LogAction;
import lombok.Getter;
import io.github.jcd2052.browser.browser.MouseActions;
import io.github.jcd2052.browser.browser.interfaces.IMouseActions;
import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.browser.services.interfaces.IElementSupplier;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.IElement;
import io.github.jcd2052.elements.interfaces.IElementCollection;
import io.github.jcd2052.elements.interfaces.IJsActions;
import io.github.jcd2052.exceptions.PlaywrightExceptionParser;
import io.github.jcd2052.logger.LoggerProvider;

import java.util.List;
import java.util.function.Supplier;

/**
 * The base class of every built-in element, implementing {@link IElement} on top of Playwright's {@link Locator}.
 * <p>
 * Every action resolves the element's selector on the current tab first, and goes through
 * {@link #executeAction} or {@link #executeActionReturning}: a successful action is logged at INFO level with a
 * localized message, and a failing one is logged at ERROR level and thrown as a framework exception
 * ({@link io.github.jcd2052.exceptions.ElementTimeoutException} or
 * {@link io.github.jcd2052.exceptions.ElementActionException}). Some reading and typing actions also outline the
 * element in red first when highlighting is enabled.
 * </p>
 * <p>
 * Custom elements extend this class (or one of its subclasses) and route their own actions through the same
 * two methods to get the same logging and error handling.
 * </p>
 */
public abstract class AbstractElement implements IElement {
    /**
     * The element's name, used in log messages and errors.
     */
    private final String name;
    /**
     * The selector the element is found with, resolved on the current tab at every action.
     */
    @Getter
    private final Selector selector;
    /**
     * The factory the element was created by, used to resolve its selector and to create child elements.
     */
    protected final IElementFactory elementFactory;
    /**
     * The element's JavaScript actions.
     */
    @Getter
    protected final IJsActions jsActions;
    /**
     * Builds the element's localized log messages.
     */
    protected final ILocalizationService i18n;

    /**
     * Creates an element. Every element class needs a constructor with exactly these parameters, which the
     * element factory calls by reflection.
     *
     * @param selector       The selector the element is found with.
     * @param name           The element's name, used in log messages and errors.
     * @param elementFactory The factory creating the element.
     */
    protected AbstractElement(
            Selector selector,
            String name,
            IElementFactory elementFactory) {
        this.elementFactory = elementFactory;
        this.name = name;
        this.selector = selector;
        this.i18n = elementFactory.getLocalizationService();
        this.jsActions = new JsActions(this);
    }

    @Override
    public void click() {
        executeAction(() -> getLocator().click(), LogAction.ELEMENT_CLICKED);
    }

    @Override
    public void doubleClick() {
        executeAction(() -> getLocator().dblclick(), LogAction.ELEMENT_DOUBLE_CLICKED);
    }

    @Override
    public void forceClick() {
        executeAction(
                () -> getLocator().click(new Locator.ClickOptions().setForce(true)),
                LogAction.ELEMENT_FORCE_CLICKED);
    }

    @Override
    public void rightClick() {
        executeAction(
                () -> getLocator().click(new Locator.ClickOptions().setButton(MouseButton.RIGHT)),
                LogAction.ELEMENT_RIGHT_CLICKED);
    }

    @Override
    public void middleClick() {
        executeAction(
                () -> getLocator().click(new Locator.ClickOptions().setButton(MouseButton.MIDDLE)),
                LogAction.ELEMENT_MIDDLE_CLICKED);
    }

    @Override
    public void click(double x, double y) {
        executeAction(
                () -> getLocator().click(new Locator.ClickOptions().setPosition(x, y)),
                LogAction.ELEMENT_CLICKED_AT, x, y);
    }

    /**
     * Focuses the element and presses a key or key combination on it.
     *
     * @param key The key or combination, e.g. "Enter" or "Control+A".
     */
    @Override
    public void press(String key) {
        executeAction(() -> getLocator().press(key), LogAction.KEY_PRESSED, key);
    }

    @Override
    public void dragAndDropTo(IElement targetElement, int steps) {
        executeAction(
                () -> {
                    getLocator().scrollIntoViewIfNeeded();
                    BoundingBox sourceBox = getLocator().boundingBox();
                    if (sourceBox == null) {
                        throw new IllegalStateException("Cannot perform dragAndDropTo: source element is not visible.");
                    }
                    getMouseActions().move(sourceBox.x + (sourceBox.width / 2), sourceBox.y + (sourceBox.height / 2));
                    getMouseActions().down();

                    targetElement.getLocator().scrollIntoViewIfNeeded();
                    BoundingBox targetBox = targetElement.getLocator().boundingBox();
                    if (targetBox == null) {
                        throw new IllegalStateException("Cannot perform dragAndDropTo: target element is not visible.");
                    }
                    getMouseActions().move(targetBox.x + (targetBox.width / 2), targetBox.y + (targetBox.height / 2), steps);
                    getMouseActions().up();
                },
                LogAction.ELEMENT_DRAGGED_TO, targetElement.getName());
    }

    @Override
    public void dragTo(IElement targetElement) {
        executeAction(() -> getLocator().dragTo(targetElement.getLocator()),
                LogAction.ELEMENT_DRAGGED_TO, targetElement.getName());
    }

    @Override
    public void hover() {
        executeAction(() -> getLocator().hover(), LogAction.ELEMENT_HOVERED);
    }

    @Override
    public void unfocus() {
        executeAction(() -> getLocator().blur(), LogAction.ELEMENT_UNFOCUSED);
    }

    @Override
    public void scrollToElement() {
        executeAction(() -> getLocator().scrollIntoViewIfNeeded(), LogAction.ELEMENT_SCROLLED_TO);
    }

    @Override
    public void clickWithJs() {
        executeAction(() -> getJsActions().click(), LogAction.ELEMENT_JS_CLICKED);
    }

    @Override
    public byte[] getScreenshot() {
        return executeActionReturning(() -> getLocator().screenshot(), LogAction.ELEMENT_SCREENSHOT_TAKEN);
    }

    /**
     * Waits for the element to reach the given state, failing with a framework exception if it doesn't.
     *
     * @param state   the state to wait for, or {@code null} for Playwright's default ({@code VISIBLE})
     * @param timeout the timeout in milliseconds, or {@code null} for the window's default timeout
     */
    @Override
    public void waitForState(WaitForSelectorState state, Double timeout) {
        executeAction(() -> getLocator().waitFor(buildWaitOptions(state, timeout)),
                LogAction.ELEMENT_STATE_REACHED, describeState(state));
    }

    /**
     * Waits for the element to reach the given state and reports whether it did.
     * <p>
     * Not reaching the state in time is an expected outcome here, so it is logged at INFO level and
     * returned as {@code false} instead of being logged as a failure; any other Playwright error is
     * thrown as a framework exception.
     * </p>
     *
     * @param state   the state to wait for, or {@code null} for Playwright's default ({@code VISIBLE})
     * @param timeout the timeout in milliseconds, or {@code null} for the window's default timeout
     * @return {@code true} if the state was reached in time, {@code false} otherwise
     */
    @Override
    public boolean waitForLoading(WaitForSelectorState state, Double timeout) {
        try {
            getLocator().waitFor(buildWaitOptions(state, timeout));
        } catch (TimeoutError e) {
            logInfo(LogAction.ELEMENT_STATE_NOT_REACHED, describeState(state));
            return false;
        } catch (PlaywrightException e) {
            throw handleException(e, LogAction.ELEMENT_STATE_REACHED);
        }
        logInfo(LogAction.ELEMENT_STATE_REACHED, describeState(state));
        return true;
    }

    @Override
    public IMouseActions getMouseActions() {
        return new MouseActions(getLocator().page().mouse());
    }

    /**
     * Gets the rendered (inner) text of every element matching this element's selector.
     *
     * @return One text per matching element, in page order.
     */
    @Override
    public List<String> getAllTexts() {
        highlightElementIfNeeded();
        return executeActionReturning(() -> getLocator().allInnerTexts(), LogAction.TEXTS_READ);
    }

    /**
     * Gets the element's text content, including text that is hidden with CSS.
     *
     * @return The element's text.
     */
    @Override
    public String getText() {
        highlightElementIfNeeded();
        return executeActionReturning(() -> getLocator().textContent(), LogAction.TEXT_READ);
    }

    /**
     * Gets the value of one of the element's HTML attributes.
     *
     * @param name The attribute name, e.g. "href" or "class".
     * @return The attribute value, or {@code null} if the element has no such attribute.
     */
    @Override
    public String getAttribute(String name) {
        return executeActionReturning(
                () -> getLocator().getAttribute(name, new Locator.GetAttributeOptions()),
                LogAction.ATTRIBUTE_READ, name);
    }

    /**
     * Checks whether the element is visible on the page right now, without waiting.
     *
     * @return {@code true} if visible; {@code false} otherwise.
     */
    @Override
    public boolean isVisible() {
        return executeActionReturning(() -> getLocator().isVisible(), LogAction.ELEMENT_VISIBILITY_CHECKED);
    }

    /**
     * Checks whether the element is enabled (not {@code disabled}) right now, without waiting.
     *
     * @return {@code true} if enabled; {@code false} otherwise.
     */
    @Override
    public boolean isEnabled() {
        highlightElementIfNeeded();
        return executeActionReturning(() -> getLocator().isEnabled(), LogAction.ELEMENT_ENABLED_CHECKED);
    }

    /**
     * Creates an element that is looked up inside this one.
     *
     * @param clazz    The child's element interface or class.
     * @param selector The child's selector, relative to this element.
     * @param name     The child's name.
     * @param <T>      The type of the child element, extending {@link IElement}.
     * @return A new instance of the specified child element type.
     */
    @Override
    public <T extends IElement> T createChildElement(Class<T> clazz, Selector selector, String name) {
        return elementFactory.createChildElement(clazz, this, selector, name);
    }

    /**
     * Creates an element that is looked up inside this one, using a custom supplier to create it.
     *
     * @param elementSupplier Creates the child element.
     * @param selector        The child's selector, relative to this element.
     * @param name            The child's name.
     * @param <T>             The type of the child element, extending {@link IElement}.
     * @return A new instance of the specified child element type.
     */
    @Override
    public <T extends IElement> T createChildElement(
            IElementSupplier<T> elementSupplier,
            Selector selector,
            String name) {
        return elementFactory.createChildElement(elementSupplier, this, selector, name);
    }

    /**
     * Creates a collection of the elements matching a selector inside this one.
     *
     * @param clazz         The elements' interface or class.
     * @param selector      The elements' selector, relative to this element.
     * @param name          The collection's name; each element is named after it with its position.
     * @param expectedCount The count condition to wait for before the elements are read.
     * @param <T>           The type of the child elements, extending {@link IElement}.
     * @return An {@link IElementCollection} containing the resolved child elements.
     */
    @Override
    public <T extends IElement> IElementCollection<T> createChildElements(
            Class<T> clazz,
            Selector selector,
            String name,
            ExpectedCount expectedCount) {
        return elementFactory.createChildElementsCollection(clazz, this, selector, name, expectedCount);
    }

    /**
     * Gets the element's name, with surrounding whitespace removed.
     *
     * @return The element's name.
     */
    @Override
    public String getName() {
        return name.trim();
    }

    /**
     * Resolves the element's selector on the current tab through the factory's finder service. Called at every
     * action, so the element always works with the current tab.
     *
     * @return The Playwright locator.
     */
    @Override
    public Locator getLocator() {
        return elementFactory.getElementFinderService().findElement(selector);
    }

    /**
     * Outlines the element in red on the page when highlighting is enabled in the configuration.
     */
    protected void highlightElementIfNeeded() {
        if (elementFactory.isHighlightEnabled()) {
            executeAction(
                    () -> getLocator().highlight(new Locator.HighlightOptions().setStyle("outline: 3px solid red")),
                    LogAction.ELEMENT_HIGHLIGHTED);
        }
    }

    /**
     * Runs an action on the element with the framework's logging and error handling: on success the action's
     * message is logged at INFO level; a Playwright failure is logged at ERROR level and thrown as a framework
     * exception naming the element and its selector.
     *
     * @param action    The action to run.
     * @param logAction The action to log on success, or {@code null} to log nothing on success; a {@link LogAction}
     *                  or a custom one from {@link ILogAction#of(String)}.
     * @param args      The log message's arguments after the element's name (see {@link LogAction}).
     */
    protected void executeAction(Runnable action, ILogAction logAction, Object... args) {
        executeActionReturning(() -> {
            action.run();
            return null;
        }, logAction, args);
    }

    /**
     * Runs an action that returns a value, with the same logging and error handling as
     * {@link #executeAction(Runnable, ILogAction, Object...)}.
     *
     * @param <R>       The type of the action's result.
     * @param action    The action to run.
     * @param logAction The action to log on success, or {@code null} to log nothing on success.
     * @param args      The log message's arguments after the element's name (see {@link LogAction}).
     * @return The action's result.
     * @throws io.github.jcd2052.exceptions.BasePlaywrightException if Playwright fails the action.
     */
    protected <R> R executeActionReturning(Supplier<R> action, ILogAction logAction, Object... args) {
        try {
            R result = action.get();
            if (logAction != null) {
                logInfo(logAction, args);
            }
            return result;
        } catch (PlaywrightException e) {
            throw handleException(e, logAction);
        }
    }

    /**
     * Formats the arguments (prefixed with this element's name) and logs the localized message at INFO level.
     */
    private void logInfo(ILogAction logAction, Object... args) {
        Object[] formatArgs = new Object[args.length + 1];
        formatArgs[0] = this.name;
        System.arraycopy(args, 0, formatArgs, 1, args.length);

        LoggerProvider.getLogger().info(i18n.getMessage(logAction, formatArgs));
    }

    /**
     * Translates a Playwright failure into a framework exception and logs it at ERROR level.
     */
    private BasePlaywrightException handleException(PlaywrightException e, ILogAction logAction) {
        BasePlaywrightException parsedException = PlaywrightExceptionParser.parse(
                e,
                this.name,
                this.selector,
                logAction != null ? logAction.getActionName() : "unknown action"
        );

        String errorMessage = i18n.getMessage(LogAction.ACTION_FAILED, this.name, parsedException.getMessage());
        LoggerProvider.getLogger().error(errorMessage);

        return parsedException;
    }

    private static Locator.WaitForOptions buildWaitOptions(WaitForSelectorState state, Double timeout) {
        Locator.WaitForOptions options = new Locator.WaitForOptions();
        if (state != null) {
            options.setState(state);
        }
        if (timeout != null) {
            options.setTimeout(timeout);
        }
        return options;
    }

    private static String describeState(WaitForSelectorState state) {
        return (state != null ? state : WaitForSelectorState.VISIBLE).name().toLowerCase();
    }
}
