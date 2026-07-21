package org.jcd2052.core.elements.selector;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

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
    @Test
    public void bySelectorEvaluatesAgainstPageAndLocator() {
        Page page = mock(Page.class);
        Locator parent = mock(Locator.class);
        Locator expectedFromPage = mock(Locator.class);
        Locator expectedFromParent = mock(Locator.class);
        when(page.locator("#id")).thenReturn(expectedFromPage);
        when(parent.locator("#id")).thenReturn(expectedFromParent);

        Selector selector = Selector.bySelector("#id");

        assertSame(selector.evaluate(page), expectedFromPage);
        assertSame(selector.evaluate(parent), expectedFromParent);
    }

    @Test
    public void byTextDelegatesToGetByText() {
        Page page = mock(Page.class);
        Locator parent = mock(Locator.class);

        Selector.byText("OK").evaluate(page);
        Selector.byText("OK").evaluate(parent);

        verify(page).getByText("OK");
        verify(parent).getByText("OK");
    }

    @Test
    public void byTextWithExactPassesExactOption() {
        Page page = mock(Page.class);

        Selector.byText("OK", true).evaluate(page);

        verify(page).getByText(eq("OK"), any(Page.GetByTextOptions.class));
    }

    @Test
    public void byRoleWithNameDelegatesCorrectly() {
        Page page = mock(Page.class);
        Locator parent = mock(Locator.class);

        Selector.byRole(AriaRole.BUTTON, "Save").evaluate(page);
        Selector.byRole(AriaRole.BUTTON, "Save").evaluate(parent);

        verify(page).getByRole(eq(AriaRole.BUTTON), any(Page.GetByRoleOptions.class));
        verify(parent).getByRole(eq(AriaRole.BUTTON), any(Locator.GetByRoleOptions.class));
    }

    @Test
    public void byRoleWithoutNameDelegatesCorrectly() {
        Page page = mock(Page.class);

        Selector.byRole(AriaRole.BUTTON).evaluate(page);

        verify(page).getByRole(AriaRole.BUTTON);
    }

    @Test
    public void byRoleWithOptionsTranslatesRoleOptions() {
        Page page = mock(Page.class);
        RoleOptions options = new RoleOptions().setNameString("Save").setExact(true);

        Selector.byRole(AriaRole.BUTTON, options).evaluate(page);

        verify(page).getByRole(eq(AriaRole.BUTTON), any(Page.GetByRoleOptions.class));
    }

    @Test
    public void byTestIdDelegatesToGetByTestId() {
        Page page = mock(Page.class);

        Selector.byTestId("submit-button").evaluate(page);

        verify(page).getByTestId("submit-button");
    }

    @Test
    public void chainEvaluatesParentThenChildAgainstPage() {
        Page page = mock(Page.class);
        Locator parentResolved = mock(Locator.class);
        Locator childResolved = mock(Locator.class);
        when(page.locator("#parent")).thenReturn(parentResolved);
        when(parentResolved.getByText("Child")).thenReturn(childResolved);

        Selector combined = Selector.bySelector("#parent").chain(Selector.byText("Child"));
        Locator result = combined.evaluate(page);

        assertSame(result, childResolved);
        verify(page).locator("#parent");
        verify(parentResolved).getByText("Child");
    }

    @Test
    public void chainWithNullChildReturnsSameSelector() {
        Selector base = Selector.bySelector("#parent");

        assertSame(base.chain(null), base);
    }

    @Test
    public void nthAppendsIndexToResolvedLocator() {
        Page page = mock(Page.class);
        Locator resolved = mock(Locator.class);
        Locator nthResolved = mock(Locator.class);
        when(page.getByTestId("row")).thenReturn(resolved);
        when(resolved.nth(2)).thenReturn(nthResolved);

        Locator result = Selector.byTestId("row").nth(2).evaluate(page);

        assertSame(result, nthResolved);
        verify(resolved).nth(2);
    }

    @Test
    public void byFrameResolvesInnerSelectorInsideTheFrame() {
        Page page = mock(Page.class);
        FrameLocator frameLocator = mock(FrameLocator.class);
        Locator frameRoot = mock(Locator.class);
        Locator innerResolved = mock(Locator.class);
        when(page.frameLocator("#widget")).thenReturn(frameLocator);
        when(frameLocator.locator(":root")).thenReturn(frameRoot);
        when(frameRoot.getByTestId("confirm")).thenReturn(innerResolved);

        Locator result = Selector.byFrame("#widget", Selector.byTestId("confirm")).evaluate(page);

        assertSame(result, innerResolved);
        verify(page).frameLocator("#widget");
        verify(frameRoot).getByTestId("confirm");
    }

    @Test
    public void byFrameSupportsNesting() {
        Page page = mock(Page.class);
        FrameLocator outerFrame = mock(FrameLocator.class);
        Locator outerRoot = mock(Locator.class);
        FrameLocator innerFrame = mock(FrameLocator.class);
        Locator innerRoot = mock(Locator.class);
        Locator finalResolved = mock(Locator.class);
        when(page.frameLocator("#outer")).thenReturn(outerFrame);
        when(outerFrame.locator(":root")).thenReturn(outerRoot);
        when(outerRoot.frameLocator("#inner")).thenReturn(innerFrame);
        when(innerFrame.locator(":root")).thenReturn(innerRoot);
        when(innerRoot.getByRole(AriaRole.BUTTON)).thenReturn(finalResolved);

        Selector nested = Selector.byFrame("#outer", Selector.byFrame("#inner", Selector.byRole(AriaRole.BUTTON)));
        Locator result = nested.evaluate(page);

        assertSame(result, finalResolved);
    }

    @Test
    public void toStringDescribesSimpleStrategies() {
        assertEquals(Selector.byText("OK").toString(), "byText(\"OK\")");
        assertEquals(Selector.bySelector("#id").toString(), "bySelector(\"#id\")");
        assertEquals(Selector.byTestId("submit").toString(), "byTestId(\"submit\")");
        assertEquals(Selector.byRole(AriaRole.BUTTON, "Save").toString(), "byRole(BUTTON, name=\"Save\")");
    }

    @Test
    public void toStringDescribesChainAndNth() {
        Selector chained = Selector.bySelector("#parent").chain(Selector.byText("Child"));
        assertEquals(chained.toString(), "bySelector(\"#parent\") -> byText(\"Child\")");

        Selector nthed = Selector.byTestId("row").nth(2);
        assertEquals(nthed.toString(), "byTestId(\"row\").nth(2)");
    }

    @Test
    public void toStringDescribesFrame() {
        Selector framed = Selector.byFrame("#widget", Selector.byRole(AriaRole.BUTTON, "Confirm"));
        assertEquals(framed.toString(), "byFrame(\"#widget\") -> byRole(BUTTON, name=\"Confirm\")");
    }
}
