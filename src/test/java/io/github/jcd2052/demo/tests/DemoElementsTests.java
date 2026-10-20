package io.github.jcd2052.demo.tests;

import com.microsoft.playwright.Page;
import io.github.jcd2052.demo.pages.DemoElementsPage;
import io.github.jcd2052.elements.ExpectedCount;
import io.github.jcd2052.elements.interfaces.IElement;
import io.github.jcd2052.elements.interfaces.ILabelElement;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.exceptions.ElementTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

/**
 * Exercises every built-in element type, and the common {@link IElement} API, against the bundled
 * {@code demo-site/elements.html} showcase page.
 */
public class DemoElementsTests extends DemoSiteBaseTests {
    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 'P', 'N', 'G'};

    @Autowired
    private DemoElementsPage page;

    @BeforeMethod(alwaysRun = true)
    public void openElementsPage() {
        openDemoPage("/elements.html");
        assertTrue(page.waitForLoading(), "Elements page did not load");
    }

    // ---- Buttons and the click family -------------------------------------------------------

    @Test
    public void clickFiresASingleClick() {
        page.getActionButton().click();
        assertTrue(page.getButtonLog().getText().startsWith("click@"));
    }

    @Test
    public void clickAtPositionClicksAtTheGivenOffsetInsideTheElement() {
        page.getActionButton().click(10, 5);
        assertEquals(page.getButtonLog().getText(), "click@10,5");
    }

    @Test
    public void doubleClickFiresADoubleClick() {
        page.getActionButton().doubleClick();
        assertEquals(page.getButtonLog().getText(), "double-click");
    }

    @Test
    public void rightClickOpensTheContextMenu() {
        page.getActionButton().rightClick();
        assertEquals(page.getButtonLog().getText(), "right-click");
    }

    @Test
    public void middleClickUsesTheMiddleMouseButton() {
        page.getActionButton().middleClick();
        assertEquals(page.getButtonLog().getText(), "middle-click");
    }

    @Test
    public void forceClickSkipsActionabilityChecksOnAnUnstableElement() {
        page.getUnstableButton().forceClick();
        assertEquals(page.getButtonLog().getText(), "unstable-click");
    }

    @Test
    public void isEnabledReflectsTheDisabledAttribute() {
        assertTrue(page.getActionButton().isEnabled());
        assertFalse(page.getDisabledButton().isEnabled());
    }

    @Test
    public void clickWithJsReachesAnElementThatIgnoresPointerEvents() {
        assertTrue(page.getNoPointerButton().getJsActions().isPointerEventsDisabled());
        assertFalse(page.getActionButton().getJsActions().isPointerEventsDisabled());

        page.getNoPointerButton().clickWithJs();

        assertEquals(page.getButtonLog().getText(), "js-click");
    }

    @Test
    public void actingOnAMissingElementThrowsAReadableFrameworkTimeout() {
        ElementTimeoutException exception = expectThrows(ElementTimeoutException.class,
                () -> page.getMissingButton().click());

        assertTrue(exception.getMessage().contains("Missing button"));
        assertTrue(exception.getMessage().contains("#does-not-exist"));
    }

    // ---- Labels, state and waits ------------------------------------------------------------

    @Test
    public void labelExposesItsNameTextAttributesAndVisibility() {
        ILabelElement greeting = page.getGreeting();

        assertEquals(greeting.getName(), "Greeting");
        assertEquals(greeting.getText(), "Hello, elements!");
        assertEquals(greeting.getAttribute("title"), "Greeting title");
        assertTrue(greeting.isVisible());
    }

    @Test
    public void setAttributeWithJsIsVisibleToGetAttribute() {
        page.getGreeting().getJsActions().setAttribute("data-state", "updated");
        assertEquals(page.getGreeting().getAttribute("data-state"), "updated");
    }

    @Test
    public void getAllTextsReturnsTheTextOfEveryMatchingElement() {
        assertEquals(page.getAllTagLabels().getAllTexts(), List.of("java", "playwright", "testng"));
    }

    @Test
    public void waitToBeVisibleWaitsForAnElementThatAppearsLater() {
        assertFalse(page.getHiddenLater().isVisible());

        page.getShowLaterButton().click();

        assertTrue(page.getHiddenLater().waitToBeVisible());
    }

    @Test
    public void waitToBeVisibleReturnsFalseWhenTheElementNeverAppears() {
        assertFalse(page.getHiddenLater().waitToBeVisible(300.0));
    }

    @Test
    public void waitToBeDetachedWaitsForAnElementToBeRemoved() {
        page.getRemoveLaterButton().click();

        assertTrue(page.getRemovable().waitToBeDetached());
        assertFalse(page.getRemovable().isVisible());
    }

    @Test
    public void hoverMovesThePointerOverTheElement() {
        page.getHoverTarget().hover();
        assertEquals(page.getHoverLog().getText(), "hovered");
    }

    @Test
    public void getScreenshotReturnsAPngImage() {
        byte[] screenshot = page.getGreeting().getScreenshot();

        assertTrue(screenshot.length > PNG_SIGNATURE.length);
        for (int i = 0; i < PNG_SIGNATURE.length; i++) {
            assertEquals(screenshot[i], PNG_SIGNATURE[i]);
        }
    }

    // ---- Text box ---------------------------------------------------------------------------

    @Test
    public void textBoxFillsReadsAndClearsItsValue() {
        page.getEchoInput().fillText("first");
        assertEquals(page.getEchoInput().getInputValue(), "first");

        page.getEchoInput().clearAndFillText("second");
        assertEquals(page.getEchoInput().getInputValue(), "second");

        page.getEchoInput().clearText();
        assertEquals(page.getEchoInput().getInputValue(), "");
    }

    @Test
    public void pressSendsAKeyToTheElement() {
        page.getEchoInput().fillText("hello");
        page.getEchoInput().press("Enter");

        assertEquals(page.getEchoLog().getText(), "submitted: hello");
    }

    @Test
    public void unfocusBlursTheElement() {
        page.getEchoInput().fillText("focused");
        page.getEchoInput().unfocus();

        assertEquals(page.getEchoLog().getText(), "blurred");
    }

    // ---- Dropdown ---------------------------------------------------------------------------

    @Test
    public void dropdownListsItsOptionsAndTheDefaultSelection() {
        assertEquals(page.getFruitDropdown().getTexts(), List.of("Apple", "Banana", "Cherry"));
        assertEquals(page.getFruitDropdown().getSelectedOption(), "Apple");
    }

    @Test
    public void dropdownSelectsByTextValueAndIndex() {
        page.getFruitDropdown().selectByText("Banana");
        assertEquals(page.getFruitDropdown().getSelectedOption(), "Banana");

        page.getFruitDropdown().selectByValue("cherry");
        assertEquals(page.getFruitDropdown().getSelectedOption(), "Cherry");

        page.getFruitDropdown().selectByIndex(0);
        assertEquals(page.getFruitDropdown().getSelectedOption(), "Apple");
    }

    // ---- Link -------------------------------------------------------------------------------

    @Test
    public void linkExposesItsHrefAndNavigatesWhenClicked() {
        assertEquals(page.getProductsLink().getHref(), "table.html");

        page.getProductsLink().click();

        Page currentPage = browserService.getBrowser().requireCurrentBrowserTab().getPage();
        currentPage.waitForURL("**/table.html");
        assertTrue(browserService.getBrowser().requireCurrentBrowserTab().getCurrentUrl().endsWith("/table.html"));
    }

    // ---- Checkbox and radio buttons ---------------------------------------------------------

    @Test
    public void checkboxChecksAndUnchecks() {
        assertFalse(page.getNewsletterCheckbox().isChecked());

        page.getNewsletterCheckbox().check();
        assertTrue(page.getNewsletterCheckbox().isChecked());

        page.getNewsletterCheckbox().uncheck();
        assertFalse(page.getNewsletterCheckbox().isChecked());
    }

    @Test
    public void clickingARadioButtonSelectsItAndDeselectsTheRestOfTheGroup() {
        assertTrue(page.getFreePlanRadio().isChecked());
        assertFalse(page.getProPlanRadio().isChecked());

        page.getProPlanRadio().click();

        assertTrue(page.getProPlanRadio().isChecked());
        assertFalse(page.getFreePlanRadio().isChecked());
    }

    // ---- Upload -----------------------------------------------------------------------------

    @Test
    public void uploadBoxAcceptsAPath() throws IOException {
        Path file = Files.createTempFile("avatar-path-", ".txt");
        try {
            page.getAvatarUpload().upload(file);
            assertEquals(page.getUploadLog().getText(), file.getFileName().toString());
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    public void uploadBoxAcceptsAFile() throws IOException {
        Path file = Files.createTempFile("avatar-file-", ".txt");
        try {
            page.getAvatarUpload().upload(file.toFile());
            assertEquals(page.getUploadLog().getText(), file.getFileName().toString());
        } finally {
            Files.deleteIfExists(file);
        }
    }

    // ---- Collections and child elements -----------------------------------------------------

    @Test
    public void elementCollectionWrapsEveryMatchAsItsOwnNamedElement() {
        List<ILabelElement> tags = page.getTags().getElements();

        assertEquals(tags.stream().map(IElement::getText).collect(Collectors.toList()),
                List.of("java", "playwright", "testng"));
        assertEquals(tags.get(0).getName(), "Tag 1");
        assertEquals(tags.get(2).getName(), "Tag 3");
    }

    @Test
    public void childElementsAreScopedToTheirParent() {
        List<ILabelElement> children = page.getTagList()
                .createChildElements(ILabelElement.class, Selector.bySelector("li"), "Child tag", ExpectedCount.MORE_THAN_ZERO)
                .getElements();

        assertEquals(children.size(), 3);
        assertEquals(children.get(1).getText(), "playwright");
    }

    @Test
    public void childElementDisambiguatesIdenticalButtonsInDifferentContainers() {
        page.pickCard(page.getBetaCard());
        assertEquals(page.getCardLog().getText(), "picked: Beta");
    }

    // ---- Drag and drop ----------------------------------------------------------------------

    @Test
    public void dragToDropsTheElementOnTheTarget() {
        page.getDragSource().dragTo(page.getDropTarget());
        assertEquals(page.getDropTarget().getText(), "Dropped");
    }

    @Test
    public void dragAndDropToMovesTheMouseInSteps() {
        page.getDragSource().dragAndDropTo(page.getDropTarget(), 10);
        assertEquals(page.getDropTarget().getText(), "Dropped");
    }

    // ---- Scrolling --------------------------------------------------------------------------

    @Test
    public void scrollToElementBringsAnOffscreenElementIntoView() {
        assertFalse(isInViewport(page.getFarAway()));

        page.getFarAway().scrollToElement();

        assertTrue(isInViewport(page.getFarAway()));
    }

    @Test
    public void scrollIntoViewWithJsBringsAnOffscreenElementIntoView() {
        assertFalse(isInViewport(page.getFarAway()));

        page.getFarAway().getJsActions().scrollIntoView();

        assertTrue(isInViewport(page.getFarAway()));
    }

    private static boolean isInViewport(IElement element) {
        return (Boolean) element.getLocator().evaluate(
                "el => { const r = el.getBoundingClientRect(); return r.top >= 0 && r.bottom <= window.innerHeight; }");
    }
}
