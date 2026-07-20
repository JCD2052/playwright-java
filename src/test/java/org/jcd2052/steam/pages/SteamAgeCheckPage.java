package org.jcd2052.steam.pages;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.elements.interfaces.IButtonElement;
import org.jcd2052.core.elements.interfaces.IDropdownElement;
import org.jcd2052.core.pages.AbstractForm;
import org.springframework.stereotype.Component;

@Component
public class SteamAgeCheckPage extends AbstractForm {
    private final IDropdownElement dayDropdown;
    private final IDropdownElement monthDropdown;
    private final IDropdownElement yearDropdown;
    private final IButtonElement viewPageButton;

    protected SteamAgeCheckPage(IElementFactory elementFactory) {
        super(Selector.bySelector("//div[contains(@class, 'age_gate')]"), "Steam Age Check", elementFactory);
        this.dayDropdown = elementFactory.createDropdownElement(Selector.bySelector("#ageDay"), "Day");
        this.monthDropdown = elementFactory.createDropdownElement(Selector.bySelector("#ageMonth"), "Month");
        this.yearDropdown = elementFactory.createDropdownElement(Selector.bySelector("#ageYear"), "Year");
        this.viewPageButton = elementFactory.createButtonElement(Selector.bySelector("#view_product_page_btn"), "View Page");
    }

    public void fillTheForm(int day, String month, int year) {
        dayDropdown.selectByText(String.valueOf(day));
        monthDropdown.selectByText(String.valueOf(month));
        yearDropdown.selectByText(String.valueOf(year));
        viewPageButton.click();
    }
}
