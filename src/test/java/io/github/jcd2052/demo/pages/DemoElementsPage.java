package io.github.jcd2052.demo.pages;

import com.microsoft.playwright.options.AriaRole;
import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.ExpectedCount;
import io.github.jcd2052.elements.interfaces.IButtonElement;
import io.github.jcd2052.elements.interfaces.ICheckBoxElement;
import io.github.jcd2052.elements.interfaces.IDropdownElement;
import io.github.jcd2052.elements.interfaces.IElementCollection;
import io.github.jcd2052.elements.interfaces.ILabelElement;
import io.github.jcd2052.elements.interfaces.ILinkElement;
import io.github.jcd2052.elements.interfaces.IRadioButtonElement;
import io.github.jcd2052.elements.interfaces.ITextBoxElement;
import io.github.jcd2052.elements.interfaces.IUploadBox;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.pages.AbstractForm;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * Page Object for the bundled {@code demo-site/elements.html} showcase, which has one fixture per
 * element type.
 * <p>
 * Unlike the other demo pages, this one exposes its elements through getters so the element tests can
 * exercise each element's API directly. It also uses a different {@link Selector} strategy for almost
 * every element.
 * </p>
 */
@Getter
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
    private final ILabelElement betaCard = getElementFactory().createLabelElement(
            Selector.byTestId("card-beta"),
            "Beta card");
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
     * Picks the given card by clicking the "Pick" button scoped to that card only.
     * Every card has an identical "Pick" button, so this only works because the button is created as
     * a child of the card.
     *
     * @param card the card whose button should be clicked
     */
    public void pickCard(ILabelElement card) {
        card.createChildElement(IButtonElement.class, Selector.byRole(AriaRole.BUTTON, "Pick"), "Pick button")
                .click();
    }
}
