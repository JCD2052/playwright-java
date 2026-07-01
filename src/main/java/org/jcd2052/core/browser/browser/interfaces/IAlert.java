package org.jcd2052.core.browser.browser.interfaces;

public interface IAlert {
    /** @return The message text displayed inside the alert. */
    String getMessage();

    /** Accepts the alert (clicks "OK"). */
    void accept();

    /** Declines/Dismisses the alert (clicks "Cancel"). */
    void decline();

    /** * Fills a prompt dialog with text and accepts it.
     * @param text The string to type into the prompt.
     */
    void fillText(String text);
}
