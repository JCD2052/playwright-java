package org.jcd2052.core.elements.interfaces;

/**
 * Represents a radio button element on a web page.
 * <p>
 * This interface extends {@link ICheckableElement}, providing a specific type
 * for radio input components. While it shares the "checkable" contract,
 * it is semantically distinct from checkboxes as radio buttons typically
 * belong to a group where only one selection is permitted at a time.
 */
public interface IRadioButtonElement extends ICheckableElement {
}