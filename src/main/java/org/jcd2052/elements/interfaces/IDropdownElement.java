package org.jcd2052.elements.interfaces;

import java.util.List;

public interface IDropdownElement extends IElement{
    String getSelectedOption();

    List<String> getTexts();

    void selectByValue(String option);

    void selectByIndex(int index);

    void selectByText(String text);
}
