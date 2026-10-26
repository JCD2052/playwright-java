package io.github.jcd2052.pages.table.gridsection;

import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.ILabelElement;
import io.github.jcd2052.pages.AbstractForm;
import io.github.jcd2052.pages.table.row.IRow;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The base class of a table's page object, exposing its rows both as row page objects and as data models.
 * <p>
 * Subclasses implement {@link #getRow(int)} to create the row page object for a position; this class counts the
 * rows matched by the row selector and builds the rest on top. The table's name gets " Grid Section" appended.
 * </p>
 *
 * @param <M> The Data Model type (DTO) representing the pure data extracted from a single row.
 * @param <R> The Row Page Object type (extending {@link IRow}) that represents the actionable web elements of a single row.
 */
public abstract class AbstractTableGridForm<M, R extends IRow<M>> extends AbstractForm implements ITableGridForm<M, R> {
    /**
     * The selector matching every row of the table.
     */
    private final Selector rowLocator;
    /**
     * Creates a table.
     *
     * @param locatorStrategy The selector of the table's root element.
     * @param rowLocator      The selector matching every row of the table.
     * @param name            The table's name, e.g. "Users"; " Grid Section" is appended to it.
     * @param elementFactory  The factory the table's elements are created with.
     */
    protected AbstractTableGridForm(
            Selector locatorStrategy,
            Selector rowLocator,
            String name,
            IElementFactory elementFactory) {
        super(locatorStrategy, name + " Grid Section", elementFactory);
        this.rowLocator = rowLocator;
    }

    /**
     * {@inheritDoc}
     * <p>Each row is mapped with its {@code getModelFromRow()}.</p>
     *
     * @return A {@link List} containing the data models ({@code M}) for every row in the table.
     */
    @Override
    public List<M> getModelsFromRows() {
        return getRowsFromPage().stream()
                .map(IRow::getModelFromRow)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * <p>The rows are counted, then created with {@link #getRow(int)} for positions 1 to the count.</p>
     *
     * @return A {@link List} of instantiated row objects of type {@code R}.
     */
    @Override
    public List<R> getRowsFromPage() {
        int rowsSize = getRowLabels().size();
        return IntStream.range(1, rowsSize + 1)
                .mapToObj(this::getRow)
                .collect(Collectors.toList());
    }

    /**
     * Gets a plain element for every row currently in the table, used to count the rows.
     *
     * @return A {@link List} of {@link ILabelElement} representing the raw row containers.
     */
    protected List<ILabelElement> getRowLabels() {
        return getElementFactory().createElementsCollection(
                rowLocator,
                getName() + " Row Label",
                ILabelElement.class).getElements();
    }
}