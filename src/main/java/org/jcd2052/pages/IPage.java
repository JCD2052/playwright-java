package org.jcd2052.pages;

import org.jcd2052.elements.interfaces.ILabelElement;

public interface IPage {
    ILabelElement getLabel();

    String getName();

    default void waitForLoading() {
        getLabel().waitForLoading();
    }
}
