package io.github.jcd2052.elements;

import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.ILabelElement;

/**
 * A concrete representation of a text label or static text element within the web application.
 * <p>This class extends {@link AbstractElement} to inherit standard Playwright
 * data-retrieval interactions (such as {@code getText()}, {@code isVisible()}, etc.)
 * and implements {@link ILabelElement} to fulfill any label-specific behavioral
 * contracts defined by the framework.</p>
 */
public class LabelElement extends AbstractElement implements ILabelElement {
    /**
     * Constructs a new {@code LabelElement}.
     *
     * @param selector       The Playwright Selector locator strategy used to find the element.
     * @param name           A human-readable name for the label (e.g., "Error Message", "Username Label"), utilized for logging.
     * @param elementFactory The {@link IElementFactory} responsible for managing and resolving locators.
     */
    protected LabelElement(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }
}