package org.jcd2052.demo.pages;

import com.microsoft.playwright.options.AriaRole;
import org.jcd2052.core.browser.interfaces.IElementFactory;
import org.jcd2052.core.elements.interfaces.IButtonElement;
import org.jcd2052.core.elements.interfaces.ILabelElement;
import org.jcd2052.core.elements.interfaces.ITextBoxElement;
import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.pages.AbstractForm;
import org.springframework.stereotype.Component;

/**
 * Page Object for the bundled {@code demo-site/frame.html} page, whose "Payment Widget" is a
 * separate document loaded inside an {@code <iframe>}. Every element below is resolved via
 * {@link Selector#byFrame(String, Selector)}, so ordinary {@code byTestId}/{@code byRole}
 * strategies keep working unchanged once you're inside the frame.
 */
@Component
public class DemoFramePage extends AbstractForm {
    private static final String WIDGET_FRAME_SELECTOR = "#payment-widget";
    private final ITextBoxElement cardNumberInput = getElementFactory().createTextBoxElement(
            Selector.byFrame(WIDGET_FRAME_SELECTOR, Selector.byTestId("card-number-input")),
            "Card number");
    private final IButtonElement confirmButton = getElementFactory().createButtonElement(
            Selector.byFrame(WIDGET_FRAME_SELECTOR, Selector.byRole(AriaRole.BUTTON, "Confirm")),
            "Confirm");
    private final ILabelElement widgetStatus = getElementFactory().createLabelElement(
            Selector.byFrame(WIDGET_FRAME_SELECTOR, Selector.bySelector("#widget-status")),
            "Widget status");

    protected DemoFramePage(IElementFactory elementFactory) {
        super(Selector.bySelector(WIDGET_FRAME_SELECTOR), "Widget Frame Page", elementFactory);
    }

    /**
     * Fills in the card number inside the iframe widget and confirms the payment.
     *
     * @param cardNumber the card number to enter
     * @return the resulting status message shown inside the widget
     */
    public String confirmPayment(String cardNumber) {
        cardNumberInput.clearAndFillText(cardNumber);
        confirmButton.click();
        return widgetStatus.getText();
    }
}
