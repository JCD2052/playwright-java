package org.jcd2052.core.exceptions;

import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.AriaRole;
import org.jcd2052.core.elements.selector.Selector;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;

/**
 * Unit tests for {@link PlaywrightExceptionParser}. {@link PlaywrightException} and
 * {@link TimeoutError} are plain concrete classes with public {@code (String)} constructors, so
 * these can be exercised directly with real instances — no mocking required.
 */
public class PlaywrightExceptionParserTest {
    @Test
    public void parseIncludesTheSelectorsReadableDescriptionNotItsAnonymousClassName() {
        Selector selector = Selector.byRole(AriaRole.BUTTON, "Save");
        PlaywrightException raw = new PlaywrightException("Error: something went wrong");
        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(raw, "Save button", selector, "click");
        assertTrue(parsed.getMessage().contains("byRole(BUTTON, name=\"Save\")"));
        assertFalse(parsed.getMessage().endsWith("Locator Strategy: "));
    }

    @Test
    public void parsePreservesTheOriginalExceptionAsTheCause() {
        PlaywrightException raw = new PlaywrightException("Error: boom");
        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");
        assertSame(parsed.getCause(), raw);
    }

    @Test
    public void parseHandlesANullSelectorGracefully() {
        PlaywrightException raw = new PlaywrightException("Error: boom");
        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(raw, "Save button", null, "click");
        assertTrue(parsed.getMessage().contains("Locator Strategy: Unknown"));
    }

    @Test
    public void timeoutErrorMapsToElementTimeoutException() {
        TimeoutError raw = new TimeoutError("Timeout 5000ms exceeded.\nCall log:\n  - waiting for locator");
        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");
        assertTrue(parsed instanceof ElementTimeoutException);
    }

    @Test
    public void detachedElementMapsToElementActionExceptionWithAnExtraHint() {
        PlaywrightException raw = new PlaywrightException("Error: element is not attached to the DOM, detached");
        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");
        assertTrue(parsed instanceof ElementActionException);
        assertTrue(parsed.getMessage().contains("likely removed from the DOM"));
    }

    @Test
    public void targetClosedMapsToElementActionExceptionWithAnExtraHint() {
        PlaywrightException raw = new PlaywrightException("Error: Target closed");

        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");

        assertTrue(parsed instanceof ElementActionException);
        assertTrue(parsed.getMessage().contains("likely removed from the DOM"));
    }

    @Test
    public void unmatchedErrorMapsToPlainElementActionException() {
        PlaywrightException raw = new PlaywrightException("Error: something unexpected happened");

        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");

        assertTrue(parsed instanceof ElementActionException);
        assertFalse(parsed.getMessage().contains("likely removed from the DOM"));
    }

    @Test
    public void messageStripsTheVerboseCallLogSection() {
        PlaywrightException raw = new PlaywrightException(
                "Error: locator.click: Target closed\nCall log:\n  - waiting for locator \"button\"\n  - locator resolved\n  - attempting click action");

        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");

        assertTrue(parsed.getMessage().contains("locator.click: Target closed"));
        assertFalse(parsed.getMessage().contains("waiting for locator"));
        assertFalse(parsed.getMessage().contains("Call log:"));
    }

    @Test
    public void messageStripsTheLeadingErrorPrefix() {
        PlaywrightException raw = new PlaywrightException("Error: strict mode violation");
        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");
        assertTrue(parsed.getMessage().contains("Reason: strict mode violation"));
    }

    @Test
    public void messageIncludesTheActionAndElementName() {
        PlaywrightException raw = new PlaywrightException("Error: boom");
        BasePlaywrightException parsed = PlaywrightExceptionParser.parse(
                raw, "Save button", Selector.bySelector("#save"), "click");
        assertTrue(parsed.getMessage().contains("Failed to perform 'click' on element 'Save button'"));
    }

    @Test
    public void basePlaywrightExceptionSingleArgConstructorHasNoCause() {
        ElementActionException exception = new ElementActionException("plain message");
        assertNull(exception.getCause());
    }
}
