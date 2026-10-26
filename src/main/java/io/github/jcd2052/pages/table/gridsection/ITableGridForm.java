package io.github.jcd2052.pages.table.gridsection;

import io.github.jcd2052.pages.IForm;
import io.github.jcd2052.pages.table.row.IRow;

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
     * Gets the row at the given position.
     * <p>
     * <b>Note:</b> rows use <b>1-based positions</b> (the first row is {@code 1}), matching how rows are
     * numbered on screen and in element names such as "Product row 1". This differs from
     * {@link io.github.jcd2052.elements.interfaces.IElementCollection} and {@code Selector.nth(...)},
     * which are 0-based. {@link io.github.jcd2052.pages.table.row.AbstractRow} rejects positions below 1.
     * </p>
     *
     * @param index The row's 1-based position.
     * @return The row's page object.
     */
    R getRow(int index);

    /**
     * Reads every row currently in the table into its data model, e.g. to assert on the whole table at once.
     *
     * @return A {@link List} of strongly-typed data models ({@code M}) representing the table's current content.
     */
    List<M> getModelsFromRows();

    /**
     * Gets a page object for every row currently in the table.
     *
     * @return The rows, in order.
     */
    List<R> getRowsFromPage();

    /**
     * Finds the data model of the first row matching a condition.
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
     * Gets the first row, i.e. {@link #getRow(int) getRow(1)}.
     *
     * @return The instantiated row Page Object of type {@code R} targeting the first row.
     */
    default R getFirstRow() {
        return getRow(1);
    }

    /**
     * Gets the first row whose data matches a condition, failing if there is none.
     *
     * @param condition A {@link Predicate} defining the search criteria against the data model {@code M}.
     * @return The actionable row Page Object of type {@code R}.
     * @throws IllegalArgumentException with the message "Could not find row." if no row matches.
     */
    default R getRowOrThrow(Predicate<M> condition) {
        return findRow(condition).orElseThrow(() -> new IllegalArgumentException("Could not find row."));
    }

    /**
     * Gets the first row whose data matches a condition, failing with the given message if there is none.
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
     * Finds the first row whose data matches a condition, e.g. the row whose user name is "Admin", so the test
     * can then click that row's "Edit" button. Every row's data is read until one matches.
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