package org.jcd2052.elements.interfaces;

public interface ITextBoxElement extends IElement {
    void fillText(String text);

    void clearText();

    default void clearAndFillText(String text) {
        clearText();
        fillText(text);
    }
}
