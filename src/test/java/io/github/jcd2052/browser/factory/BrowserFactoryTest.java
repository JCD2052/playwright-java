package io.github.jcd2052.browser.factory;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Selectors;
import io.github.jcd2052.browser.configuration.IBrowserProperties;
import io.github.jcd2052.browser.launcher.IBrowserLauncher;
import io.github.jcd2052.browser.launcher.IBrowserLauncherRegistry;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

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
    /**
     * Verifies that headless mode, timeout and launch arguments are translated into Playwright's launch options,
     * and that the browser is launched by the launcher registered for the configured browser name.
     */
    @Test(description = "createBrowser translates properties into launch options and delegates to the resolved launcher")
    public void createBrowserTranslatesPropertiesIntoLaunchOptionsAndDelegatesToTheResolvedLauncher() {
        Playwright playwright = Mockito.mock(Playwright.class);
        IBrowserProperties properties = Mockito.mock(IBrowserProperties.class);
        IBrowserLauncherRegistry registry = Mockito.mock(IBrowserLauncherRegistry.class);
        IBrowserLauncher launcher = Mockito.mock(IBrowserLauncher.class);
        Browser launchedBrowser = Mockito.mock(Browser.class);

        Mockito.when(properties.isHeadless()).thenReturn(true);
        Mockito.when(properties.getTimeout()).thenReturn(30000L);
        Mockito.when(properties.getArgs()).thenReturn(List.of("--no-sandbox", "--disable-gpu"));
        Mockito.when(properties.getName()).thenReturn("chrome");
        Mockito.when(registry.getLauncher("chrome")).thenReturn(launcher);
        Mockito.when(launcher.launch(
                ArgumentMatchers.eq(playwright),
                ArgumentMatchers.any(BrowserType.LaunchOptions.class))).thenReturn(launchedBrowser);

        Browser result = new BrowserFactory<>(properties, registry).createBrowser(playwright);

        Assert.assertSame(result, launchedBrowser);

        ArgumentCaptor<BrowserType.LaunchOptions> captor = ArgumentCaptor.forClass(BrowserType.LaunchOptions.class);
        Mockito.verify(launcher).launch(ArgumentMatchers.eq(playwright), captor.capture());
        BrowserType.LaunchOptions usedOptions = captor.getValue();
        Assert.assertEquals(usedOptions.headless, Boolean.TRUE);
        Assert.assertEquals(usedOptions.timeout, 30000.0);
        Assert.assertEquals(usedOptions.args, List.of("--no-sandbox", "--disable-gpu"));
    }

    /**
     * Verifies that a browser name with no registered launcher fails with an {@link IllegalArgumentException}
     * that names the unsupported browser.
     */
    @Test(description = "createBrowser throws for an unsupported browser name")
    public void createBrowserThrowsForAnUnsupportedBrowserName() {
        Playwright playwright = Mockito.mock(Playwright.class);
        IBrowserProperties properties = Mockito.mock(IBrowserProperties.class);
        IBrowserLauncherRegistry registry = Mockito.mock(IBrowserLauncherRegistry.class);

        Mockito.when(properties.isHeadless()).thenReturn(false);
        Mockito.when(properties.getTimeout()).thenReturn(30000L);
        Mockito.when(properties.getName()).thenReturn("netscape-navigator");
        Mockito.when(registry.getLauncher("netscape-navigator")).thenReturn(null);

        BrowserFactory<IBrowserProperties> factory = new BrowserFactory<>(properties, registry);

        IllegalArgumentException exception = Assert.expectThrows(IllegalArgumentException.class,
                () -> factory.createBrowser(playwright));
        Assert.assertTrue(exception.getMessage().contains("netscape-navigator"));
    }

    /**
     * Verifies that a configured custom test-id attribute is set on Playwright's selectors, so
     * {@code Selector.byTestId(...)} uses it.
     */
    @Test(description = "createBrowser applies the custom test-id attribute when configured")
    public void createBrowserAppliesTheCustomTestIdAttributeWhenConfigured() {
        Playwright playwright = Mockito.mock(Playwright.class);
        Selectors selectors = Mockito.mock(Selectors.class);
        IBrowserProperties properties = Mockito.mock(IBrowserProperties.class);
        IBrowserLauncherRegistry registry = Mockito.mock(IBrowserLauncherRegistry.class);
        IBrowserLauncher launcher = Mockito.mock(IBrowserLauncher.class);

        Mockito.when(properties.isHeadless()).thenReturn(true);
        Mockito.when(properties.getTimeout()).thenReturn(30000L);
        Mockito.when(properties.getTestIdAttribute()).thenReturn("data-test");
        Mockito.when(properties.getName()).thenReturn("chrome");
        Mockito.when(playwright.selectors()).thenReturn(selectors);
        Mockito.when(registry.getLauncher("chrome")).thenReturn(launcher);

        new BrowserFactory<>(properties, registry).createBrowser(playwright);

        Mockito.verify(selectors).setTestIdAttribute("data-test");
    }

    /**
     * Verifies that a blank test-id attribute leaves Playwright's selectors untouched, keeping the default
     * {@code data-testid}.
     */
    @Test(description = "createBrowser skips the test-id attribute when blank")
    public void createBrowserSkipsTheTestIdAttributeWhenBlank() {
        Playwright playwright = Mockito.mock(Playwright.class);
        IBrowserProperties properties = Mockito.mock(IBrowserProperties.class);
        IBrowserLauncherRegistry registry = Mockito.mock(IBrowserLauncherRegistry.class);
        IBrowserLauncher launcher = Mockito.mock(IBrowserLauncher.class);

        Mockito.when(properties.isHeadless()).thenReturn(true);
        Mockito.when(properties.getTimeout()).thenReturn(30000L);
        Mockito.when(properties.getTestIdAttribute()).thenReturn("   ");
        Mockito.when(properties.getName()).thenReturn("chrome");
        Mockito.when(registry.getLauncher("chrome")).thenReturn(launcher);

        new BrowserFactory<>(properties, registry).createBrowser(playwright);

        Mockito.verify(playwright, Mockito.never()).selectors();
    }

    /**
     * Verifies that an empty argument list is not passed to the launch options, so Playwright uses its defaults.
     */
    @Test(description = "createBrowser omits args when none are configured")
    public void createBrowserOmitsArgsWhenNoneAreConfigured() {
        Playwright playwright = Mockito.mock(Playwright.class);
        IBrowserProperties properties = Mockito.mock(IBrowserProperties.class);
        IBrowserLauncherRegistry registry = Mockito.mock(IBrowserLauncherRegistry.class);
        IBrowserLauncher launcher = Mockito.mock(IBrowserLauncher.class);

        Mockito.when(properties.isHeadless()).thenReturn(false);
        Mockito.when(properties.getTimeout()).thenReturn(15000L);
        Mockito.when(properties.getArgs()).thenReturn(List.of());
        Mockito.when(properties.getName()).thenReturn("firefox");
        Mockito.when(registry.getLauncher("firefox")).thenReturn(launcher);

        new BrowserFactory<>(properties, registry).createBrowser(playwright);

        ArgumentCaptor<BrowserType.LaunchOptions> captor = ArgumentCaptor.forClass(BrowserType.LaunchOptions.class);
        Mockito.verify(launcher).launch(ArgumentMatchers.eq(playwright), captor.capture());
        Assert.assertEquals(captor.getValue().headless, Boolean.FALSE);
        Assert.assertNull(captor.getValue().args);
    }
}
