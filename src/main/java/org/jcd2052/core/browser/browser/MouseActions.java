package org.jcd2052.core.browser.browser;

import com.microsoft.playwright.Mouse;
import org.jcd2052.core.browser.browser.interfaces.IMouseActions;
import org.jcd2052.core.logger.LoggerProvider;

public class MouseActions implements IMouseActions {
    private final Mouse mouse;

    public MouseActions(Mouse mouse) {
        this.mouse = mouse;
    }

    @Override
    public void move(double x, double y) {
        mouse.move(x, y);
    }

    @Override
    public void move(double x, double y, int steps) {
        mouse.move(x, y, new Mouse.MoveOptions().setSteps(steps));
    }

    @Override
    public void click(double x, double y) {
        LoggerProvider.getLogger().info("Page-level mouse click at (x: %s, y: %s)", x, y);
        mouse.click(x, y);
    }

    @Override
    public void doubleClick(double x, double y) {
        LoggerProvider.getLogger().info("Page-level mouse double-click at (x: %s, y: %s)", x, y);
        mouse.dblclick(x, y);
    }

    @Override
    public void down() {
        mouse.down();
    }

    @Override
    public void up() {
        mouse.up();
    }

    @Override
    public void wheel(double deltaX, double deltaY) {
        LoggerProvider.getLogger().info("Scrolling mouse wheel by (x: %s, y: %s)", deltaX, deltaY);
        mouse.wheel(deltaX, deltaY);
    }
}