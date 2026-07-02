package org.jcd2052.core.browser.browser.interfaces;

/**
 * Exposes raw, page-level mouse controls for complex movements and scrolling.
 */
public interface IMouseActions {
    /** Moves the mouse to the exact X and Y coordinates on the page. */
    void move(double x, double y);

    /** * Moves the mouse smoothly over a series of steps.
     * Great for bypassing bot-detection or simulating human dragging.
     */
    void move(double x, double y, int steps);

    /** Clicks at the exact X and Y coordinates. */
    void click(double x, double y);

    /** Presses the left mouse button down (without releasing it). */
    void down();

    /** Releases the mouse button. */
    void up();

    /** * Scrolls the mouse wheel.
     * @param deltaX Horizontal scroll amount.
     * @param deltaY Vertical scroll amount.
     */
    void wheel(double deltaX, double deltaY);
}