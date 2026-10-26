package io.github.jcd2052.demo.tests;

import io.github.jcd2052.browser.browser.interfaces.IBrowserTab;
import io.github.jcd2052.browser.configuration.IBrowserProperties;
import io.github.jcd2052.browser.factory.BrowserFactory;
import io.github.jcd2052.browser.launcher.BrowserLauncherRegistry;
import io.github.jcd2052.browser.services.BrowserService;
import io.github.jcd2052.browser.services.ElementFactory;
import io.github.jcd2052.browser.services.ElementFinderService;
import io.github.jcd2052.browser.services.LocalizationService;
import io.github.jcd2052.browser.services.interfaces.IBrowserService;
import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.demo.pages.DemoLoginPage;
import io.github.jcd2052.demo.support.DemoServer;
import io.github.jcd2052.logger.i18n.LocalizationReader;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Runs the demo without Spring: the framework is wired by hand from the browser settings a subclass provides,
 * so each subclass shows one way to supply them (see {@link JsonPropertiesDemoTests} and
 * {@link HardcodedPropertiesDemoTests}).
 * <p>
 * The tests drive the bundled sign-in page and check that the provided settings reached the browser.
 * </p>
 */
public abstract class WithoutSpringDemoTests {
    private IBrowserProperties browserProperties;
    private IBrowserService<IBrowserProperties> browserService;
    private DemoLoginPage loginPage;
    private DemoServer demoServer;

    /**
     * Provides the browser settings the framework is wired with.
     *
     * @return The browser settings.
     */
    protected abstract IBrowserProperties createBrowserProperties();

    /**
     * Wires the framework by hand from the subclass's settings, creates the page object, and starts the
     * demo server.
     */
    @BeforeClass(alwaysRun = true)
    public void wireFrameworkAndStartDemoServer() {
        browserProperties = createBrowserProperties();
        browserService = new BrowserService<>(
                browserProperties,
                new BrowserFactory<>(browserProperties, new BrowserLauncherRegistry()));
        IElementFactory elementFactory = new ElementFactory(
                new ElementFinderService(browserService),
                browserProperties,
                new LocalizationService(new LocalizationReader(), "en", null));
        loginPage = new DemoLoginPage(elementFactory);
        try {
            demoServer = new DemoServer();
            demoServer.start();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to start the bundled demo server", e);
        }
    }

    /**
     * Stops the demo server.
     */
    @AfterClass(alwaysRun = true)
    public void stopDemoServer() {
        if (demoServer != null) {
            demoServer.stop();
        }
    }

    /**
     * Opens the thread's browser and loads the sign-in page.
     */
    @BeforeMethod(alwaysRun = true)
    public void openLoginPage() {
        browserService.getBrowser().navigateTo(demoServer.getBaseUrl() + "/index.html");
    }

    /**
     * Closes the thread's browser.
     */
    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        browserService.close();
    }

    /**
     * Verifies that the hand-wired framework drives a page object: the sign-in form is filled and submitted.
     */
    @Test(description = "The login form fills and submits without Spring")
    public void loginFormFillsAndSubmitsWithoutSpring() {
        String status = loginPage.signIn("qa-engineer", "hunter2", false, "ua");

        Assert.assertEquals(status, "Welcome, qa-engineer! [ua]");
    }

    /**
     * Verifies that the provided settings reached the browser: the page sees the configured viewport size,
     * locale and time zone. The time zone is compared by the browser's own name for it, since browsers may
     * report an older alias (e.g. {@code Europe/Kiev} for {@code Europe/Kyiv}).
     */
    @Test(description = "The provided settings reach the browser")
    public void theProvidedSettingsReachTheBrowser() {
        IBrowserTab tab = browserService.getBrowser().requireCurrentBrowserTab();

        Assert.assertEquals(tab.evaluate("window.innerWidth"), browserProperties.getWidth());
        Assert.assertEquals(tab.evaluate("window.innerHeight"), browserProperties.getHeight());
        Assert.assertEquals(tab.evaluate("navigator.language"), browserProperties.getLocale());
        Assert.assertEquals(tab.evaluate("Intl.DateTimeFormat().resolvedOptions().timeZone"),
                tab.evaluate(canonicalTimeZoneScript(browserProperties.getTimezoneId())));
    }

    /**
     * Builds a script that returns the browser's own name for the given time zone.
     */
    private static String canonicalTimeZoneScript(String timeZoneId) {
        return "Intl.DateTimeFormat('en', {timeZone: '" + timeZoneId + "'}).resolvedOptions().timeZone";
    }
}
