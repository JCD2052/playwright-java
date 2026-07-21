package org.jcd2052.core.browser.launcher;

import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for {@link BrowserLauncherRegistry}: the default browser registrations, name
 * lookup (including the unknown/invalid-name case), and custom launcher registration.
 */
public class BrowserLauncherRegistryTest {
    @Test
    public void defaultConstructorRegistersTheFourCorePlaywrightBrowsers() {
        BrowserLauncherRegistry registry = new BrowserLauncherRegistry();

        assertTrue(registry.getLauncher("chrome") instanceof ChromeLauncher);
        assertTrue(registry.getLauncher("firefox") instanceof FirefoxLauncher);
        assertTrue(registry.getLauncher("edge") instanceof EdgeLauncher);
        assertTrue(registry.getLauncher("webkit") instanceof WebkitLauncher);
    }

    @Test
    public void getLauncherIsCaseInsensitive() {
        BrowserLauncherRegistry registry = new BrowserLauncherRegistry();

        IBrowserLauncher lowercase = registry.getLauncher("chrome");
        assertNotNull(lowercase);
        assertSame(registry.getLauncher("Chrome"), lowercase);
        assertSame(registry.getLauncher("CHROME"), lowercase);
    }

    @Test
    public void getLauncherReturnsNullForAnUnknownBrowserName() {
        BrowserLauncherRegistry registry = new BrowserLauncherRegistry();

        assertNull(registry.getLauncher("netscape-navigator"));
    }

    @Test
    public void getLauncherReturnsNullForANullName() {
        BrowserLauncherRegistry registry = new BrowserLauncherRegistry();

        assertNull(registry.getLauncher(null));
    }

    @Test
    public void registerAddsACustomLauncherRetrievableByItsOwnNameCaseInsensitively() {
        BrowserLauncherRegistry registry = new BrowserLauncherRegistry();
        IBrowserLauncher customLauncher = mock(IBrowserLauncher.class);
        when(customLauncher.getName()).thenReturn("opera");

        registry.register(customLauncher);

        assertSame(registry.getLauncher("opera"), customLauncher);
        assertSame(registry.getLauncher("OPERA"), customLauncher);
    }

    @Test
    public void registerOverwritesAnExistingLauncherWithTheSameName() {
        BrowserLauncherRegistry registry = new BrowserLauncherRegistry();
        IBrowserLauncher replacement = mock(IBrowserLauncher.class);
        when(replacement.getName()).thenReturn("chrome");

        registry.register(replacement);

        assertSame(registry.getLauncher("chrome"), replacement);
    }
}
