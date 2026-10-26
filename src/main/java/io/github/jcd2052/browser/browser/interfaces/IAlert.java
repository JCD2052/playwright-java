package io.github.jcd2052.browser.browser.interfaces;

/**
 * Represents a native browser dialog (JavaScript {@code alert}, {@code confirm}, or {@code prompt}) opened by
 * a page. Obtained through {@link IBrowserTab#handleNextAlert(java.util.function.Consumer, Runnable)}, which
 * also returns it after handling, so its values can still be read once it is closed.
 */
public interface IAlert {
    /**
     * Gets the text the dialog shows.
     *
     * @return The message text displayed inside the dialog.
     */
    String getMessage();

    /**
     * Gets the kind of dialog.
     *
     * @return The dialog's type.
     */
    AlertType getType();

    /**
     * Gets the value a {@code prompt} dialog's text field starts with (the second argument of {@code prompt}).
     *
     * @return The prompt's default value, or an empty string for other dialogs or a prompt without one.
     */
    String getDefaultValue();

    /**
     * Accepts the dialog, like clicking "OK". A {@code prompt} accepted this way is answered with an empty string;
     * use {@link #fillText(String)} to answer it with text.
     */
    void accept();

    /**
     * Dismisses the dialog, like clicking "Cancel": a {@code confirm} returns {@code false} to the page, and a
     * {@code prompt} returns {@code null}.
     */
    void decline();

    /**
     * Enters text into a {@code prompt} dialog and accepts it.
     *
     * @param text The text to enter into the prompt.
     */
    void fillText(String text);
}
