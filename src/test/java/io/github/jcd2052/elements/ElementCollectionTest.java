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
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Verifies {@link ElementCollection}'s {@link ExpectedCount} waits against a real headless Chrome,
 * using the framework's real service wiring.
 */
public class ElementCollectionTest {
    /**
     * The selector every test collection is built from.
     */
    private static final Selector ITEMS = Selector.bySelector("li.item");

    private BrowserService<IBrowserProperties> browserService;
    private ElementFactory elementFactory;

    /**
     * Starts a headless Chrome through the real service wiring before each test. The launch timeout is kept
     * generous, but the page's default timeout is lowered to one second so the waits that are expected to time
     * out fail fast.
     */
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
        browserService.getBrowser();
        browserService.getBrowser().requireCurrentBrowserWindow().getBrowserContext().setDefaultTimeout(1000);
    }

    /**
     * Closes the browser after each test.
     */
    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        browserService.close();
    }

    /**
     * Verifies that {@link ExpectedCount#ZERO} waits until elements that are removed after a delay are gone.
     */
    @Test(description = "ZERO waits until matching elements disappear")
    public void zeroWaitsUntilMatchingElementsDisappear() {
        setContent("<ul><li class='item'>a</li><li class='item'>b</li></ul>"
                + "<script>setTimeout(() => document.querySelectorAll('li.item').forEach(e => e.remove()), 300)</script>");

        Assert.assertTrue(collection(ExpectedCount.ZERO).getElements().isEmpty());
    }

    /**
     * Verifies that {@link ExpectedCount#ZERO} fails with an {@link ElementTimeoutException} naming the collection and
     * its selector when elements are still there after the timeout.
     */
    @Test(description = "ZERO throws a framework timeout when elements remain")
    public void zeroThrowsAFrameworkTimeoutWhenElementsRemain() {
        setContent("<ul><li class='item'>a</li></ul>");

        ElementTimeoutException exception = Assert.expectThrows(ElementTimeoutException.class,
                () -> collection(ExpectedCount.ZERO).getElements());

        Assert.assertTrue(exception.getMessage().contains("Items"));
        Assert.assertTrue(exception.getMessage().contains("li.item"));
    }

    /**
     * Verifies that {@link ExpectedCount#MORE_THAN_ZERO} fails with an {@link ElementTimeoutException} when no element
     * appears.
     */
    @Test(description = "MORE_THAN_ZERO throws a framework timeout when nothing matches")
    public void moreThanZeroThrowsAFrameworkTimeoutWhenNothingMatches() {
        setContent("<ul></ul>");

        Assert.expectThrows(ElementTimeoutException.class, () -> collection(ExpectedCount.MORE_THAN_ZERO).getElements());
    }

    /**
     * Verifies that {@link ExpectedCount#MORE_THAN_ZERO} returns one element per match.
     */
    @Test(description = "MORE_THAN_ZERO returns every matching element")
    public void moreThanZeroReturnsEveryMatchingElement() {
        setContent("<ul><li class='item'>a</li><li class='item'>b</li></ul>");

        Assert.assertEquals(collection(ExpectedCount.MORE_THAN_ZERO).getElements().size(), 2);
    }

    /**
     * Creates a collection of the {@link #ITEMS} elements with the given count expectation.
     */
    private ElementCollection<ILabelElement> collection(ExpectedCount expectedCount) {
        return (ElementCollection<ILabelElement>) elementFactory.createElementsCollection(
                ITEMS, "Items", ILabelElement.class, expectedCount);
    }

    /**
     * Replaces the current tab's content with the given HTML.
     */
    private void setContent(String html) {
        browserService.getBrowser().requireCurrentBrowserTab().getPage().setContent(html);
    }
}
