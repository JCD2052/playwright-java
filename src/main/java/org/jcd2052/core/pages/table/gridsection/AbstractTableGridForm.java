package org.jcd2052.core.pages.table.gridsection;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.interfaces.ILabelElement;
import org.jcd2052.core.pages.AbstractForm;
import org.jcd2052.core.pages.table.row.IRow;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * An abstract base class representing a structured data table or grid within the web application.
 * <p>This class extends {@link AbstractForm} to inherit standard form synchronization (like waiting
 * for the table to load) and implements {@link ITableGridForm}. It bridges the gap between the
 * raw UI DOM elements (rows and cells) and strongly-typed Java data models.</p>
 *
 * @param <M> The Data Model type (DTO) representing the pure data extracted from a single row.
 * @param <R> The Row Page Object type (extending {@link IRow}) that represents the actionable web elements of a single row.
 */
public abstract class AbstractTableGridForm<M, R extends IRow<M>> extends AbstractForm implements ITableGridForm<M, R> {
    /**
     * The specific Playwright selector used to locate the individual row containers (e.g., "tbody tr")
     * within the broader table grid.
     */
    private final String rowLocator;

    /**
     * Constructs a new {@code AbstractTableGridForm}.
     *
     * @param locatorStrategy The Playwright selector used to locate the main table container (the root form label).
     * @param rowLocator      The Playwright selector used to locate individual rows within the table.
     * @param name            A human-readable name for the table (e.g., "Users Data Grid"), used for logging.
     * @param elementFactory  The {@link IElementFactory} responsible for creating the underlying elements and collections.
     */
    protected AbstractTableGridForm(
            String locatorStrategy,
            String rowLocator,
            String name,
            IElementFactory elementFactory) {
        super(locatorStrategy, name + " Grid Section", elementFactory);
        this.rowLocator = rowLocator;
    }

    /**
     * Parses the entire visible table and extracts the underlying data into strongly-typed models.
     * <p>This method iterates through all row Page Objects and invokes their specific mapping
     * logic ({@code getModelFromRow()}) to strip away the UI wrappers and return pure data.</p>
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
     * Retrieves all rows currently present in the table as interactive Page Objects.
     * <p>This method determines the total number of rows and generates a 1-based index stream
     * to dynamically instantiate and return the specific row elements ({@code R}).</p>
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
     * A private helper method that resolves the {@code rowLocator} into a collection of generic label elements.
     * <p>This is utilized primarily to accurately count the number of rows currently attached to the DOM
     * before attempting to map them to complex row objects or data models.</p>
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