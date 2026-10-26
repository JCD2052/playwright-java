package io.github.jcd2052.pages.table.row;

import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.ILabelElement;
import io.github.jcd2052.pages.AbstractForm;

import java.util.List;

/**
 * The base class of a table row's page object, bound to one row by its position.
 * <p>
 * The row is a form whose root element is the row at the given 1-based position among the rows matched by the
 * row selector. Its cells and other elements are created as children of that root (via {@code getFormLabel()}),
 * so they never match elements of other rows. Subclasses map the row's cells to a data model in
 * {@link #getModelFromRow()}.
 * </p>
 *
 * @param <T> The data model, enum, or DTO type representing the structured data contained in this row.
 */
public abstract class AbstractRow<T> extends AbstractForm implements IRow<T> {
    /**
     * The selector of the row's cells, relative to the row.
     */
    protected final Selector cellLocator;

    /**
     * Creates a row bound to the given position.
     *
     * @param position        The row's 1-based position among the rows matched by {@code locatorStrategy}.
     * @param cellLocator     The selector of the row's cells, relative to the row.
     * @param locatorStrategy The selector matching all rows of the table.
     * @param name            The row's name, used in log messages and errors.
     * @param elementFactory  The factory the row's elements are created with.
     * @throws IllegalArgumentException if {@code position} is below 1.
     */
    protected AbstractRow(
            int position,
            Selector cellLocator,
            Selector locatorStrategy,
            String name,
            IElementFactory elementFactory) {
        super(createPositionLocator(locatorStrategy, position), name, elementFactory);
        this.cellLocator = cellLocator;
    }

    /**
     * Gets the row's cells, found inside this row only.
     *
     * @return A {@link List} of {@link ILabelElement} wrappers representing the individual cells.
     */
    protected List<ILabelElement> getCellElements() {
        return getFormLabel().createChildElements(ILabelElement.class, cellLocator, getName() + " cell").getElements();
    }

    /**
     * Narrows the row selector to the row at a 1-based position, using {@link Selector#nth(int)} with the
     * matching 0-based index.
     *
     * @param rowLocator The selector matching all rows.
     * @param position   The row's 1-based position.
     * @return The selector of that one row.
     * @throws IllegalArgumentException if {@code position} is below 1 (e.g. a 0-based index was passed).
     */
    protected static Selector createPositionLocator(Selector rowLocator, int position) {
        if (position < 1) {
            throw new IllegalArgumentException(
                    "Row positions are 1-based (the first row is 1), but got " + position);
        }
        return rowLocator.nth(position - 1);
    }
}