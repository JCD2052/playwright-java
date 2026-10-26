package io.github.jcd2052.demo.tests;

import io.github.jcd2052.browser.browser.interfaces.IBrowserTab;
import io.github.jcd2052.demo.pages.DemoElementsPage;
import io.github.jcd2052.exceptions.ElementTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Exercises every built-in element type, and the common element API, against the bundled
 * {@code demo-site/elements.html} showcase page, through the {@link DemoElementsPage} page object.
 */
public class DemoElementsTests extends DemoSiteBaseTests {
    /**
     * The first bytes of every PNG file, used to check that a screenshot is a real image.
     */
    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 'P', 'N', 'G'};

    @Autowired
    private DemoElementsPage page;

    /**
     * Opens the elements showcase page before each test and waits for it to load.
     */
    @BeforeMethod(alwaysRun = true)
    public void openElementsPage() {
        openDemoPage("/elements.html");
        Assert.assertTrue(page.waitForLoading(), "Elements page did not load");
    }

    /**
     * Verifies that a click reaches the button as a single click.
     */
    @Test(description = "click fires a single click")
    public void clickFiresASingleClick() {
        page.clickActionButton();

        Assert.assertTrue(page.getButtonLog().startsWith("click@"));
    }

    /**
     * Verifies that clicking at a position clicks at that offset inside the element.
     */
    @Test(description = "click at a position clicks at the given offset inside the element")
    public void clickAtPositionClicksAtTheGivenOffsetInsideTheElement() {
        page.clickActionButtonAt(10, 5);

        Assert.assertEquals(page.getButtonLog(), "click@10,5");
    }

    /**
     * Verifies that a double-click reaches the button as a double-click.
     */
    @Test(description = "doubleClick fires a double click")
    public void doubleClickFiresADoubleClick() {
        page.doubleClickActionButton();

        Assert.assertEquals(page.getButtonLog(), "double-click");
    }

    /**
     * Verifies that a right-click opens the context menu.
     */
    @Test(description = "rightClick opens the context menu")
    public void rightClickOpensTheContextMenu() {
        page.rightClickActionButton();

        Assert.assertEquals(page.getButtonLog(), "right-click");
    }

    /**
     * Verifies that a middle click uses the middle mouse button.
     */
    @Test(description = "middleClick uses the middle mouse button")
    public void middleClickUsesTheMiddleMouseButton() {
        page.middleClickActionButton();

        Assert.assertEquals(page.getButtonLog(), "middle-click");
    }

    /**
     * Verifies that a force click reaches a constantly moving element, which a normal click would wait on
     * because the element never becomes stable.
     */
    @Test(description = "forceClick skips actionability checks on an unstable element")
    public void forceClickSkipsActionabilityChecksOnAnUnstableElement() {
        page.forceClickUnstableButton();

        Assert.assertEquals(page.getButtonLog(), "unstable-click");
    }

    /**
     * Verifies that {@code isEnabled} reflects the {@code disabled} attribute.
     */
    @Test(description = "isEnabled reflects the disabled attribute")
    public void isEnabledReflectsTheDisabledAttribute() {
        Assert.assertTrue(page.isActionButtonEnabled());
        Assert.assertFalse(page.isDisabledButtonEnabled());
    }

    /**
     * Verifies that pointer-event detection works, and that a JavaScript click reaches an element that ignores
     * pointer events.
     */
    @Test(description = "clickWithJs reaches an element that ignores pointer events")
    public void clickWithJsReachesAnElementThatIgnoresPointerEvents() {
        Assert.assertTrue(page.isNoPointerButtonIgnoringPointerEvents());
        Assert.assertFalse(page.isActionButtonIgnoringPointerEvents());

        page.clickNoPointerButtonWithJs();

        Assert.assertEquals(page.getButtonLog(), "js-click");
    }

    /**
     * Verifies that acting on an element that is not on the page fails with an {@link ElementTimeoutException}
     * whose message names the action in plain words, the element, and its selector.
     */
    @Test(description = "Acting on a missing element throws a readable framework timeout")
    public void actingOnAMissingElementThrowsAReadableFrameworkTimeout() {
        ElementTimeoutException exception = Assert.expectThrows(ElementTimeoutException.class,
                () -> page.clickMissingButton());

        Assert.assertTrue(exception.getMessage().contains("Failed to perform 'click' on element 'Missing button'"));
        Assert.assertTrue(exception.getMessage().contains("#does-not-exist"));
    }

    /**
     * Verifies that a label reports the name it was created with, its text, its attributes and its visibility.
     */
    @Test(description = "A label exposes its name, text, attributes and visibility")
    public void labelExposesItsNameTextAttributesAndVisibility() {
        Assert.assertEquals(page.getGreetingName(), "Greeting");
        Assert.assertEquals(page.getGreetingText(), "Hello, elements!");
        Assert.assertEquals(page.getGreetingAttribute("title"), "Greeting title");
        Assert.assertTrue(page.isGreetingVisible());
    }

    /**
     * Verifies that an attribute set with JavaScript can be read back.
     */
    @Test(description = "setAttribute with JavaScript is visible to getAttribute")
    public void setAttributeWithJsIsVisibleToGetAttribute() {
        page.setGreetingAttributeWithJs("data-state", "updated");

        Assert.assertEquals(page.getGreetingAttribute("data-state"), "updated");
    }

    /**
     * Verifies that reading all texts through one element that matches several returns the text of each match.
     */
    @Test(description = "getAllTexts returns the text of every matching element")
    public void getAllTextsReturnsTheTextOfEveryMatchingElement() {
        Assert.assertEquals(page.getAllTagTexts(), List.of("java", "playwright", "testng"));
    }

    /**
     * Verifies that waiting for visibility waits for an element that only appears after a delay.
     */
    @Test(description = "waitToBeVisible waits for an element that appears later")
    public void waitToBeVisibleWaitsForAnElementThatAppearsLater() {
        Assert.assertFalse(page.isHiddenLabelVisible());

        page.showLabelLater();

        Assert.assertTrue(page.waitForHiddenLabelToBeVisible());
    }

    /**
     * Verifies that waiting for visibility returns {@code false}, instead of throwing, when the element never
     * appears within the timeout.
     */
    @Test(description = "waitToBeVisible returns false when the element never appears")
    public void waitToBeVisibleReturnsFalseWhenTheElementNeverAppears() {
        Assert.assertFalse(page.waitForHiddenLabelToBeVisible(300.0));
    }

    /**
     * Verifies that waiting for an element to be detached waits until it is removed from the page.
     */
    @Test(description = "waitToBeDetached waits for an element to be removed")
    public void waitToBeDetachedWaitsForAnElementToBeRemoved() {
        page.removeLabelLater();

        Assert.assertTrue(page.waitForRemovableLabelToBeRemoved());
        Assert.assertFalse(page.isRemovableLabelVisible());
    }

    /**
     * Verifies that hovering moves the mouse over the element.
     */
    @Test(description = "hover moves the pointer over the element")
    public void hoverMovesThePointerOverTheElement() {
        page.hoverOverHoverTarget();

        Assert.assertEquals(page.getHoverLog(), "hovered");
    }

    /**
     * Verifies that an element screenshot is a PNG image.
     */
    @Test(description = "getScreenshot returns a PNG image")
    public void getScreenshotReturnsAPngImage() {
        byte[] screenshot = page.takeGreetingScreenshot();

        Assert.assertTrue(screenshot.length > PNG_SIGNATURE.length);
        for (int i = 0; i < PNG_SIGNATURE.length; i++) {
            Assert.assertEquals(screenshot[i], PNG_SIGNATURE[i]);
        }
    }

    /**
     * Verifies that a text box can be filled, replaced and cleared, and that its value can be read each time.
     */
    @Test(description = "A text box fills, reads and clears its value")
    public void textBoxFillsReadsAndClearsItsValue() {
        page.fillEcho("first");
        Assert.assertEquals(page.getEchoValue(), "first");

        page.replaceEcho("second");
        Assert.assertEquals(page.getEchoValue(), "second");

        page.clearEcho();
        Assert.assertEquals(page.getEchoValue(), "");
    }

    /**
     * Verifies that pressing a key sends it to the element: Enter submits the text box's form.
     */
    @Test(description = "press sends a key to the element")
    public void pressSendsAKeyToTheElement() {
        page.fillEcho("hello");
        page.submitEchoWithEnter();

        Assert.assertEquals(page.getEchoLog(), "submitted: hello");
    }

    /**
     * Verifies that unfocusing an element moves the focus away from it.
     */
    @Test(description = "unfocus blurs the element")
    public void unfocusBlursTheElement() {
        page.fillEcho("focused");
        page.unfocusEcho();

        Assert.assertEquals(page.getEchoLog(), "blurred");
    }

    /**
     * Verifies that a dropdown lists all its options and reports the default selection.
     */
    @Test(description = "A dropdown lists its options and the default selection")
    public void dropdownListsItsOptionsAndTheDefaultSelection() {
        Assert.assertEquals(page.getFruitOptions(), List.of("Apple", "Banana", "Cherry"));
        Assert.assertEquals(page.getSelectedFruit(), "Apple");
    }

    /**
     * Verifies that a dropdown option can be selected by its text, its value and its index.
     */
    @Test(description = "A dropdown selects by text, value and index")
    public void dropdownSelectsByTextValueAndIndex() {
        page.selectFruitByText("Banana");
        Assert.assertEquals(page.getSelectedFruit(), "Banana");

        page.selectFruitByValue("cherry");
        Assert.assertEquals(page.getSelectedFruit(), "Cherry");

        page.selectFruitByIndex(0);
        Assert.assertEquals(page.getSelectedFruit(), "Apple");
    }

    /**
     * Verifies that a link reports its {@code href} and navigates to it when clicked.
     */
    @Test(description = "A link exposes its href and navigates when clicked")
    public void linkExposesItsHrefAndNavigatesWhenClicked() {
        Assert.assertEquals(page.getProductsLinkHref(), "table.html");

        page.clickProductsLink();

        IBrowserTab tab = browserService.getBrowser().requireCurrentBrowserTab();
        tab.getPage().waitForURL("**/table.html");
        Assert.assertTrue(tab.getCurrentUrl().endsWith("/table.html"));
    }

    /**
     * Verifies that a checkbox can be checked and unchecked, and reports its state each time.
     */
    @Test(description = "A checkbox checks and unchecks")
    public void checkboxChecksAndUnchecks() {
        Assert.assertFalse(page.isNewsletterChecked());

        page.checkNewsletter();
        Assert.assertTrue(page.isNewsletterChecked());

        page.uncheckNewsletter();
        Assert.assertFalse(page.isNewsletterChecked());
    }

    /**
     * Verifies that clicking a radio button selects it and deselects the other buttons of its group.
     */
    @Test(description = "Clicking a radio button selects it and deselects the rest of the group")
    public void clickingARadioButtonSelectsItAndDeselectsTheRestOfTheGroup() {
        Assert.assertTrue(page.isFreePlanSelected());
        Assert.assertFalse(page.isProPlanSelected());

        page.selectProPlan();

        Assert.assertTrue(page.isProPlanSelected());
        Assert.assertFalse(page.isFreePlanSelected());
    }

    /**
     * Verifies that an upload box accepts a file given as a {@link Path}.
     */
    @Test(description = "An upload box accepts a Path")
    public void uploadBoxAcceptsAPath() throws IOException {
        Path file = Files.createTempFile("avatar-path-", ".txt");
        try {
            page.uploadAvatar(file);

            Assert.assertEquals(page.getUploadedFileName(), file.getFileName().toString());
        } finally {
            Files.deleteIfExists(file);
        }
    }

    /**
     * Verifies that an upload box accepts a file given as a {@link java.io.File}.
     */
    @Test(description = "An upload box accepts a File")
    public void uploadBoxAcceptsAFile() throws IOException {
        Path file = Files.createTempFile("avatar-file-", ".txt");
        try {
            page.uploadAvatar(file.toFile());

            Assert.assertEquals(page.getUploadedFileName(), file.getFileName().toString());
        } finally {
            Files.deleteIfExists(file);
        }
    }

    /**
     * Verifies that an element collection wraps every match as its own element, named by its position.
     */
    @Test(description = "An element collection wraps every match as its own named element")
    public void elementCollectionWrapsEveryMatchAsItsOwnNamedElement() {
        Assert.assertEquals(page.getTagTexts(), List.of("java", "playwright", "testng"));
        Assert.assertEquals(page.getTagElementNames(), List.of("Tag 1", "Tag 2", "Tag 3"));
    }

    /**
     * Verifies that child elements are only looked up inside their parent element.
     */
    @Test(description = "Child elements are scoped to their parent")
    public void childElementsAreScopedToTheirParent() {
        Assert.assertEquals(page.getChildTagTexts(), List.of("java", "playwright", "testng"));
    }

    /**
     * Verifies that a child element tells apart identical buttons that sit in different containers.
     */
    @Test(description = "A child element disambiguates identical buttons in different containers")
    public void childElementDisambiguatesIdenticalButtonsInDifferentContainers() {
        page.pickCard("Beta");

        Assert.assertEquals(page.getCardLog(), "picked: Beta");
    }

    /**
     * Verifies that Playwright's built-in drag drops the element on the target.
     */
    @Test(description = "dragTo drops the element on the target")
    public void dragToDropsTheElementOnTheTarget() {
        page.dragSourceToTarget();

        Assert.assertEquals(page.getDropTargetText(), "Dropped");
    }

    /**
     * Verifies that dragging by moving the mouse in steps drops the element on the target, even though the
     * elements start outside the viewport.
     */
    @Test(description = "dragAndDropTo moves the mouse in steps")
    public void dragAndDropToMovesTheMouseInSteps() {
        page.dragSourceToTargetInSteps(10);

        Assert.assertEquals(page.getDropTargetText(), "Dropped");
    }

    /**
     * Verifies that scrolling to an element brings an off-screen element into the viewport.
     */
    @Test(description = "scrollToElement brings an off-screen element into view")
    public void scrollToElementBringsAnOffscreenElementIntoView() {
        Assert.assertFalse(page.isFarAwayLabelInViewport());

        page.scrollToFarAwayLabel();

        Assert.assertTrue(page.isFarAwayLabelInViewport());
    }

    /**
     * Verifies that scrolling with JavaScript brings an off-screen element into the viewport.
     */
    @Test(description = "scrollIntoView with JavaScript brings an off-screen element into view")
    public void scrollIntoViewWithJsBringsAnOffscreenElementIntoView() {
        Assert.assertFalse(page.isFarAwayLabelInViewport());

        page.scrollToFarAwayLabelWithJs();

        Assert.assertTrue(page.isFarAwayLabelInViewport());
    }
}
