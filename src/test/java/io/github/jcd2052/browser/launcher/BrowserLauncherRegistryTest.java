package io.github.jcd2052.browser.launcher;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link BrowserLauncherRegistry}: the default browser registrations, name
 * lookup (including the unknown/invalid-name case), and custom launcher registration.
 */
public class BrowserLauncherRegistryTest {
    /**
     * Verifies that a new registry has launchers for every built-in browser name: chrome, chromium, firefox, edge
     * and webkit.
     */
    @Test(description = "The default constructor registers the core Playwright browsers")
    public void defaultConstructorRegistersTheCorePlaywrightBrowsers() {
        BrowserLauncherRegistry registry = new BrowserLauncherRegistry();

        Assert.assertTrue(registry.getLauncher("chrome") instanceof ChromeLauncher);
        Assert.assertTrue(registry.getLauncher("chromium") instanceof ChromiumLauncher);
        Assert.assertTrue(registry.getLauncher("firefox") instanceof FirefoxLauncher);
        Assert.assertTrue(registry.getLauncher("edge") instanceof EdgeLauncher);
        Assert.assertTrue(registry.getLauncher("webkit") instanceof WebkitLauncher);
    }

    /**
     * Verifies that browser names are looked up regardless of case.
     */
    @Test(description = "getLauncher is case-insensitive")
    public void getLauncherIsCaseInsensitive() {
        BrowserLauncherRegistry registry = new BrowserLauncherRegistry();

        IBrowserLauncher lowercase = registry.getLauncher("chrome");
        Assert.assertNotNull(lowercase);
        Assert.assertSame(registry.getLauncher("Chrome"), lowercase);
        Assert.assertSame(registry.getLauncher("CHROME"), lowercase);
    }

    /**
     * Verifies that an unknown browser name finds no launcher.
     */
    @Test(description = "getLauncher returns null for an unknown browser name")
    public void getLauncherReturnsNullForAnUnknownBrowserName() {
        BrowserLauncherRegistry registry = new BrowserLauncherRegistry();

        Assert.assertNull(registry.getLauncher("netscape-navigator"));
    }

    /**
     * Verifies that a {@code null} browser name finds no launcher instead of failing.
     */
    @Test(description = "getLauncher returns null for a null name")
    public void getLauncherReturnsNullForANullName() {
        BrowserLauncherRegistry registry = new BrowserLauncherRegistry();

        Assert.assertNull(registry.getLauncher(null));
    }

    /**
     * Verifies that a custom launcher is registered under the name it reports, and can be found regardless of case.
     */
    @Test(description = "register adds a custom launcher retrievable by its own name, case-insensitively")
    public void registerAddsACustomLauncherRetrievableByItsOwnNameCaseInsensitively() {
        BrowserLauncherRegistry registry = new BrowserLauncherRegistry();
        IBrowserLauncher customLauncher = Mockito.mock(IBrowserLauncher.class);
        Mockito.when(customLauncher.getName()).thenReturn("opera");

        registry.register(customLauncher);

        Assert.assertSame(registry.getLauncher("opera"), customLauncher);
        Assert.assertSame(registry.getLauncher("OPERA"), customLauncher);
    }

    /**
     * Verifies that registering a launcher under an existing name replaces the built-in launcher.
     */
    @Test(description = "register overwrites an existing launcher with the same name")
    public void registerOverwritesAnExistingLauncherWithTheSameName() {
        BrowserLauncherRegistry registry = new BrowserLauncherRegistry();
        IBrowserLauncher replacement = Mockito.mock(IBrowserLauncher.class);
        Mockito.when(replacement.getName()).thenReturn("chrome");

        registry.register(replacement);

        Assert.assertSame(registry.getLauncher("chrome"), replacement);
    }
}
