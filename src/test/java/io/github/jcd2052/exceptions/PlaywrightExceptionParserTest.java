package io.github.jcd2052.exceptions;

import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.AriaRole;
import io.github.jcd2052.elements.selector.Selector;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link PlaywrightExceptionParser}. {@link PlaywrightException} and
 * {@link TimeoutError} are plain concrete classes with public {@code (String)} constructors, so
 * these can be exercised directly with real instances — no mocking required.
 */
public class PlaywrightExceptionParserTest {
    /**
     * Verifies that the parsed message shows the selector's readable description (e.g. {@code byRole(BUTTON, ...)}),
     * not an anonymous class name.
     */
    @Test(description = "parse includes the selector's readable description, not its anonymous class name")
    public void parseIncludesTheSelectorsReadableDescriptionNotItsAnonymousClassName() {
        Selector selector = Selector.byRole(AriaRole.BUTTON, "Save");
        PlaywrightException raw = new PlaywrightException("Error: something went wrong");
        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(raw, "Save button", selector, "click");
        Assert.assertTrue(parsed.getMessage().contains("byRole(BUTTON, name=\"Save\")"));
        Assert.assertFalse(parsed.getMessage().endsWith("Locator Strategy: "));
    }

    /**
     * Verifies that the original Playwright exception is kept as the cause of the framework exception.
     */
    @Test(description = "parse preserves the original exception as the cause")
    public void parsePreservesTheOriginalExceptionAsTheCause() {
        PlaywrightException raw = new PlaywrightException("Error: boom");
        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");
        Assert.assertSame(parsed.getCause(), raw);
    }

    /**
     * Verifies that a missing selector is reported as {@code Unknown} instead of failing.
     */
    @Test(description = "parse handles a null selector gracefully")
    public void parseHandlesANullSelectorGracefully() {
        PlaywrightException raw = new PlaywrightException("Error: boom");
        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(raw, "Save button", null, "click");
        Assert.assertTrue(parsed.getMessage().contains("Locator Strategy: Unknown"));
    }

    /**
     * Verifies that a Playwright timeout becomes an {@link ElementTimeoutException}.
     */
    @Test(description = "A timeout error maps to ElementTimeoutException")
    public void timeoutErrorMapsToElementTimeoutException() {
        TimeoutError raw = new TimeoutError("Timeout 5000ms exceeded.\nCall log:\n  - waiting for locator");
        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");
        Assert.assertTrue(parsed instanceof ElementTimeoutException);
    }

    /**
     * Verifies that an element detached during an action becomes an {@link ElementActionException} whose message
     * hints that the element was removed from the DOM.
     */
    @Test(description = "A detached element maps to ElementActionException with an extra hint")
    public void detachedElementMapsToElementActionExceptionWithAnExtraHint() {
        PlaywrightException raw = new PlaywrightException("Error: element is not attached to the DOM, detached");
        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");
        Assert.assertTrue(parsed instanceof ElementActionException);
        Assert.assertTrue(parsed.getMessage().contains("likely removed from the DOM"));
    }

    /**
     * Verifies that a closed target becomes an {@link ElementActionException} whose message hints that the element
     * was removed from the DOM.
     */
    @Test(description = "Target closed maps to ElementActionException with an extra hint")
    public void targetClosedMapsToElementActionExceptionWithAnExtraHint() {
        PlaywrightException raw = new PlaywrightException("Error: Target closed");

        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");

        Assert.assertTrue(parsed instanceof ElementActionException);
        Assert.assertTrue(parsed.getMessage().contains("likely removed from the DOM"));
    }

    /**
     * Verifies that any other Playwright error becomes a plain {@link ElementActionException}, without the
     * removed-from-the-DOM hint.
     */
    @Test(description = "An unmatched error maps to a plain ElementActionException")
    public void unmatchedErrorMapsToPlainElementActionException() {
        PlaywrightException raw = new PlaywrightException("Error: something unexpected happened");

        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");

        Assert.assertTrue(parsed instanceof ElementActionException);
        Assert.assertFalse(parsed.getMessage().contains("likely removed from the DOM"));
    }

    /**
     * Verifies that Playwright's verbose {@code Call log:} section is removed from the message, keeping only the
     * primary error.
     */
    @Test(description = "The message strips the verbose call log section")
    public void messageStripsTheVerboseCallLogSection() {
        PlaywrightException raw = new PlaywrightException(
                "Error: locator.click: Target closed\nCall log:\n  - waiting for locator \"button\"\n  - locator resolved\n  - attempting click action");

        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");

        Assert.assertTrue(parsed.getMessage().contains("locator.click: Target closed"));
        Assert.assertFalse(parsed.getMessage().contains("waiting for locator"));
        Assert.assertFalse(parsed.getMessage().contains("Call log:"));
    }

    /**
     * Verifies that Playwright's leading {@code Error:} prefix is removed from the reason.
     */
    @Test(description = "The message strips the leading Error prefix")
    public void messageStripsTheLeadingErrorPrefix() {
        PlaywrightException raw = new PlaywrightException("Error: strict mode violation");
        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");
        Assert.assertTrue(parsed.getMessage().contains("Reason: strict mode violation"));
    }

    /**
     * Verifies that the message names the action that failed and the element it was performed on.
     */
    @Test(description = "The message includes the action and the element name")
    public void messageIncludesTheActionAndElementName() {
        PlaywrightException raw = new PlaywrightException("Error: boom");
        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");
        Assert.assertTrue(parsed.getMessage().contains("Failed to perform 'click' on element 'Save button'"));
    }

    /**
     * Verifies that a framework exception created from a message alone has no cause.
     */
    @Test(description = "The single-argument exception constructor has no cause")
    public void basePlaywrightExceptionSingleArgConstructorHasNoCause() {
        ElementActionException exception = new ElementActionException("plain message");
        Assert.assertNull(exception.getCause());
    }
}
