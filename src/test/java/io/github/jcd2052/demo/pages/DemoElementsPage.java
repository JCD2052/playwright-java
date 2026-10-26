package io.github.jcd2052.demo.pages;

import com.microsoft.playwright.options.AriaRole;
import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.ExpectedCount;
import io.github.jcd2052.elements.interfaces.IButtonElement;
import io.github.jcd2052.elements.interfaces.ICheckBoxElement;
import io.github.jcd2052.elements.interfaces.IDropdownElement;
import io.github.jcd2052.elements.interfaces.IElement;
import io.github.jcd2052.elements.interfaces.IElementCollection;
import io.github.jcd2052.elements.interfaces.ILabelElement;
import io.github.jcd2052.elements.interfaces.ILinkElement;
import io.github.jcd2052.elements.interfaces.IRadioButtonElement;
import io.github.jcd2052.elements.interfaces.ITextBoxElement;
import io.github.jcd2052.elements.interfaces.IUploadBox;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.pages.AbstractForm;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the bundled {@code demo-site/elements.html} showcase, which has one fixture per
 * element type.
 * <p>
 * Its elements are private: every interaction is a page method that performs one element action and
 * returns plain values, so tests never touch elements directly. The elements use a different
 * {@link Selector} strategy for almost every fixture.
 * </p>
 */
@Component
public class DemoElementsPage extends AbstractForm {
    private final IButtonElement actionButton = getElementFactory().createButtonElement(
            Selector.byTestId("action-button"),
            "Action button");
    private final IButtonElement disabledButton = getElementFactory().createButtonElement(
            Selector.byRole(AriaRole.BUTTON, "Disabled"),
            "Disabled button");
    private final IButtonElement unstableButton = getElementFactory().createButtonElement(
            Selector.byText("Unstable", true),
            "Unstable button");
    private final IButtonElement noPointerButton = getElementFactory().createButtonElement(
            Selector.bySelector("#no-pointer-button"),
            "No pointer button");
    private final IButtonElement missingButton = getElementFactory().createButtonElement(
            Selector.bySelector("#does-not-exist"),
            "Missing button");
    private final ILabelElement buttonLog = getElementFactory().createLabelElement(
            Selector.byTestId("button-log"),
            "Button log");

    private final ILabelElement greeting = getElementFactory().createLabelElement(
            Selector.byTitle("Greeting title"),
            "Greeting");
    private final IButtonElement showLaterButton = getElementFactory().createButtonElement(
            Selector.byRole(AriaRole.BUTTON, "Show later"),
            "Show later button");
    private final ILabelElement hiddenLater = getElementFactory().createLabelElement(
            Selector.bySelector("#hidden-later"),
            "Hidden later label");
    private final IButtonElement removeLaterButton = getElementFactory().createButtonElement(
            Selector.byRole(AriaRole.BUTTON, "Remove later"),
            "Remove later button");
    private final ILabelElement removable = getElementFactory().createLabelElement(
            Selector.bySelector("#removable"),
            "Removable label");
    private final ILabelElement hoverTarget = getElementFactory().createLabelElement(
            Selector.byText("Hover me"),
            "Hover target");
    private final ILabelElement hoverLog = getElementFactory().createLabelElement(
            Selector.bySelector("#hover-log"),
            "Hover log");

    private final ITextBoxElement echoInput = getElementFactory().createTextBoxElement(
            Selector.byPlaceholder("Type here"),
            "Echo input");
    private final ILabelElement echoLog = getElementFactory().createLabelElement(
            Selector.bySelector("#echo-log"),
            "Echo log");

    private final IDropdownElement fruitDropdown = getElementFactory().createDropdownElement(
            Selector.byLabel("Fruit"),
            "Fruit dropdown");

    private final ILinkElement productsLink = getElementFactory().createLinkElement(
            Selector.byRole(AriaRole.LINK, "Go to products"),
            "Products link");

    private final ICheckBoxElement newsletterCheckbox = getElementFactory().createCheckBoxElement(
            Selector.byLabel("Subscribe to newsletter"),
            "Newsletter checkbox");
    private final IRadioButtonElement freePlanRadio = getElementFactory().createRadioButtonElement(
            Selector.byLabel("Free plan"),
            "Free plan radio");
    private final IRadioButtonElement proPlanRadio = getElementFactory().createRadioButtonElement(
            Selector.byLabel("Pro plan"),
            "Pro plan radio");

    private final IUploadBox avatarUpload = getElementFactory().createUploadBoxElement(
            Selector.byTestId("avatar-upload"),
            "Avatar upload");
    private final ILabelElement uploadLog = getElementFactory().createLabelElement(
            Selector.bySelector("#upload-log"),
            "Upload log");

    private final ILabelElement tagList = getElementFactory().createLabelElement(
            Selector.bySelector("#tags"),
            "Tag list");
    private final ILabelElement allTagLabels = getElementFactory().createLabelElement(
            Selector.bySelector("li.tag"),
            "All tag labels");
    private final IElementCollection<ILabelElement> tags = getElementFactory().createElementsCollection(
            Selector.bySelector("li.tag"),
            "Tag",
            ILabelElement.class,
            ExpectedCount.MORE_THAN_ZERO);
    private final ILabelElement cardLog = getElementFactory().createLabelElement(
            Selector.bySelector("#card-log"),
            "Card log");

    private final ILabelElement dragSource = getElementFactory().createLabelElement(
            Selector.bySelector("#drag-source"),
            "Drag source");
    private final ILabelElement dropTarget = getElementFactory().createLabelElement(
            Selector.bySelector("#drop-target"),
            "Drop target");

    private final ILabelElement farAway = getElementFactory().createLabelElement(
            Selector.bySelector("#far-away"),
            "Far away label");

    protected DemoElementsPage(IElementFactory elementFactory) {
        super(Selector.bySelector("#elements"), "Demo Elements Page", elementFactory);
    }

    /**
     * Clicks the action button once.
     */
    public void clickActionButton() {
        actionButton.click();
    }

    /**
     * Clicks the action button at the given offset from its top-left corner.
     *
     * @param x horizontal offset in pixels
     * @param y vertical offset in pixels
     */
    public void clickActionButtonAt(double x, double y) {
        actionButton.click(x, y);
    }

    /**
     * Double-clicks the action button.
     */
    public void doubleClickActionButton() {
        actionButton.doubleClick();
    }

    /**
     * Right-clicks the action button.
     */
    public void rightClickActionButton() {
        actionButton.rightClick();
    }

    /**
     * Clicks the action button with the middle mouse button.
     */
    public void middleClickActionButton() {
        actionButton.middleClick();
    }

    /**
     * Force-clicks the constantly moving button, skipping Playwright's actionability checks.
     */
    public void forceClickUnstableButton() {
        unstableButton.forceClick();
    }

    /**
     * Clicks the button that ignores pointer events, using JavaScript.
     */
    public void clickNoPointerButtonWithJs() {
        noPointerButton.clickWithJs();
    }

    /**
     * Clicks a button that does not exist on the page.
     */
    public void clickMissingButton() {
        missingButton.click();
    }

    /**
     * @return whether the action button is enabled
     */
    public boolean isActionButtonEnabled() {
        return actionButton.isEnabled();
    }

    /**
     * @return whether the button with the {@code disabled} attribute is enabled
     */
    public boolean isDisabledButtonEnabled() {
        return disabledButton.isEnabled();
    }

    /**
     * @return whether the action button ignores pointer events ({@code pointer-events: none})
     */
    public boolean isActionButtonIgnoringPointerEvents() {
        return actionButton.getJsActions().isPointerEventsDisabled();
    }

    /**
     * @return whether the no-pointer button ignores pointer events ({@code pointer-events: none})
     */
    public boolean isNoPointerButtonIgnoringPointerEvents() {
        return noPointerButton.getJsActions().isPointerEventsDisabled();
    }

    /**
     * @return the text the buttons write when they are clicked, e.g. {@code "click@10,5"} or {@code "double-click"}
     */
    public String getButtonLog() {
        return buttonLog.getText();
    }

    /**
     * @return the name the greeting label was created with
     */
    public String getGreetingName() {
        return greeting.getName();
    }

    /**
     * @return the greeting's text
     */
    public String getGreetingText() {
        return greeting.getText();
    }

    /**
     * @param attributeName the HTML attribute to read
     * @return the greeting's value for that attribute
     */
    public String getGreetingAttribute(String attributeName) {
        return greeting.getAttribute(attributeName);
    }

    /**
     * Sets an HTML attribute on the greeting using JavaScript.
     *
     * @param attributeName the attribute to set
     * @param value         the value to set it to
     */
    public void setGreetingAttributeWithJs(String attributeName, String value) {
        greeting.getJsActions().setAttribute(attributeName, value);
    }

    /**
     * @return whether the greeting is visible
     */
    public boolean isGreetingVisible() {
        return greeting.isVisible();
    }

    /**
     * @return a PNG screenshot of the greeting
     */
    public byte[] takeGreetingScreenshot() {
        return greeting.getScreenshot();
    }

    /**
     * @return the text of every tag, read through one label that matches all of them
     */
    public List<String> getAllTagTexts() {
        return allTagLabels.getAllTexts();
    }

    /**
     * Clicks the button that shows the hidden label after a short delay.
     */
    public void showLabelLater() {
        showLaterButton.click();
    }

    /**
     * @return whether the initially hidden label is visible
     */
    public boolean isHiddenLabelVisible() {
        return hiddenLater.isVisible();
    }

    /**
     * Waits, up to the default timeout, for the initially hidden label to become visible.
     *
     * @return whether it became visible in time
     */
    public boolean waitForHiddenLabelToBeVisible() {
        return hiddenLater.waitToBeVisible();
    }

    /**
     * Waits for the initially hidden label to become visible.
     *
     * @param timeout the timeout in milliseconds
     * @return whether it became visible in time
     */
    public boolean waitForHiddenLabelToBeVisible(double timeout) {
        return hiddenLater.waitToBeVisible(timeout);
    }

    /**
     * Clicks the button that removes the removable label after a short delay.
     */
    public void removeLabelLater() {
        removeLaterButton.click();
    }

    /**
     * Waits, up to the default timeout, for the removable label to be removed from the page.
     *
     * @return whether it was removed in time
     */
    public boolean waitForRemovableLabelToBeRemoved() {
        return removable.waitToBeDetached();
    }

    /**
     * @return whether the removable label is visible
     */
    public boolean isRemovableLabelVisible() {
        return removable.isVisible();
    }

    /**
     * Moves the mouse over the hover target.
     */
    public void hoverOverHoverTarget() {
        hoverTarget.hover();
    }

    /**
     * @return the text the hover target writes when the mouse enters it
     */
    public String getHoverLog() {
        return hoverLog.getText();
    }

    /**
     * Fills the echo input with the given text.
     *
     * @param text the text to enter
     */
    public void fillEcho(String text) {
        echoInput.fillText(text);
    }

    /**
     * Replaces the echo input's text with the given text.
     *
     * @param text the text to enter
     */
    public void replaceEcho(String text) {
        echoInput.clearAndFillText(text);
    }

    /**
     * Clears the echo input.
     */
    public void clearEcho() {
        echoInput.clearText();
    }

    /**
     * @return the echo input's current value
     */
    public String getEchoValue() {
        return echoInput.getInputValue();
    }

    /**
     * Presses Enter in the echo input, which submits its form.
     */
    public void submitEchoWithEnter() {
        echoInput.press("Enter");
    }

    /**
     * Moves the focus away from the echo input.
     */
    public void unfocusEcho() {
        echoInput.unfocus();
    }

    /**
     * @return the text the echo form writes when it is submitted or the input loses focus
     */
    public String getEchoLog() {
        return echoLog.getText();
    }

    /**
     * @return the text of every option in the fruit dropdown
     */
    public List<String> getFruitOptions() {
        return fruitDropdown.getTexts();
    }

    /**
     * @return the text of the selected fruit
     */
    public String getSelectedFruit() {
        return fruitDropdown.getSelectedOption();
    }

    /**
     * Selects a fruit by its visible text.
     *
     * @param text the option text, e.g. {@code "Banana"}
     */
    public void selectFruitByText(String text) {
        fruitDropdown.selectByText(text);
    }

    /**
     * Selects a fruit by its {@code value} attribute.
     *
     * @param value the option value, e.g. {@code "cherry"}
     */
    public void selectFruitByValue(String value) {
        fruitDropdown.selectByValue(value);
    }

    /**
     * Selects a fruit by its position.
     *
     * @param index the zero-based option index
     */
    public void selectFruitByIndex(int index) {
        fruitDropdown.selectByIndex(index);
    }

    /**
     * @return the products link's {@code href}
     */
    public String getProductsLinkHref() {
        return productsLink.getHref();
    }

    /**
     * Clicks the link to the products page.
     */
    public void clickProductsLink() {
        productsLink.click();
    }

    /**
     * @return whether the newsletter checkbox is checked
     */
    public boolean isNewsletterChecked() {
        return newsletterCheckbox.isChecked();
    }

    /**
     * Checks the newsletter checkbox.
     */
    public void checkNewsletter() {
        newsletterCheckbox.check();
    }

    /**
     * Unchecks the newsletter checkbox.
     */
    public void uncheckNewsletter() {
        newsletterCheckbox.uncheck();
    }

    /**
     * @return whether the free plan radio button is selected
     */
    public boolean isFreePlanSelected() {
        return freePlanRadio.isChecked();
    }

    /**
     * @return whether the pro plan radio button is selected
     */
    public boolean isProPlanSelected() {
        return proPlanRadio.isChecked();
    }

    /**
     * Selects the pro plan by clicking its radio button.
     */
    public void selectProPlan() {
        proPlanRadio.click();
    }

    /**
     * Uploads a file as the avatar.
     *
     * @param file the path of the file to upload
     */
    public void uploadAvatar(Path file) {
        avatarUpload.upload(file);
    }

    /**
     * Uploads a file as the avatar.
     *
     * @param file the file to upload
     */
    public void uploadAvatar(File file) {
        avatarUpload.upload(file);
    }

    /**
     * @return the name of the uploaded file, as shown by the page
     */
    public String getUploadedFileName() {
        return uploadLog.getText();
    }

    /**
     * @return the text of every tag, read element by element through a collection
     */
    public List<String> getTagTexts() {
        return tags.getElements().stream().map(IElement::getText).collect(Collectors.toList());
    }

    /**
     * @return the names the collection gave its tag elements, e.g. {@code "Tag 1"}
     */
    public List<String> getTagElementNames() {
        return tags.getElements().stream().map(IElement::getName).collect(Collectors.toList());
    }

    /**
     * @return the text of every list item, found as child elements of the tag list only
     */
    public List<String> getChildTagTexts() {
        return tagList.createChildElements(ILabelElement.class, Selector.bySelector("li"), "Child tag",
                        ExpectedCount.MORE_THAN_ZERO)
                .getElements()
                .stream()
                .map(IElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Picks a card by clicking the "Pick" button inside that card only.
     * Every card has an identical "Pick" button, so this only works because the button is created as a
     * child of the card.
     *
     * @param title the card's title, e.g. {@code "Beta"}
     */
    public void pickCard(String title) {
        ILabelElement card = getElementFactory().createLabelElement(
                Selector.bySelector(String.format(".card:has-text('%s')", title)),
                title + " card");
        card.createChildElement(IButtonElement.class, Selector.byRole(AriaRole.BUTTON, "Pick"), "Pick button")
                .click();
    }

    /**
     * @return the text the cards write when one is picked, e.g. {@code "picked: Beta"}
     */
    public String getCardLog() {
        return cardLog.getText();
    }

    /**
     * Drags the drag source onto the drop target with Playwright's built-in drag.
     */
    public void dragSourceToTarget() {
        dragSource.dragTo(dropTarget);
    }

    /**
     * Drags the drag source onto the drop target by moving the mouse in steps.
     *
     * @param steps the number of intermediate mouse moves
     */
    public void dragSourceToTargetInSteps(int steps) {
        dragSource.dragAndDropTo(dropTarget, steps);
    }

    /**
     * @return the drop target's text, which becomes {@code "Dropped"} after a successful drop
     */
    public String getDropTargetText() {
        return dropTarget.getText();
    }

    /**
     * Scrolls the far-away label into view.
     */
    public void scrollToFarAwayLabel() {
        farAway.scrollToElement();
    }

    /**
     * Scrolls the far-away label into view using JavaScript.
     */
    public void scrollToFarAwayLabelWithJs() {
        farAway.getJsActions().scrollIntoView();
    }

    /**
     * @return whether the far-away label is fully inside the viewport
     */
    public boolean isFarAwayLabelInViewport() {
        return farAway.getJsActions().isInViewport();
    }
}
