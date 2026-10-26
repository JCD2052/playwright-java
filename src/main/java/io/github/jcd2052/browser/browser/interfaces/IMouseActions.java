package io.github.jcd2052.browser.browser.interfaces;

/**
 * Exposes raw, page-level mouse controls that work with viewport coordinates instead of elements, for
 * movements an element action can't express (custom drags, drawing, wheel scrolling).
 * Coordinates are in CSS pixels, relative to the top-left corner of the viewport.
 */
public interface IMouseActions {
    /**
     * Moves the mouse to the given position in a single step.
     *
     * @param x The horizontal position in pixels.
     * @param y The vertical position in pixels.
     */
    void move(double x, double y);

    /**
     * Moves the mouse to the given position through intermediate moves, firing a {@code mousemove} event for
     * each one, as a user dragging would.
     *
     * @param x     The horizontal position in pixels.
     * @param y     The vertical position in pixels.
     * @param steps The number of intermediate moves.
     */
    void move(double x, double y, int steps);

    /**
     * Clicks at the given position.
     *
     * @param x The horizontal position in pixels.
     * @param y The vertical position in pixels.
     */
    void click(double x, double y);

    /**
     * Double-clicks at the given position.
     *
     * @param x The horizontal position in pixels.
     * @param y The vertical position in pixels.
     */
    void doubleClick(double x, double y);

    /**
     * Presses the left mouse button at the current position, without releasing it.
     */
    void down();

    /**
     * Releases the mouse button pressed with {@link #down()}.
     */
    void up();

    /**
     * Scrolls with the mouse wheel.
     *
     * @param deltaX The horizontal scroll amount in pixels.
     * @param deltaY The vertical scroll amount in pixels.
     */
    void wheel(double deltaX, double deltaY);
}