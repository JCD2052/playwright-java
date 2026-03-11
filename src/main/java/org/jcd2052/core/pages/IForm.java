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
     * <p>The specific conditions for "loading" (e.g., waiting for a spinner to disappear or
     * a specific network state) are defined by the implementing class.</p>
     *
     * @return {@code true} if the form successfully loaded within the designated timeout; {@code false} otherwise.
     */
    boolean waitForLoading();

    /**
     * Halts test execution until the form becomes visible on the page.
     * <p>This is useful for synchronizing interactions with forms that appear dynamically,
     * such as popups, modals, or dropdown menus.</p>
     *
     * @return {@code true} if the form became visible within the designated timeout; {@code false} otherwise.
     */
    boolean waitToBeVisible();

    /**
     * Halts test execution until the form becomes invisible or is completely detached from the DOM.
     * <p>This is frequently used to verify that a form (like a modal dialog or loading overlay)
     * has successfully closed or disappeared before proceeding with the next test step.</p>
     *
     * @return {@code true} if the form became invisible within the designated timeout; {@code false} otherwise.
     */
    boolean waitToBeInvisible();
}