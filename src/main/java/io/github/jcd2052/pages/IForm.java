package io.github.jcd2052.pages;

/**
 * A page object: a page, form or page fragment, identified by one root element.
 * <p>
 * The form's visibility and waits all refer to that root element. {@link AbstractForm} is the usual base class.
 * </p>
 */
public interface IForm {
    /**
     * Gets the form's name, which is also the name of its root element in log messages and errors.
     *
     * @return The form's name.
     */
    String getName();

    /**
     * Checks whether the form's root element is visible right now, without waiting.
     *
     * @return {@code true} if the form is currently visible; {@code false} otherwise.
     */
    boolean isVisible();

    /**
     * Waits for the form's root element to be visible, i.e. for the form to be shown.
     *
     * @param timeout The maximum time to wait in milliseconds; {@code null} for the window's default timeout.
     * @return {@code true} if the form was shown in time; {@code false} otherwise.
     */
    boolean waitForLoading(Double timeout);

    /**
     * Waits, up to the window's default timeout, for the form's root element to be visible.
     *
     * @return {@code true} if the form was shown in time; {@code false} otherwise.
     */
    default boolean waitForLoading() {
        return waitForLoading(null);
    }

    /**
     * Waits for the form's root element to become visible.
     *
     * @param timeout The maximum time to wait in milliseconds; {@code null} for the window's default timeout.
     * @return {@code true} if the form became visible; {@code false} otherwise.
     */
    boolean waitToBeVisible(Double timeout);

    /**
     * Waits, up to the window's default timeout, for the form's root element to become visible.
     *
     * @return {@code true} if the form became visible in time; {@code false} otherwise.
     */
    default boolean waitToBeVisible() {
        return waitToBeVisible(null);
    }

    /**
     * Waits for the form's root element to be removed from the DOM. A form that is only hidden (still in the
     * DOM) does not count.
     *
     * @param timeout The maximum time to wait in milliseconds; {@code null} for the window's default timeout.
     * @return {@code true} if the root element was removed in time; {@code false} otherwise.
     */
    boolean waitToBeInvisible(Double timeout);

    /**
     * Waits, up to the window's default timeout, for the form's root element to be removed from the DOM.
     *
     * @return {@code true} if the root element was removed in time; {@code false} otherwise.
     */
    default boolean waitToBeInvisible() {
        return waitToBeInvisible(null);
    }
}