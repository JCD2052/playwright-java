package org.jcd2052.core.pages.table.gridsection;

import org.jcd2052.core.pages.IForm;
import org.jcd2052.core.pages.table.row.IRow;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Defines the core contract for interacting with structured data tables or grid components.
 * <p>
 * This interface extends {@link IForm} to inherit standard synchronization and visibility checks.
 * It enforces a strongly-typed, dual-layer architectural pattern that cleanly separates the
 * actionable UI elements of a row from the pure data (DTO) extracted from it.
 * </p>
 *
 * @param <M> The Data Model (DTO) type representing the pure data extracted from a single row.
 * @param <R> The Row Page Object type (extending {@link IRow}) representing the interactive web elements of a single row.
 */
public interface ITableGridForm<M, R extends IRow<M>> extends IForm {
    /**
     * Retrieves a specific row component from the table based on its physical DOM index.
     * <p>
     * <b>Note:</b> Implementations of this method strictly use <b>1-based indexing</b> to align
     * with standard CSS/XPath {@code nth-child()} selectors utilized by Playwright's engine.
     * </p>
     *
     * @param index The 1-based index of the row to retrieve.
     * @return An instantiated row Page Object of type {@code R} scoped to that specific index.
     */
    R getRow(int index);

    /**
     * Scrapes and extracts the underlying data from all currently rendered rows in the table.
     * <p>
     * This method acts as a bulk data-reader, ideal for making assertions against the entire
     * state of the grid without interacting with the UI elements.
     * </p>
     *
     * @return A {@link List} of strongly-typed data models ({@code M}) representing the table's current content.
     */
    List<M> getModelsFromRows();

    /**
     * Retrieves all currently rendered rows in the table as fully interactive Page Objects.
     *
     * @return A {@link List} of instantiated row objects of type {@code R}, allowing for bulk UI operations.
     */
    List<R> getRowsFromPage();

    /**
     * Searches the table's extracted data models to find the first model matching a specific condition.
     * <p>
     * Leverages the Java Stream API to efficiently filter the extracted DTOs.
     * </p>
     *
     * @param condition A {@link Predicate} defining the search criteria against the data model {@code M}.
     * @return An {@link Optional} containing the matched data model, or an empty Optional if no match is found.
     */
    default Optional<M> findModel(Predicate<M> condition) {
        return getModelsFromRows().stream()
                .filter(condition)
                .findFirst();
    }

    /**
     * Retrieves the very first row in the table.
     * <p>
     * A convenience method that resolves to calling {@link #getRow(int)} with an index of 1.
     * </p>
     *
     * @return The instantiated row Page Object of type {@code R} targeting the first row.
     */
    default R getFirstRow() {
        return getRow(1);
    }

    /**
     * Safely retrieves the first interactive row Page Object whose underlying data matches the specified condition.
     * <p>
     * If no matching row is found in the grid, this method instantly fails the test execution by
     * throwing an exception with a default message, saving you from writing repetitive {@code .orElseThrow()} boilerplate.
     * </p>
     *
     * @param condition A {@link Predicate} defining the search criteria against the data model {@code M}.
     * @return The actionable row Page Object of type {@code R}.
     * @throws IllegalArgumentException if no row matching the condition is currently present in the grid.
     */
    default R getRowOrThrow(Predicate<M> condition) {
        return findRow(condition).orElseThrow(() -> new IllegalArgumentException("Could not find row."));
    }

    /**
     * Safely retrieves the first interactive row Page Object whose underlying data matches the specified condition,
     * utilizing a custom error message if the row is missing.
     *
     * @param condition A {@link Predicate} defining the search criteria against the data model {@code M}.
     * @param message   The custom context message to include in the exception if the row is not found.
     * @return The actionable row Page Object of type {@code R}.
     * @throws IllegalArgumentException with the provided message if no row matching the condition is present in the grid.
     */
    default R getRowOrThrow(Predicate<M> condition, String message) {
        return findRow(condition).orElseThrow(() -> new IllegalArgumentException(message));
    }

    /**
     * Searches the table to find the first interactive row Page Object whose underlying data
     * matches the specified condition.
     * <p>
     * This is a highly powerful convenience method for locating a specific UI element based on its data
     * (e.g., finding the exact Row object where the internal Username model equals "Admin",
     * so that the test can click that specific row's "Edit" button).
     * </p>
     *
     * @param condition A {@link Predicate} defining the search criteria against the data model {@code M}.
     * @return An {@link Optional} containing the actionable row Page Object of type {@code R},
     * or an empty Optional if no matching row is found.
     */
    default Optional<R> findRow(Predicate<M> condition) {
        return getRowsFromPage().stream()
                .filter(row -> condition.test(row.getModelFromRow()))
                .findFirst();
    }
}