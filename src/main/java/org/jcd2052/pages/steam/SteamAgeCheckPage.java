package org.jcd2052.pages.steam;

import org.jcd2052.browser.services.ElementFactory;
import org.jcd2052.elements.interfaces.IButtonElement;
import org.jcd2052.elements.interfaces.IDropdownElement;
import org.jcd2052.pages.AbstractPage;
import org.springframework.stereotype.Component;

@Component
public class SteamAgeCheckPage extends AbstractPage {
    private final IDropdownElement dayDropdown;
    private final IDropdownElement monthDropdown;
    private final IDropdownElement yearDropdown;
    private final IButtonElement viewPageButton;

    protected SteamAgeCheckPage(ElementFactory elementFactory) {
        super("//div[contains(@class, 'age_gate')]", "Steam Age Check", elementFactory);
        this.dayDropdown = elementFactory.createDropdownElement("#ageDay", "Day");
        this.monthDropdown = elementFactory.createDropdownElement("#ageMonth", "Month");
        this.yearDropdown = elementFactory.createDropdownElement("#ageYear", "Year"); ;
        this.viewPageButton = elementFactory.createButtonElement("#view_product_page_btn", "View Page");
    }

    public void fillTheForm(int day, String month, int year) {
        dayDropdown.selectByText(String.valueOf(day));
        monthDropdown.selectByText(String.valueOf(month));
        yearDropdown.selectByText(String.valueOf(year));
        viewPageButton.click();
    }
}
