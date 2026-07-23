package org.jcd2052.core.browser.services;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import org.jcd2052.core.browser.browser.interfaces.IBrowser;
import org.jcd2052.core.browser.configuration.IBrowserProperties;
import org.jcd2052.core.browser.factory.IBrowserFactory;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

/**
 * Unit tests for {@link BrowserService}'s {@link ThreadLocal} browser caching/reuse lifecycle.
 * <p>
 * {@link Playwright} and {@link Browser} are plain interfaces in the Playwright Java client, so
 * this is exercised via the test-only {@code (properties, factory, Supplier<Playwright>)}
 * constructor rather than launching a real driver process.
 */
public class BrowserServiceTest {

    @Test
    public void getBrowserLaunchesOnlyOnceAndReusesTheSameInstance() {
        Playwright playwright = mock(Playwright.class);
        Browser rawBrowser = mock(Browser.class);
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserFactory factory = mock(IBrowserFactory.class);
        when(factory.createBrowser(playwright)).thenReturn(rawBrowser);

        BrowserService service = new BrowserService(properties, factory, () -> playwright);

        IBrowser first = service.getBrowser();
        IBrowser second = service.getBrowser();

        assertSame(first, second);
        verify(factory, times(1)).createBrowser(playwright);
    }

    @Test
    public void setBrowserClosesThePreviousInstanceBeforeReplacingIt() {
        Playwright playwright = mock(Playwright.class);
        Browser firstRawBrowser = mock(Browser.class);
        Browser secondRawBrowser = mock(Browser.class);
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserFactory factory = mock(IBrowserFactory.class);
        when(factory.createBrowser(playwright)).thenReturn(firstRawBrowser, secondRawBrowser);

        BrowserService service = new BrowserService(properties, factory, () -> playwright);
        IBrowser firstBrowser = service.getBrowser();

        service.setBrowser(factory);
        IBrowser secondBrowser = service.getBrowser();

        assertTrue(firstBrowser.isClosed());
        assertFalse(secondBrowser.isClosed());
        verify(firstRawBrowser).close();
        verify(factory, times(2)).createBrowser(playwright);
    }

    @Test
    public void closeClosesTheCurrentBrowserAndClearsTheThreadLocalSlot() {
        Playwright playwright = mock(Playwright.class);
        Browser rawBrowser = mock(Browser.class);
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserFactory factory = mock(IBrowserFactory.class);
        when(factory.createBrowser(playwright)).thenReturn(rawBrowser);

        BrowserService service = new BrowserService(properties, factory, () -> playwright);
        IBrowser browser = service.getBrowser();

        service.close();

        assertTrue(browser.isClosed());
        verify(rawBrowser).close();
        // A subsequent getBrowser() call must not reuse the closed instance.
        assertNotSame(service.getBrowser(), browser);
    }

    @Test
    public void setBrowserRejectsANullFactory() {
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserFactory factory = mock(IBrowserFactory.class);
        BrowserService service = new BrowserService(properties, factory, () -> mock(Playwright.class));

        expectThrows(IllegalArgumentException.class, () -> service.setBrowser(null));
    }
}
