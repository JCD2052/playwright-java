package io.github.jcd2052.pages.table.row;

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
     * Reads the row's cells and maps them into the data model, so tests can assert on plain data.
     *
     * @return A populated data model of type {@code T} containing the information extracted from the UI row.
     */
    T getModelFromRow();
}