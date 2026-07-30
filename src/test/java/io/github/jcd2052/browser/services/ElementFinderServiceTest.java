package io.github.jcd2052.browser.services;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.jcd2052.browser.browser.interfaces.IBrowser;
import io.github.jcd2052.browser.browser.interfaces.IBrowserTab;
import io.github.jcd2052.browser.browser.interfaces.IBrowserWindow;
import io.github.jcd2052.browser.services.interfaces.IBrowserService;
import io.github.jcd2052.elements.selector.Selector;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertSame;

/**
 * Unit tests for {@link ElementFinderService}, verifying it correctly walks
 * {@code IBrowserService -> IBrowser -> IBrowserWindow -> IBrowserTab -> Page} and hands the
 * resolved {@link Page} to the {@link Selector}, entirely without a real browser.
 */
public class ElementFinderServiceTest {
    @Test
    public void findElementResolvesTheSelectorAgainstTheCurrentThreadsPage() {
        IBrowserService<?> browserService = mock(IBrowserService.class);
        IBrowser browser = mock(IBrowser.class);
        IBrowserWindow window = mock(IBrowserWindow.class);
        IBrowserTab tab = mock(IBrowserTab.class);
        Page page = mock(Page.class);
        Locator expected = mock(Locator.class);
        Selector selector = mock(Selector.class);

        when(browserService.getBrowser()).thenReturn(browser);
        when(browser.getCurrentBrowserWindow()).thenReturn(window);
        when(window.getCurrentBrowserTab()).thenReturn(tab);
        when(tab.getPage()).thenReturn(page);
        when(selector.evaluate(page)).thenReturn(expected);

        ElementFinderService finderService = new ElementFinderService(browserService);
        Locator result = finderService.findElement(selector);

        assertSame(result, expected);
        verify(selector).evaluate(page);
    }

    @Test
    public void findElementAlwaysReReadsTheCurrentPage() {
        IBrowserService<?> browserService = mock(IBrowserService.class);
        IBrowser browser = mock(IBrowser.class);
        IBrowserWindow window = mock(IBrowserWindow.class);
        IBrowserTab tab = mock(IBrowserTab.class);
        Page firstPage = mock(Page.class);
        Page secondPage = mock(Page.class);
        Selector selector = mock(Selector.class);

        when(browserService.getBrowser()).thenReturn(browser);
        when(browser.getCurrentBrowserWindow()).thenReturn(window);
        when(window.getCurrentBrowserTab()).thenReturn(tab);
        when(tab.getPage()).thenReturn(firstPage, secondPage);

        ElementFinderService finderService = new ElementFinderService(browserService);
        finderService.findElement(selector);
        finderService.findElement(selector);

        verify(selector).evaluate(firstPage);
        verify(selector).evaluate(secondPage);
    }
}
