package org.jcd2052.core.pages.table.row;

/**
 * Defines the contract for a single, interactive row within a data table or grid.
 * <p>This interface represents the Page Object for a specific table row. It serves as the
 * bridge between the actionable UI elements (e.g., buttons, text cells, checkboxes) contained
 * within the row and the pure data model (DTO) that represents the row's underlying information.</p>
 *
 * @param <T> The Data Model type (DTO) representing the pure data extracted from this row.
 */
public interface IRow<T> {

    /**
     * Extracts the data from the UI elements of this row and maps it into a strongly-typed model.
     * <p>Implementations of this method typically read the text or values from the row's
     * specific field elements and use them to instantiate and populate the data model {@code T}.
     * This allows test scripts to assert against pure data rather than complex web elements.</p>
     *
     * @return A populated data model of type {@code T} containing the information extracted from the UI row.
     */
    T getModelFromRow();
}