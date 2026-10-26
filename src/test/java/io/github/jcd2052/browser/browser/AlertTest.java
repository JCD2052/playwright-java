package io.github.jcd2052.browser.browser;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.TimeoutError;
import io.github.jcd2052.browser.browser.interfaces.AlertType;
import io.github.jcd2052.browser.browser.interfaces.IAlert;
import io.github.jcd2052.browser.browser.interfaces.IBrowserTab;
import io.github.jcd2052.browser.configuration.BrowserProperties;
import io.github.jcd2052.browser.factory.BrowserFactory;
import io.github.jcd2052.browser.launcher.BrowserLauncherRegistry;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Verifies {@link IAlert} and {@link IBrowserTab#handleNextAlert} against a real headless Chromium, on a page
 * whose buttons open an {@code alert}, a {@code confirm} and a {@code prompt} and write what the page received
 * into {@code #result}.
 */
public class AlertTest {
    /**
     * The test page: one button per dialog kind, plus one that opens an alert a moment after it is clicked and
     * one that opens no dialog at all.
     */
    private static final String DIALOG_PAGE = "<button id='alert' onclick=\"alert('Saved!')\">alert</button>"
            + "<button id='confirm' onclick=\"result.textContent = confirm('Delete it?') ? 'confirmed' : 'cancelled'\">"
            + "confirm</button>"
            + "<button id='prompt' onclick=\"result.textContent = String(prompt('Your name?', 'guest'))\">prompt</button>"
            + "<button id='later' onclick=\"setTimeout(() => alert('Later!'), 300)\">later</button>"
            + "<button id='nothing'>nothing</button>"
            + "<p id='result'></p>";

    private PlaywrightBrowser browser;
    private IBrowserTab tab;
    private Page page;

    /**
     * Launches a fresh headless Chromium for each test, opens a window, and loads the dialog test page.
     */
    @BeforeMethod
    public void openDialogPage() {
        BrowserProperties properties = new BrowserProperties()
                .setName("chromium")
                .setHeadless(true)
                .setTimeout(5000L)
                .setPageLoadTimeout(10000L)
                .setWidth(800)
                .setHeight(600);
        Playwright playwright = Playwright.create();
        browser = new PlaywrightBrowser(
                playwright,
                new BrowserFactory<>(properties, new BrowserLauncherRegistry()).createBrowser(playwright),
                properties);
        tab = browser.openNewWindow().getCurrentBrowserTab();
        page = tab.getPage();
        page.setContent(DIALOG_PAGE);
    }

    /**
     * Closes the whole browser and its Playwright driver after each test.
     */
    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        browser.close();
    }

    /**
     * Verifies that the handled alert is returned, with its message and type readable after it was closed.
     */
    @Test(description = "handleNextAlert returns the handled alert with its message and type")
    public void handleNextAlertReturnsTheHandledAlertWithItsMessageAndType() {
        IAlert alert = tab.handleNextAlert(IAlert::accept, () -> page.click("#alert"));

        Assert.assertEquals(alert.getMessage(), "Saved!");
        Assert.assertEquals(alert.getType(), AlertType.ALERT);
        Assert.assertEquals(alert.getDefaultValue(), "");
    }

    /**
     * Verifies that accepting a {@code confirm} makes it return {@code true} to the page.
     */
    @Test(description = "Accepting a confirm confirms it")
    public void acceptingAConfirmConfirmsIt() {
        IAlert alert = tab.handleNextAlert(IAlert::accept, () -> page.click("#confirm"));

        Assert.assertEquals(alert.getType(), AlertType.CONFIRM);
        Assert.assertEquals(alert.getMessage(), "Delete it?");
        Assert.assertEquals(page.textContent("#result"), "confirmed");
    }

    /**
     * Verifies that declining a {@code confirm} makes it return {@code false} to the page.
     */
    @Test(description = "Declining a confirm cancels it")
    public void decliningAConfirmCancelsIt() {
        tab.handleNextAlert(IAlert::decline, () -> page.click("#confirm"));

        Assert.assertEquals(page.textContent("#result"), "cancelled");
    }

    /**
     * Verifies that a {@code prompt} reports its type, message and default value.
     */
    @Test(description = "A prompt exposes its message, type and default value")
    public void aPromptExposesItsMessageTypeAndDefaultValue() {
        IAlert alert = tab.handleNextAlert(IAlert::decline, () -> page.click("#prompt"));

        Assert.assertEquals(alert.getType(), AlertType.PROMPT);
        Assert.assertEquals(alert.getMessage(), "Your name?");
        Assert.assertEquals(alert.getDefaultValue(), "guest");
    }

    /**
     * Verifies that filling a {@code prompt} answers it with the given text.
     */
    @Test(description = "fillText answers a prompt with the given text")
    public void fillTextAnswersAPromptWithTheGivenText() {
        tab.handleNextAlert(alert -> alert.fillText("Ada"), () -> page.click("#prompt"));

        Assert.assertEquals(page.textContent("#result"), "Ada");
    }

    /**
     * Verifies what a {@code prompt} returns to the page when it is accepted without text.
     */
    @Test(description = "Accepting a prompt without text answers it with an empty string")
    public void acceptingAPromptWithoutTextAnswersItWithAnEmptyString() {
        tab.handleNextAlert(IAlert::accept, () -> page.click("#prompt"));

        Assert.assertEquals(page.textContent("#result"), "");
    }

    /**
     * Verifies that declining a {@code prompt} makes it return {@code null} to the page.
     */
    @Test(description = "Declining a prompt answers it with null")
    public void decliningAPromptAnswersItWithNull() {
        tab.handleNextAlert(IAlert::decline, () -> page.click("#prompt"));

        Assert.assertEquals(page.textContent("#result"), "null");
    }

    /**
     * Verifies that a dialog opened a moment after the action returns is still waited for and handled.
     */
    @Test(description = "handleNextAlert waits for a dialog that opens after the action")
    public void handleNextAlertWaitsForADialogThatOpensAfterTheAction() {
        IAlert alert = tab.handleNextAlert(IAlert::accept, () -> page.click("#later"));

        Assert.assertEquals(alert.getMessage(), "Later!");
    }

    /**
     * Verifies that an action that opens no dialog fails with a timeout, and that the handler is removed, so the
     * next dialog is handled by the next call instead of the stale handler.
     */
    @Test(description = "handleNextAlert times out when no dialog opens and leaves no stale handler")
    public void handleNextAlertTimesOutWhenNoDialogOpensAndLeavesNoStaleHandler() {
        page.context().setDefaultTimeout(500);

        Assert.expectThrows(TimeoutError.class,
                () -> tab.handleNextAlert(alert -> alert.fillText("stale"), () -> page.click("#nothing")));

        page.context().setDefaultTimeout(5000);
        tab.handleNextAlert(alert -> alert.fillText("fresh"), () -> page.click("#prompt"));
        Assert.assertEquals(page.textContent("#result"), "fresh");
    }
}
