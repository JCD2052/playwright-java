package org.jcd2052.core.browser.factory;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Selectors;
import org.jcd2052.core.browser.configuration.IBrowserProperties;
import org.jcd2052.core.browser.launcher.IBrowserLauncher;
import org.jcd2052.core.browser.launcher.IBrowserLauncherRegistry;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

/**
 * Unit tests for {@link BrowserFactory}.
 * <p>
 * {@link Playwright}, {@link BrowserType}, {@link Browser}, and {@link Selectors} are all plain
 * interfaces in the Playwright Java client, so the whole launch pipeline can be exercised with
 * Mockito, with no real browser or driver process involved. {@link BrowserType.LaunchOptions} is
 * a plain options object with public fields, so the translated options can be inspected directly
 * via an {@link ArgumentCaptor} rather than mocked.
 */
public class BrowserFactoryTest {
    @Test
    public void createBrowserTranslatesPropertiesIntoLaunchOptionsAndDelegatesToTheResolvedLauncher() {
        Playwright playwright = mock(Playwright.class);
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserLauncherRegistry registry = mock(IBrowserLauncherRegistry.class);
        IBrowserLauncher launcher = mock(IBrowserLauncher.class);
        Browser launchedBrowser = mock(Browser.class);

        when(properties.isHeadless()).thenReturn(true);
        when(properties.getTimeout()).thenReturn(30000L);
        when(properties.getArgs()).thenReturn(List.of("--no-sandbox", "--disable-gpu"));
        when(properties.getName()).thenReturn("chrome");
        when(registry.getLauncher("chrome")).thenReturn(launcher);
        when(launcher.launch(eq(playwright), any(BrowserType.LaunchOptions.class))).thenReturn(launchedBrowser);

        Browser result = new BrowserFactory(properties, registry).createBrowser(playwright);

        assertSame(result, launchedBrowser);

        ArgumentCaptor<BrowserType.LaunchOptions> captor = ArgumentCaptor.forClass(BrowserType.LaunchOptions.class);
        verify(launcher).launch(eq(playwright), captor.capture());
        BrowserType.LaunchOptions usedOptions = captor.getValue();
        assertEquals(usedOptions.headless, Boolean.TRUE);
        assertEquals(usedOptions.timeout, 30000.0);
        assertEquals(usedOptions.args, List.of("--no-sandbox", "--disable-gpu"));
    }

    @Test
    public void createBrowserThrowsForAnUnsupportedBrowserName() {
        Playwright playwright = mock(Playwright.class);
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserLauncherRegistry registry = mock(IBrowserLauncherRegistry.class);

        when(properties.isHeadless()).thenReturn(false);
        when(properties.getTimeout()).thenReturn(30000L);
        when(properties.getName()).thenReturn("netscape-navigator");
        when(registry.getLauncher("netscape-navigator")).thenReturn(null);

        BrowserFactory factory = new BrowserFactory(properties, registry);

        IllegalArgumentException exception = expectThrows(IllegalArgumentException.class,
                () -> factory.createBrowser(playwright));
        assertTrue(exception.getMessage().contains("netscape-navigator"));
    }

    @Test
    public void createBrowserAppliesTheCustomTestIdAttributeWhenConfigured() {
        Playwright playwright = mock(Playwright.class);
        Selectors selectors = mock(Selectors.class);
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserLauncherRegistry registry = mock(IBrowserLauncherRegistry.class);
        IBrowserLauncher launcher = mock(IBrowserLauncher.class);

        when(properties.isHeadless()).thenReturn(true);
        when(properties.getTimeout()).thenReturn(30000L);
        when(properties.getTestIdAttribute()).thenReturn("data-test");
        when(properties.getName()).thenReturn("chrome");
        when(playwright.selectors()).thenReturn(selectors);
        when(registry.getLauncher("chrome")).thenReturn(launcher);

        new BrowserFactory(properties, registry).createBrowser(playwright);

        verify(selectors).setTestIdAttribute("data-test");
    }

    @Test
    public void createBrowserSkipsTheTestIdAttributeWhenBlank() {
        Playwright playwright = mock(Playwright.class);
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserLauncherRegistry registry = mock(IBrowserLauncherRegistry.class);
        IBrowserLauncher launcher = mock(IBrowserLauncher.class);

        when(properties.isHeadless()).thenReturn(true);
        when(properties.getTimeout()).thenReturn(30000L);
        when(properties.getTestIdAttribute()).thenReturn("   ");
        when(properties.getName()).thenReturn("chrome");
        when(registry.getLauncher("chrome")).thenReturn(launcher);

        new BrowserFactory(properties, registry).createBrowser(playwright);

        verify(playwright, never()).selectors();
    }

    @Test
    public void createBrowserOmitsArgsWhenNoneAreConfigured() {
        Playwright playwright = mock(Playwright.class);
        IBrowserProperties properties = mock(IBrowserProperties.class);
        IBrowserLauncherRegistry registry = mock(IBrowserLauncherRegistry.class);
        IBrowserLauncher launcher = mock(IBrowserLauncher.class);

        when(properties.isHeadless()).thenReturn(false);
        when(properties.getTimeout()).thenReturn(15000L);
        when(properties.getArgs()).thenReturn(List.of());
        when(properties.getName()).thenReturn("firefox");
        when(registry.getLauncher("firefox")).thenReturn(launcher);

        new BrowserFactory(properties, registry).createBrowser(playwright);

        ArgumentCaptor<BrowserType.LaunchOptions> captor = ArgumentCaptor.forClass(BrowserType.LaunchOptions.class);
        verify(launcher).launch(eq(playwright), captor.capture());
        assertEquals(captor.getValue().headless, Boolean.FALSE);
        assertNull(captor.getValue().args);
    }
}
