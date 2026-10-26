package io.github.jcd2052.browser.services;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.Tracing;
import io.github.jcd2052.browser.browser.PlaywrightBrowser;
import io.github.jcd2052.browser.browser.interfaces.IBrowser;
import io.github.jcd2052.browser.configuration.BrowserProperties;
import io.github.jcd2052.browser.configuration.IBrowserProperties;
import io.github.jcd2052.browser.factory.IBrowserFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * Unit tests for {@link BrowserService}'s one-browser-per-thread lifecycle and for closing a
 * {@link PlaywrightBrowser}.
 * <p>
 * {@link Playwright} and {@link Browser} are plain interfaces in the Playwright Java client, so this
 * is exercised via the test-only {@code (properties, factory, Supplier<Playwright>)} constructor
 * rather than launching a real driver process.
 * </p>
 */
public class BrowserServiceTest {

    /**
     * Verifies that the first {@link BrowserService#getBrowser()} launches a browser through the factory with an
     * open window and tab, and that later calls on the same thread return that same browser.
     */
    @Test(description = "getBrowser launches on first use with a window and then reuses it")
    public void getBrowserLaunchesOnFirstUseWithAWindowAndThenReusesIt() {
        Playwright playwright = Mockito.mock(Playwright.class);
        IBrowserFactory factory = factoryLaunching(runningBrowser());
        BrowserService<IBrowserProperties> service = service(factory, () -> playwright);

        IBrowser first = service.getBrowser();
        IBrowser second = service.getBrowser();

        Assert.assertSame(second, first);
        Mockito.verify(factory, Mockito.times(1)).createBrowser(playwright);
        Assert.assertNotNull(first.requireCurrentBrowserTab());
    }

    /**
     * Verifies that a newly created browser opens exactly one window, and that returning the running browser
     * afterwards does not open another one.
     */
    @Test(description = "getBrowser opens only the first window and returns a running browser as is")
    public void getBrowserOpensOnlyTheFirstWindowAndReturnsARunningBrowserAsIs() {
        Browser rawBrowser = runningBrowser();
        BrowserService<IBrowserProperties> service = service(factoryLaunching(rawBrowser), () -> Mockito.mock(Playwright.class));

        service.getBrowser();
        service.getBrowser();

        Mockito.verify(rawBrowser, Mockito.times(1)).newContext(ArgumentMatchers.any(Browser.NewContextOptions.class));
        Assert.assertEquals(service.getBrowser().getBrowserWindows().size(), 1);
    }

    /**
     * Verifies that {@link BrowserService#setBrowser(IBrowserFactory)} closes the thread's previous browser and its
     * driver before replacing it with a new one.
     */
    @Test(description = "setBrowser closes the previous instance before replacing it")
    public void setBrowserClosesThePreviousInstanceBeforeReplacingIt() {
        Playwright firstPlaywright = Mockito.mock(Playwright.class);
        Playwright secondPlaywright = Mockito.mock(Playwright.class);
        Iterator<Playwright> playwrights = List.of(firstPlaywright, secondPlaywright).iterator();
        IBrowserFactory factory = factoryLaunching(runningBrowser(), runningBrowser());
        BrowserService<IBrowserProperties> service = service(factory, playwrights::next);
        IBrowser firstBrowser = service.getBrowser();

        service.setBrowser(factory);

        Assert.assertNotSame(service.getBrowser(), firstBrowser);
        Assert.assertTrue(firstBrowser.isClosed());
        Mockito.verify(firstPlaywright).close();
        Mockito.verify(secondPlaywright, Mockito.never()).close();
    }

    /**
     * Verifies that {@link BrowserService#close()} closes the thread's browser and its driver, and that the next
     * {@link BrowserService#getBrowser()} creates a new browser.
     */
    @Test(description = "close closes the browser and its driver and clears the thread-local slot")
    public void closeClosesTheBrowserAndItsDriverAndClearsTheThreadLocalSlot() {
        Playwright playwright = Mockito.mock(Playwright.class);
        BrowserService<IBrowserProperties> service = service(factoryLaunching(runningBrowser(), runningBrowser()), () -> playwright);
        IBrowser browser = service.getBrowser();

        service.close();

        Assert.assertTrue(browser.isClosed());
        Mockito.verify(playwright).close();
        Assert.assertNotSame(service.getBrowser(), browser);
    }

    /**
     * Verifies that {@link BrowserService#setBrowser(IBrowserFactory)} rejects a {@code null} factory.
     */
    @Test(description = "setBrowser rejects a null factory")
    public void setBrowserRejectsANullFactory() {
        BrowserService<IBrowserProperties> service =
                service(factoryLaunching(runningBrowser()), () -> Mockito.mock(Playwright.class));

        Assert.expectThrows(IllegalArgumentException.class, () -> service.setBrowser(null));
    }

    /**
     * Verifies that when the thread's browser disconnects on its own (a crash), the next
     * {@link BrowserService#getBrowser()} closes its driver and returns a newly launched browser.
     */
    @Test(description = "getBrowser replaces a crashed browser and releases its driver")
    public void getBrowserReplacesACrashedBrowserAndReleasesItsDriver() {
        Playwright firstPlaywright = Mockito.mock(Playwright.class);
        Playwright secondPlaywright = Mockito.mock(Playwright.class);
        AtomicBoolean connected = new AtomicBoolean(true);
        Browser crashingBrowser = runningBrowser();
        Mockito.when(crashingBrowser.isConnected()).thenAnswer(invocation -> connected.get());
        Browser freshBrowser = runningBrowser();
        Iterator<Playwright> playwrights = List.of(firstPlaywright, secondPlaywright).iterator();
        BrowserService<IBrowserProperties> service =
                service(factoryLaunching(crashingBrowser, freshBrowser), playwrights::next);
        IBrowser crashed = service.getBrowser();

        connected.set(false);
        IBrowser replacement = service.getBrowser();

        Assert.assertNotSame(replacement, crashed);
        Assert.assertSame(replacement.getBrowser(), freshBrowser);
        Mockito.verify(firstPlaywright).close();
    }

    /**
     * Verifies that a failure while closing the driver is not thrown from {@link BrowserService#close()}, so it
     * cannot fail test teardown, and that the next {@link BrowserService#getBrowser()} still creates a new browser.
     */
    @Test(description = "close does not throw and clears the thread-local slot when closing fails")
    public void closeDoesNotThrowAndClearsTheThreadLocalSlotWhenClosingFails() {
        Playwright playwright = Mockito.mock(Playwright.class);
        Mockito.doThrow(new RuntimeException("driver close failed")).when(playwright).close();
        BrowserService<IBrowserProperties> service = service(factoryLaunching(runningBrowser(), runningBrowser()), () -> playwright);
        IBrowser browser = service.getBrowser();

        service.close();

        Assert.assertTrue(browser.isClosed());
        Assert.assertNotSame(service.getBrowser(), browser);
    }

    /**
     * Verifies that when a newly launched browser cannot open its first window, the failure is thrown and the new
     * Playwright driver is closed, so no process is left running.
     */
    @Test(description = "A failed first window closes the new driver")
    public void aFailedFirstWindowClosesTheNewDriver() {
        Playwright playwright = Mockito.mock(Playwright.class);
        Browser rawBrowser = runningBrowser();
        Mockito.doThrow(new PlaywrightException("Failed to read storage state from file"))
                .when(rawBrowser).newContext(ArgumentMatchers.any(Browser.NewContextOptions.class));
        BrowserService<IBrowserProperties> service = service(factoryLaunching(rawBrowser), () -> playwright);

        Assert.expectThrows(PlaywrightException.class, service::getBrowser);
        Mockito.verify(playwright).close();
    }

    /**
     * Verifies that when the factory fails to launch the browser, the failure is thrown and the new Playwright
     * driver is closed, so no process is left running.
     */
    @Test(description = "A failed launch closes the new driver")
    public void aFailedLaunchClosesTheNewDriver() {
        Playwright playwright = Mockito.mock(Playwright.class);
        IBrowserFactory factory = factoryLaunching(runningBrowser());
        Mockito.when(factory.createBrowser(playwright)).thenThrow(new IllegalArgumentException("Unsupported browser"));
        BrowserService<IBrowserProperties> service = service(factory, () -> playwright);

        Assert.expectThrows(IllegalArgumentException.class, service::getBrowser);
        Mockito.verify(playwright).close();
    }

    /**
     * Verifies that {@link PlaywrightBrowser#close()} stops the driver only once when called twice, and that
     * {@link PlaywrightBrowser#isClosed()} reports {@code true} even though the browser still reports itself as
     * connected, as Playwright's does after its driver is closed.
     */
    @Test(description = "Browser close stops the driver once and reports closed even though the browser still looks connected")
    public void browserCloseStopsTheDriverOnceAndReportsClosedEvenThoughTheBrowserStillLooksConnected() {
        Playwright playwright = Mockito.mock(Playwright.class);
        Browser rawBrowser = runningBrowser();
        IBrowser browser = new PlaywrightBrowser(playwright, rawBrowser, properties());

        browser.close();
        browser.close();

        Mockito.verify(playwright, Mockito.times(1)).close();
        Assert.assertTrue(rawBrowser.isConnected());
        Assert.assertTrue(browser.isClosed());
    }

    /**
     * Verifies that {@link BrowserService#isTracingEnabled()} reflects the {@code tracing} property.
     */
    @Test(description = "isTracingEnabled reflects the tracing property")
    public void isTracingEnabledReflectsTheTracingProperty() {
        IBrowserFactory factory = factoryLaunching(runningBrowser());

        BrowserService<IBrowserProperties> enabled = new BrowserService<>(
                new BrowserProperties().setTracing(true), factory, () -> Mockito.mock(Playwright.class));
        BrowserService<IBrowserProperties> disabled = new BrowserService<>(
                new BrowserProperties().setTracing(false), factory, () -> Mockito.mock(Playwright.class));

        Assert.assertTrue(enabled.isTracingEnabled());
        Assert.assertFalse(disabled.isTracingEnabled());
    }

    /**
     * Verifies that {@link BrowserService#startTracing()} always starts tracing on the current window, even when
     * the {@code tracing} property is off, with the configured screenshot and snapshot options.
     */
    @Test(description = "startTracing starts tracing with the configured options, whatever the tracing property")
    public void startTracingStartsTracingWithTheConfiguredOptionsWhateverTheTracingProperty() {
        IBrowserProperties properties = properties().setTracing(false)
                .setTracingScreenshotsEnabled(true)
                .setTracingSnapshotsEnabled(false);
        BrowserService<IBrowserProperties> service = new BrowserService<>(
                properties,
                factoryLaunching(runningBrowser()),
                () -> Mockito.mock(Playwright.class));
        Tracing tracing = tracingOfCurrentWindow(service);

        service.startTracing();

        ArgumentCaptor<Tracing.StartOptions> captor = ArgumentCaptor.forClass(Tracing.StartOptions.class);
        Mockito.verify(tracing).start(captor.capture());
        Assert.assertEquals(captor.getValue().screenshots, Boolean.TRUE);
        Assert.assertEquals(captor.getValue().snapshots, Boolean.FALSE);
    }

    /**
     * Verifies that {@link BrowserService#stopAndSaveTrace(String)} stops tracing into a file in the configured
     * folder and returns that file's content.
     */
    @Test(description = "stopAndSaveTrace saves the trace into the tracing folder and returns it")
    public void stopAndSaveTraceSavesTheTraceIntoTheTracingFolderAndReturnsIt() throws IOException {
        Path folder = Files.createTempDirectory("traces-");
        try {
            IBrowserProperties properties = properties().setTracingSaveFolder(folder.toString());
            BrowserService<IBrowserProperties> service =
                    new BrowserService<>(properties, factoryLaunching(runningBrowser()), () -> Mockito.mock(Playwright.class));
            Tracing tracing = tracingOfCurrentWindow(service);
            Mockito.doAnswer(invocation -> {
                Tracing.StopOptions options = invocation.getArgument(0);
                Files.write(options.path, new byte[]{1, 2, 3});
                return null;
            }).when(tracing).stop(ArgumentMatchers.any(Tracing.StopOptions.class));

            byte[] trace = service.stopAndSaveTrace("trace.zip");

            Assert.assertEquals(trace, new byte[]{1, 2, 3});
            Assert.assertTrue(Files.exists(folder.resolve("trace.zip")));
        } finally {
            Files.deleteIfExists(folder.resolve("trace.zip"));
            Files.deleteIfExists(folder);
        }
    }

    /**
     * Creates a browser service with test properties, the given factory, and the given source of Playwright drivers.
     */
    private static BrowserService<IBrowserProperties> service(
            IBrowserFactory factory, Supplier<Playwright> playwrightSupplier) {
        return new BrowserService<>(properties(), factory, playwrightSupplier);
    }

    /**
     * Minimal properties needed to open windows: viewport size and timeouts.
     */
    private static BrowserProperties properties() {
        return new BrowserProperties()
                .setWidth(800)
                .setHeight(600)
                .setTimeout(1000L)
                .setPageLoadTimeout(1000L);
    }

    /**
     * A mocked factory that launches the given browsers, one per call.
     */
    private static IBrowserFactory factoryLaunching(Browser first, Browser... next) {
        IBrowserFactory factory = Mockito.mock(IBrowserFactory.class);
        Mockito.when(factory.createBrowser(ArgumentMatchers.any())).thenReturn(first, next);
        return factory;
    }

    /**
     * A mocked, connected browser whose new contexts can open pages and are reported by
     * {@link Browser#contexts()}, like a real browser's.
     */
    private static Browser runningBrowser() {
        Browser browser = Mockito.mock(Browser.class);
        List<BrowserContext> contexts = new ArrayList<>();
        Mockito.when(browser.isConnected()).thenReturn(true);
        Mockito.when(browser.contexts()).thenAnswer(invocation -> new ArrayList<>(contexts));
        Mockito.when(browser.newContext(ArgumentMatchers.any(Browser.NewContextOptions.class))).thenAnswer(invocation -> {
            BrowserContext context = newContext();
            contexts.add(context);
            return context;
        });
        return browser;
    }

    /**
     * Gives the current window of the service's browser a mocked {@link Tracing} and returns it.
     */
    private static Tracing tracingOfCurrentWindow(BrowserService<IBrowserProperties> service) {
        Tracing tracing = Mockito.mock(Tracing.class);
        BrowserContext context = service.getBrowser().requireCurrentBrowserWindow().getBrowserContext();
        Mockito.when(context.tracing()).thenReturn(tracing);
        return tracing;
    }

    /**
     * A mocked context (window) whose new pages (tabs) are mocks too.
     */
    private static BrowserContext newContext() {
        BrowserContext context = Mockito.mock(BrowserContext.class);
        Mockito.when(context.newPage()).thenAnswer(invocation -> Mockito.mock(Page.class));
        return context;
    }
}
