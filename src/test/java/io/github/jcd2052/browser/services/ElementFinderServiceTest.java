package io.github.jcd2052.browser.services;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.jcd2052.browser.browser.interfaces.IBrowser;
import io.github.jcd2052.browser.browser.interfaces.IBrowserTab;
import io.github.jcd2052.browser.browser.interfaces.IBrowserWindow;
import io.github.jcd2052.browser.services.interfaces.IBrowserService;
import io.github.jcd2052.elements.selector.Selector;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link ElementFinderService}, verifying it correctly walks
 * {@code IBrowserService -> IBrowser -> IBrowserWindow -> IBrowserTab -> Page} and hands the
 * resolved {@link Page} to the {@link Selector}, entirely without a real browser.
 */
public class ElementFinderServiceTest {
    /**
     * Verifies that the selector is resolved against the page of the current thread's current tab, and that the
     * resulting locator is returned.
     */
    @Test(description = "findElement resolves the selector against the current thread's page")
    public void findElementResolvesTheSelectorAgainstTheCurrentThreadsPage() {
        IBrowserService<?> browserService = Mockito.mock(IBrowserService.class);
        IBrowser browser = Mockito.mock(IBrowser.class);
        IBrowserWindow window = Mockito.mock(IBrowserWindow.class);
        IBrowserTab tab = Mockito.mock(IBrowserTab.class);
        Page page = Mockito.mock(Page.class);
        Locator expected = Mockito.mock(Locator.class);
        Selector selector = Mockito.mock(Selector.class);

        Mockito.when(browserService.getBrowser()).thenReturn(browser);
        Mockito.when(browser.requireCurrentBrowserTab()).thenCallRealMethod();
        Mockito.when(browser.requireCurrentBrowserWindow()).thenCallRealMethod();
        Mockito.when(browser.getCurrentBrowserWindow()).thenReturn(window);
        Mockito.when(window.getCurrentBrowserTab()).thenReturn(tab);
        Mockito.when(tab.getPage()).thenReturn(page);
        Mockito.when(selector.evaluate(page)).thenReturn(expected);

        ElementFinderService finderService = new ElementFinderService(browserService);
        Locator result = finderService.findElement(selector);

        Assert.assertSame(result, expected);
        Mockito.verify(selector).evaluate(page);
    }

    /**
     * Verifies that every lookup reads the current page again instead of caching it, so elements follow the user
     * to whichever tab is current at the moment of the action.
     */
    @Test(description = "findElement always re-reads the current page")
    public void findElementAlwaysReReadsTheCurrentPage() {
        IBrowserService<?> browserService = Mockito.mock(IBrowserService.class);
        IBrowser browser = Mockito.mock(IBrowser.class);
        IBrowserWindow window = Mockito.mock(IBrowserWindow.class);
        IBrowserTab tab = Mockito.mock(IBrowserTab.class);
        Page firstPage = Mockito.mock(Page.class);
        Page secondPage = Mockito.mock(Page.class);
        Selector selector = Mockito.mock(Selector.class);

        Mockito.when(browserService.getBrowser()).thenReturn(browser);
        Mockito.when(browser.requireCurrentBrowserTab()).thenCallRealMethod();
        Mockito.when(browser.requireCurrentBrowserWindow()).thenCallRealMethod();
        Mockito.when(browser.getCurrentBrowserWindow()).thenReturn(window);
        Mockito.when(window.getCurrentBrowserTab()).thenReturn(tab);
        Mockito.when(tab.getPage()).thenReturn(firstPage, secondPage);

        ElementFinderService finderService = new ElementFinderService(browserService);
        finderService.findElement(selector);
        finderService.findElement(selector);

        Mockito.verify(selector).evaluate(firstPage);
        Mockito.verify(selector).evaluate(secondPage);
    }

    /**
     * Verifies that a lookup with no open window fails with an {@link IllegalStateException} explaining that no
     * browser window is open, instead of a {@link NullPointerException}.
     */
    @Test(description = "findElement fails with a clear message when no window is open")
    public void findElementFailsWithAClearMessageWhenNoWindowIsOpen() {
        IBrowserService<?> browserService = Mockito.mock(IBrowserService.class);
        IBrowser browser = Mockito.mock(IBrowser.class);

        Mockito.when(browserService.getBrowser()).thenReturn(browser);
        Mockito.when(browser.requireCurrentBrowserTab()).thenCallRealMethod();
        Mockito.when(browser.requireCurrentBrowserWindow()).thenCallRealMethod();

        IllegalStateException exception = Assert.expectThrows(IllegalStateException.class,
                () -> new ElementFinderService(browserService).findElement(Mockito.mock(Selector.class)));

        Assert.assertTrue(exception.getMessage().contains("No browser window is open"));
    }

    /**
     * Verifies that a lookup in a window with no open tab fails with an {@link IllegalStateException} explaining
     * that no browser tab is open.
     */
    @Test(description = "findElement fails with a clear message when no tab is open")
    public void findElementFailsWithAClearMessageWhenNoTabIsOpen() {
        IBrowserService<?> browserService = Mockito.mock(IBrowserService.class);
        IBrowser browser = Mockito.mock(IBrowser.class);
        IBrowserWindow window = Mockito.mock(IBrowserWindow.class);

        Mockito.when(browserService.getBrowser()).thenReturn(browser);
        Mockito.when(browser.requireCurrentBrowserTab()).thenCallRealMethod();
        Mockito.when(browser.requireCurrentBrowserWindow()).thenCallRealMethod();
        Mockito.when(browser.getCurrentBrowserWindow()).thenReturn(window);

        IllegalStateException exception = Assert.expectThrows(IllegalStateException.class,
                () -> new ElementFinderService(browserService).findElement(Mockito.mock(Selector.class)));

        Assert.assertTrue(exception.getMessage().contains("No browser tab is open"));
    }
}
