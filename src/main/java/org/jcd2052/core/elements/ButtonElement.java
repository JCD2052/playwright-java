package org.jcd2052.core.elements;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.interfaces.IButtonElement;

/**
 * A concrete representation of a clickable button element within the web application.
 * * <p>This class extends {@link AbstractElement} to inherit standard Playwright
 * interactions (such as {@code click()}, {@code isVisible()}, etc.) and implements
 * {@link IButtonElement} to fulfill any button-specific behavioral contracts defined
 * by the framework.</p>
 */
public class ButtonElement extends AbstractElement implements IButtonElement {
    /**
     * Constructs a new {@code ButtonElement}.
     *
     * @param locatorValue   The Playwright selector (e.g., CSS, XPath, or text locator) used to find the button in the DOM.
     * @param name           A human-readable name for the button (e.g., "Submit Button", "Login"), utilized for logging.
     * @param elementFactory The {@link IElementFactory} responsible for managing and resolving locators.
     */
    protected ButtonElement(String locatorValue, String name, IElementFactory elementFactory) {
        super(locatorValue, name, elementFactory);
    }
}