package io.github.jcd2052.browser.services;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import io.github.jcd2052.browser.browser.PlaywrightBrowser;
import io.github.jcd2052.browser.browser.interfaces.IBrowser;
import io.github.jcd2052.browser.configuration.IBrowserProperties;
import io.github.jcd2052.browser.factory.IBrowserFactory;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
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
        when(rawBrowser.isConnected()).thenReturn(true);
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserFactory<IBrowserProperties> factory = mock(IBrowserFactory.class);
        when(factory.createBrowser(playwright)).thenReturn(rawBrowser);

        BrowserService<IBrowserProperties> service = new BrowserService<>(properties, factory, () -> playwright);

        IBrowser first = service.getBrowser();
        IBrowser second = service.getBrowser();

        assertSame(first, second);
        verify(factory, times(1)).createBrowser(playwright);
    }

    @Test
    public void setBrowserClosesThePreviousInstanceBeforeReplacingIt() {
        Playwright playwright = mock(Playwright.class);
        Browser firstRawBrowser = mock(Browser.class);
        when(firstRawBrowser.isConnected()).thenReturn(true);
        Browser secondRawBrowser = mock(Browser.class);
        when(secondRawBrowser.isConnected()).thenReturn(true);
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserFactory<IBrowserProperties> factory = mock(IBrowserFactory.class);
        when(factory.createBrowser(playwright)).thenReturn(firstRawBrowser, secondRawBrowser);

        BrowserService<IBrowserProperties> service = new BrowserService<>(properties, factory, () -> playwright);
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
        when(rawBrowser.isConnected()).thenReturn(true);
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserFactory<IBrowserProperties> factory = mock(IBrowserFactory.class);
        when(factory.createBrowser(playwright)).thenReturn(rawBrowser);

        BrowserService<IBrowserProperties> service = new BrowserService<>(properties, factory, () -> playwright);
        IBrowser browser = service.getBrowser();

        service.close();

        assertTrue(browser.isClosed());
        verify(rawBrowser).close();
        assertNotSame(service.getBrowser(), browser);
    }

    @Test
    public void setBrowserRejectsANullFactory() {
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserFactory<IBrowserProperties> factory = mock(IBrowserFactory.class);
        BrowserService<IBrowserProperties> service = new BrowserService<>(properties, factory, () -> mock(Playwright.class));

        expectThrows(IllegalArgumentException.class, () -> service.setBrowser(null));
    }

    @Test
    public void getBrowserReplacesACrashedBrowserAndReleasesItsDriver() {
        Playwright firstPlaywright = mock(Playwright.class);
        Playwright secondPlaywright = mock(Playwright.class);
        Browser crashedBrowser = mock(Browser.class);
        Browser freshBrowser = mock(Browser.class);
        when(freshBrowser.isConnected()).thenReturn(true);
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserFactory<IBrowserProperties> factory = mock(IBrowserFactory.class);
        when(factory.createBrowser(firstPlaywright)).thenReturn(crashedBrowser);
        when(factory.createBrowser(secondPlaywright)).thenReturn(freshBrowser);
        Iterator<Playwright> playwrights = List.of(firstPlaywright, secondPlaywright).iterator();

        BrowserService<IBrowserProperties> service = new BrowserService<>(properties, factory, playwrights::next);
        IBrowser crashed = service.getBrowser();
        IBrowser replacement = service.getBrowser();

        assertNotSame(replacement, crashed);
        assertSame(replacement.getBrowser(), freshBrowser);
        verify(firstPlaywright).close();
    }

    @Test
    public void closeDoesNotThrowAndClearsTheThreadLocalSlotWhenClosingFails() {
        Playwright playwright = mock(Playwright.class);
        Browser rawBrowser = mock(Browser.class);
        when(rawBrowser.isConnected()).thenReturn(true);
        doThrow(new RuntimeException("browser close failed")).when(rawBrowser).close();
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserFactory<IBrowserProperties> factory = mock(IBrowserFactory.class);
        when(factory.createBrowser(playwright)).thenReturn(rawBrowser);

        BrowserService<IBrowserProperties> service = new BrowserService<>(properties, factory, () -> playwright);
        IBrowser browser = service.getBrowser();

        service.close();

        verify(playwright).close();
        assertTrue(browser.isClosed());
        assertNotSame(service.getBrowser(), browser);
    }

    @Test
    public void closeAllClosesBrowsersOpenedByOtherThreads() throws InterruptedException {
        Playwright playwright = mock(Playwright.class);
        Browser mainThreadBrowser = mock(Browser.class);
        Browser workerThreadBrowser = mock(Browser.class);
        when(mainThreadBrowser.isConnected()).thenReturn(true);
        when(workerThreadBrowser.isConnected()).thenReturn(true);
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserFactory<IBrowserProperties> factory = mock(IBrowserFactory.class);
        when(factory.createBrowser(playwright)).thenReturn(mainThreadBrowser, workerThreadBrowser);

        BrowserService<IBrowserProperties> service = new BrowserService<>(properties, factory, () -> playwright);
        IBrowser mainBrowser = service.getBrowser();
        AtomicReference<IBrowser> workerBrowser = new AtomicReference<>();
        Thread worker = new Thread(() -> workerBrowser.set(service.getBrowser()));
        worker.start();
        worker.join();

        service.closeAll();

        assertTrue(mainBrowser.isClosed());
        assertTrue(workerBrowser.get().isClosed());
        verify(mainThreadBrowser).close();
        verify(workerThreadBrowser).close();
        verify(playwright, times(2)).close();
    }

    @Test
    public void browserCloseAlwaysClosesTheDriverAndIsIdempotent() {
        Playwright playwright = mock(Playwright.class);
        Browser rawBrowser = mock(Browser.class);
        doThrow(new RuntimeException("browser close failed")).when(rawBrowser).close();
        IBrowser browser = new PlaywrightBrowser(playwright, rawBrowser, mock(IBrowserProperties.class));

        expectThrows(RuntimeException.class, browser::close);
        browser.close();

        verify(rawBrowser, times(1)).close();
        verify(playwright, times(1)).close();
        assertTrue(browser.isClosed());
    }

    @Test
    public void startReplacesABrowserWhoseConnectionWasLost() {
        Playwright deadPlaywright = mock(Playwright.class);
        Playwright freshPlaywright = mock(Playwright.class);
        Browser deadBrowser = mock(Browser.class);
        when(deadBrowser.isConnected()).thenReturn(true);
        when(deadBrowser.newContext(any(Browser.NewContextOptions.class)))
                .thenThrow(new PlaywrightException("Playwright connection closed"));
        Browser freshBrowser = mock(Browser.class);
        when(freshBrowser.isConnected()).thenReturn(true);
        BrowserContext context = mock(BrowserContext.class);
        when(freshBrowser.newContext(any(Browser.NewContextOptions.class))).thenReturn(context);
        when(context.newPage()).thenReturn(mock(Page.class));
        IBrowserProperties properties = startableProperties();
        IBrowserFactory<IBrowserProperties> factory = mock(IBrowserFactory.class);
        when(factory.createBrowser(deadPlaywright)).thenReturn(deadBrowser);
        when(factory.createBrowser(freshPlaywright)).thenReturn(freshBrowser);
        Iterator<Playwright> playwrights = List.of(deadPlaywright, freshPlaywright).iterator();

        BrowserService<IBrowserProperties> service = new BrowserService<>(properties, factory, playwrights::next);
        service.start();

        assertSame(service.getBrowser().getBrowser(), freshBrowser);
        assertNotNull(service.getBrowser().requireCurrentBrowserTab());
        verify(deadPlaywright).close();
    }

    @Test
    public void startRethrowsFailuresThatAreNotAConnectionLoss() {
        Playwright playwright = mock(Playwright.class);
        Browser rawBrowser = mock(Browser.class);
        when(rawBrowser.isConnected()).thenReturn(true);
        when(rawBrowser.newContext(any(Browser.NewContextOptions.class)))
                .thenThrow(new PlaywrightException("Failed to read storage state from file"));
        IBrowserProperties properties = startableProperties();
        IBrowserFactory<IBrowserProperties> factory = mock(IBrowserFactory.class);
        when(factory.createBrowser(playwright)).thenReturn(rawBrowser);

        BrowserService<IBrowserProperties> service = new BrowserService<>(properties, factory, () -> playwright);

        expectThrows(PlaywrightException.class, service::start);
        verify(factory, times(1)).createBrowser(playwright);
    }

    private static IBrowserProperties startableProperties() {
        IBrowserProperties properties = mock(IBrowserProperties.class);
        when(properties.getWidth()).thenReturn(800);
        when(properties.getHeight()).thenReturn(600);
        when(properties.getTimeout()).thenReturn(1000L);
        when(properties.getPageLoadTimeout()).thenReturn(1000L);
        return properties;
    }
}

