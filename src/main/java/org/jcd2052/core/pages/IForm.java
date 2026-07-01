package org.jcd2052.core.pages;

/**
 * Defines the foundational contract for all page objects, forms, or UI fragments within the framework.
 * <p>Implementing classes (such as {@link AbstractForm}) use this interface to ensure a consistent
 * approach to managing form state, visibility, and synchronization during automated test execution.</p>
 */
public interface IForm {
    /**
     * Retrieves the human-readable name of the form.
     * <p>This name is typically used for framework-level logging and reporting to provide
     * clear, descriptive output regarding which part of the application is being interacted with.</p>
     *
     * @return The string representation of the form's name.
     */
    String getName();

    /**
     * Evaluates whether the form is currently visible in the DOM.
     * <p>This is an instantaneous check and does not typically involve waiting.</p>
     *
     * @return {@code true} if the form is currently visible; {@code false} otherwise.
     */
    boolean isVisible();

    /**
     * Halts test execution until the form has completely loaded.
     *
     * @param timeout The maximum time to wait in milliseconds. If null, the framework default is used.
     * @return {@code true} if the form successfully loaded; {@code false} otherwise.
     */
    boolean waitForLoading(Double timeout);

    /**
     * Halts test execution until the form has completely loaded using default timeout.
     */
    default boolean waitForLoading() {
        return waitForLoading(null);
    }

    /**
     * Halts test execution until the form becomes visible on the page.
     *
     * @param timeout The maximum time to wait in milliseconds. If null, the framework default is used.
     * @return {@code true} if the form became visible; {@code false} otherwise.
     */
    boolean waitToBeVisible(Double timeout);

    /**
     * Halts test execution until the form becomes visible on the page using default timeout.
     */
    default boolean waitToBeVisible() {
        return waitToBeVisible(null);
    }

    /**
     * Halts test execution until the form becomes invisible or is completely detached from the DOM.
     *
     * @param timeout The maximum time to wait in milliseconds. If null, the framework default is used.
     * @return {@code true} if the form became invisible; {@code false} otherwise.
     */
    boolean waitToBeInvisible(Double timeout);

    /**
     * Halts test execution until the form becomes invisible using default timeout.
     */
    default boolean waitToBeInvisible() {
        return waitToBeInvisible(null);
    }
}