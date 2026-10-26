package io.github.jcd2052.elements.selector;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link Selector}.
 * <p>
 * {@link Page}, {@link Locator}, and {@link FrameLocator} are plain interfaces in the Playwright
 * Java client, so they can be mocked directly with Mockito without touching a real browser —
 * these tests verify that each strategy calls the correct Playwright method with the correct
 * arguments, that {@code chain()}/{@code nth()}/{@code byFrame()} compose correctly, and that
 * {@code toString()} produces the expected description.
 */
public class SelectorTest {
    /**
     * Verifies that a CSS/XPath selector resolves with {@code locator(...)} both from a page and from a parent locator.
     */
    @Test(description = "bySelector evaluates against a page and a parent locator")
    public void bySelectorEvaluatesAgainstPageAndLocator() {
        Page page = Mockito.mock(Page.class);
        Locator parent = Mockito.mock(Locator.class);
        Locator expectedFromPage = Mockito.mock(Locator.class);
        Locator expectedFromParent = Mockito.mock(Locator.class);
        Mockito.when(page.locator("#id")).thenReturn(expectedFromPage);
        Mockito.when(parent.locator("#id")).thenReturn(expectedFromParent);

        Selector selector = Selector.bySelector("#id");

        Assert.assertSame(selector.evaluate(page), expectedFromPage);
        Assert.assertSame(selector.evaluate(parent), expectedFromParent);
    }

    /**
     * Verifies that a text selector resolves with Playwright's {@code getByText(...)} from a page and from a parent
     * locator.
     */
    @Test(description = "byText delegates to getByText")
    public void byTextDelegatesToGetByText() {
        Page page = Mockito.mock(Page.class);
        Locator parent = Mockito.mock(Locator.class);

        Selector.byText("OK").evaluate(page);
        Selector.byText("OK").evaluate(parent);

        Mockito.verify(page).getByText("OK");
        Mockito.verify(parent).getByText("OK");
    }

    /**
     * Verifies that an exact text selector passes text-matching options to {@code getByText(...)}.
     */
    @Test(description = "byText with exact passes the exact option")
    public void byTextWithExactPassesExactOption() {
        Page page = Mockito.mock(Page.class);

        Selector.byText("OK", true).evaluate(page);

        Mockito.verify(page).getByText(ArgumentMatchers.eq("OK"), ArgumentMatchers.any(Page.GetByTextOptions.class));
    }

    /**
     * Verifies that a role selector with an accessible name resolves with {@code getByRole(...)} and role options,
     * from a page and from a parent locator.
     */
    @Test(description = "byRole with a name delegates correctly")
    public void byRoleWithNameDelegatesCorrectly() {
        Page page = Mockito.mock(Page.class);
        Locator parent = Mockito.mock(Locator.class);

        Selector.byRole(AriaRole.BUTTON, "Save").evaluate(page);
        Selector.byRole(AriaRole.BUTTON, "Save").evaluate(parent);

        Mockito.verify(page).getByRole(ArgumentMatchers.eq(AriaRole.BUTTON), ArgumentMatchers.any(Page.GetByRoleOptions.class));
        Mockito.verify(parent).getByRole(ArgumentMatchers.eq(AriaRole.BUTTON), ArgumentMatchers.any(Locator.GetByRoleOptions.class));
    }

    /**
     * Verifies that a role selector without a name resolves with the plain {@code getByRole(role)} call.
     */
    @Test(description = "byRole without a name delegates correctly")
    public void byRoleWithoutNameDelegatesCorrectly() {
        Page page = Mockito.mock(Page.class);

        Selector.byRole(AriaRole.BUTTON).evaluate(page);

        Mockito.verify(page).getByRole(AriaRole.BUTTON);
    }

    /**
     * Verifies that a role selector built from {@link RoleOptions} translates them into Playwright's role options.
     */
    @Test(description = "byRole with options translates RoleOptions")
    public void byRoleWithOptionsTranslatesRoleOptions() {
        Page page = Mockito.mock(Page.class);
        RoleOptions options = new RoleOptions().setNameString("Save").setExact(true);

        Selector.byRole(AriaRole.BUTTON, options).evaluate(page);

        Mockito.verify(page).getByRole(ArgumentMatchers.eq(AriaRole.BUTTON), ArgumentMatchers.any(Page.GetByRoleOptions.class));
    }

    /**
     * Verifies that a test-id selector resolves with Playwright's {@code getByTestId(...)}.
     */
    @Test(description = "byTestId delegates to getByTestId")
    public void byTestIdDelegatesToGetByTestId() {
        Page page = Mockito.mock(Page.class);

        Selector.byTestId("submit-button").evaluate(page);

        Mockito.verify(page).getByTestId("submit-button");
    }

    /**
     * Verifies that a chained selector resolves the parent from the page first, then the child inside the
     * resolved parent.
     */
    @Test(description = "chain evaluates the parent, then the child against it")
    public void chainEvaluatesParentThenChildAgainstPage() {
        Page page = Mockito.mock(Page.class);
        Locator parentResolved = Mockito.mock(Locator.class);
        Locator childResolved = Mockito.mock(Locator.class);
        Mockito.when(page.locator("#parent")).thenReturn(parentResolved);
        Mockito.when(parentResolved.getByText("Child")).thenReturn(childResolved);

        Selector combined = Selector.bySelector("#parent").chain(Selector.byText("Child"));
        Locator result = combined.evaluate(page);

        Assert.assertSame(result, childResolved);
        Mockito.verify(page).locator("#parent");
        Mockito.verify(parentResolved).getByText("Child");
    }

    /**
     * Verifies that chaining a {@code null} child returns the original selector unchanged.
     */
    @Test(description = "chain with a null child returns the same selector")
    public void chainWithNullChildReturnsSameSelector() {
        Selector base = Selector.bySelector("#parent");

        Assert.assertSame(base.chain(null), base);
    }

    /**
     * Verifies that {@code nth(index)} narrows the resolved locator to the element at that zero-based index.
     */
    @Test(description = "nth appends the index to the resolved locator")
    public void nthAppendsIndexToResolvedLocator() {
        Page page = Mockito.mock(Page.class);
        Locator resolved = Mockito.mock(Locator.class);
        Locator nthResolved = Mockito.mock(Locator.class);
        Mockito.when(page.getByTestId("row")).thenReturn(resolved);
        Mockito.when(resolved.nth(2)).thenReturn(nthResolved);

        Locator result = Selector.byTestId("row").nth(2).evaluate(page);

        Assert.assertSame(result, nthResolved);
        Mockito.verify(resolved).nth(2);
    }

    /**
     * Verifies that a frame selector enters the matched {@code <iframe>} and resolves the inner selector against the
     * frame's document root.
     */
    @Test(description = "byFrame resolves the inner selector inside the frame")
    public void byFrameResolvesInnerSelectorInsideTheFrame() {
        Page page = Mockito.mock(Page.class);
        FrameLocator frameLocator = Mockito.mock(FrameLocator.class);
        Locator frameRoot = Mockito.mock(Locator.class);
        Locator innerResolved = Mockito.mock(Locator.class);
        Mockito.when(page.frameLocator("#widget")).thenReturn(frameLocator);
        Mockito.when(frameLocator.locator(":root")).thenReturn(frameRoot);
        Mockito.when(frameRoot.getByTestId("confirm")).thenReturn(innerResolved);

        Locator result = Selector.byFrame("#widget", Selector.byTestId("confirm")).evaluate(page);

        Assert.assertSame(result, innerResolved);
        Mockito.verify(page).frameLocator("#widget");
        Mockito.verify(frameRoot).getByTestId("confirm");
    }

    /**
     * Verifies that frame selectors can be nested to reach an element inside an iframe within another iframe.
     */
    @Test(description = "byFrame supports nesting")
    public void byFrameSupportsNesting() {
        Page page = Mockito.mock(Page.class);
        FrameLocator outerFrame = Mockito.mock(FrameLocator.class);
        Locator outerRoot = Mockito.mock(Locator.class);
        FrameLocator innerFrame = Mockito.mock(FrameLocator.class);
        Locator innerRoot = Mockito.mock(Locator.class);
        Locator finalResolved = Mockito.mock(Locator.class);
        Mockito.when(page.frameLocator("#outer")).thenReturn(outerFrame);
        Mockito.when(outerFrame.locator(":root")).thenReturn(outerRoot);
        Mockito.when(outerRoot.frameLocator("#inner")).thenReturn(innerFrame);
        Mockito.when(innerFrame.locator(":root")).thenReturn(innerRoot);
        Mockito.when(innerRoot.getByRole(AriaRole.BUTTON)).thenReturn(finalResolved);

        Selector nested = Selector.byFrame("#outer", Selector.byFrame("#inner", Selector.byRole(AriaRole.BUTTON)));
        Locator result = nested.evaluate(page);

        Assert.assertSame(result, finalResolved);
    }

    /**
     * Verifies the readable descriptions of simple strategies, as used in logs and error messages.
     */
    @Test(description = "toString describes simple strategies")
    public void toStringDescribesSimpleStrategies() {
        Assert.assertEquals(Selector.byText("OK").toString(), "byText(\"OK\")");
        Assert.assertEquals(Selector.bySelector("#id").toString(), "bySelector(\"#id\")");
        Assert.assertEquals(Selector.byTestId("submit").toString(), "byTestId(\"submit\")");
        Assert.assertEquals(Selector.byRole(AriaRole.BUTTON, "Save").toString(), "byRole(BUTTON, name=\"Save\")");
    }

    /**
     * Verifies the readable descriptions of chained and indexed selectors.
     */
    @Test(description = "toString describes chain and nth")
    public void toStringDescribesChainAndNth() {
        Selector chained = Selector.bySelector("#parent").chain(Selector.byText("Child"));
        Assert.assertEquals(chained.toString(), "bySelector(\"#parent\") -> byText(\"Child\")");

        Selector nthed = Selector.byTestId("row").nth(2);
        Assert.assertEquals(nthed.toString(), "byTestId(\"row\").nth(2)");
    }

    /**
     * Verifies the readable description of a frame selector, including its inner selector.
     */
    @Test(description = "toString describes a frame")
    public void toStringDescribesFrame() {
        Selector framed = Selector.byFrame("#widget", Selector.byRole(AriaRole.BUTTON, "Confirm"));
        Assert.assertEquals(framed.toString(), "byFrame(\"#widget\") -> byRole(BUTTON, name=\"Confirm\")");
    }
}
