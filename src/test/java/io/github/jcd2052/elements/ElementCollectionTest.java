package io.github.jcd2052.elements;

import io.github.jcd2052.browser.configuration.BrowserProperties;
import io.github.jcd2052.browser.configuration.IBrowserProperties;
import io.github.jcd2052.browser.factory.BrowserFactory;
import io.github.jcd2052.browser.launcher.BrowserLauncherRegistry;
import io.github.jcd2052.browser.services.BrowserService;
import io.github.jcd2052.browser.services.ElementFactory;
import io.github.jcd2052.browser.services.ElementFinderService;
import io.github.jcd2052.browser.services.LocalizationService;
import io.github.jcd2052.elements.interfaces.ILabelElement;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.exceptions.ElementTimeoutException;
import io.github.jcd2052.logger.i18n.LocalizationReader;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

/**
 * Verifies {@link ElementCollection}'s {@link ExpectedCount} waits against a real headless Chrome,
 * using the framework's real service wiring.
 */
public class ElementCollectionTest {
    private static final Selector ITEMS = Selector.bySelector("li.item");

    private BrowserService<IBrowserProperties> browserService;
    private ElementFactory elementFactory;

    @BeforeMethod
    public void startBrowser() {
        IBrowserProperties properties = new BrowserProperties()
                .setName("chrome")
                .setHeadless(true)
                .setTimeout(30000L)
                .setPageLoadTimeout(5000L)
                .setWidth(800)
                .setHeight(600);
        browserService = new BrowserService<>(
                properties, new BrowserFactory<>(properties, new BrowserLauncherRegistry()));
        elementFactory = new ElementFactory(
                new ElementFinderService(browserService),
                properties,
                new LocalizationService(new LocalizationReader(), "en", null));
        browserService.start();
        // Keep the launch timeout generous, but make negative waits fail fast.
        browserService.getBrowser().requireCurrentBrowserWindow().getBrowserContext().setDefaultTimeout(1000);
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        browserService.close();
    }

    @Test
    public void zeroWaitsUntilMatchingElementsDisappear() {
        setContent("<ul><li class='item'>a</li><li class='item'>b</li></ul>"
                + "<script>setTimeout(() => document.querySelectorAll('li.item').forEach(e => e.remove()), 300)</script>");

        assertTrue(collection(ExpectedCount.ZERO).getElements().isEmpty());
    }

    @Test
    public void zeroThrowsAFrameworkTimeoutWhenElementsRemain() {
        setContent("<ul><li class='item'>a</li></ul>");

        ElementTimeoutException exception = expectThrows(ElementTimeoutException.class,
                () -> collection(ExpectedCount.ZERO).getElements());

        assertTrue(exception.getMessage().contains("Items"));
        assertTrue(exception.getMessage().contains("li.item"));
    }

    @Test
    public void moreThanZeroThrowsAFrameworkTimeoutWhenNothingMatches() {
        setContent("<ul></ul>");

        expectThrows(ElementTimeoutException.class, () -> collection(ExpectedCount.MORE_THAN_ZERO).getElements());
    }

    @Test
    public void moreThanZeroReturnsEveryMatchingElement() {
        setContent("<ul><li class='item'>a</li><li class='item'>b</li></ul>");

        assertEquals(collection(ExpectedCount.MORE_THAN_ZERO).getElements().size(), 2);
    }

    private ElementCollection<ILabelElement> collection(ExpectedCount expectedCount) {
        return (ElementCollection<ILabelElement>) elementFactory.createElementsCollection(
                ITEMS, "Items", ILabelElement.class, expectedCount);
    }

    private void setContent(String html) {
        browserService.getBrowser().requireCurrentBrowserTab().getPage().setContent(html);
    }
}
