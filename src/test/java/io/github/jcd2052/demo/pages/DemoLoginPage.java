package io.github.jcd2052.demo.pages;

import com.microsoft.playwright.options.AriaRole;
import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.interfaces.IButtonElement;
import io.github.jcd2052.elements.interfaces.ICheckBoxElement;
import io.github.jcd2052.elements.interfaces.IDropdownElement;
import io.github.jcd2052.elements.interfaces.ILabelElement;
import io.github.jcd2052.elements.interfaces.ITextBoxElement;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.pages.AbstractForm;
import org.springframework.stereotype.Component;

/**
 * Page Object for the bundled {@code demo-site/index.html} sign-in form.
 * <p>
 * Demonstrates {@code byTestId}, {@code byRole}, and {@code bySelector} strategies together with
 * text box, checkbox, dropdown, and button elements.
 * </p>
 */
@Component
public class DemoLoginPage extends AbstractForm {
    private final ITextBoxElement usernameInput = getElementFactory().createTextBoxElement(
            Selector.byTestId("username-input"),
            "Username");
    private final ITextBoxElement passwordInput = getElementFactory().createTextBoxElement(
            Selector.byTestId("password-input"),
            "Password");
    private final ICheckBoxElement rememberMeCheckbox = getElementFactory().createCheckBoxElement(
            Selector.byTestId("remember-me"),
            "Remember me");
    private final IDropdownElement countryDropdown = getElementFactory().createDropdownElement(
            Selector.byTestId("country-select"),
            "Country");
    private final IButtonElement signInButton = getElementFactory().createButtonElement(
            Selector.byRole(AriaRole.BUTTON, "Sign In"),
            "Sign In");
    private final ILabelElement statusLabel = getElementFactory().createLabelElement(
            Selector.bySelector("#status"),
            "Status");

    protected DemoLoginPage(IElementFactory elementFactory) {
        super(Selector.bySelector("#login-form"), "Demo Login Page", elementFactory);
    }

    /**
     * Fills in the sign-in form and submits it.
     *
     * @param username     the username to enter
     * @param password     the password to enter
     * @param rememberMe   whether to check the "Remember me" checkbox
     * @param countryValue the {@code value} attribute of the country option to select, or
     *                     {@code null} to leave the dropdown at its default option
     * @return the resulting status message shown on the page
     */
    public String signIn(String username, String password, boolean rememberMe, String countryValue) {
        usernameInput.clearAndFillText(username);
        passwordInput.clearAndFillText(password);

        if (rememberMe) {
            rememberMeCheckbox.check();
        }
        if (countryValue != null) {
            countryDropdown.selectByValue(countryValue);
        }

        signInButton.click();
        return statusLabel.getText();
    }
}
