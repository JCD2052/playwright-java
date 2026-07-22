package org.jcd2052.core.elements;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.elements.interfaces.ILinkElement;
import org.jcd2052.core.logger.LoggerProvider;

/**
 * A concrete representation of a hyperlink element (typically an HTML {@code <a>} tag) within the web application.
 * <p>This class extends {@link AbstractElement} to inherit standard Playwright interactions
 * (such as {@code click()}) and implements {@link ILinkElement} to provide specific functionality
 * for links, such as retrieving the destination URL.</p>
 */
public class LinkElement extends AbstractElement implements ILinkElement {
    /**
     * The standard HTML attribute name for a hyperlink's destination URL.
     */
    private static final String HREF_ATTRIBUTE_NAME = "href";

    /**
     * Constructs a new {@code LinkElement}.
     *
     * @param selector       The Playwright Selector locator strategy used to find the element.
     * @param name           A human-readable name for the link (e.g., "Forgot Password Link", "Home"), utilized for logging.
     * @param elementFactory The {@link IElementFactory} responsible for creating and locating the element.
     */
    protected LinkElement(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    /**
     * Retrieves the value of the {@code href} attribute from the link element.
     * <p>This method is typically used to verify the destination URL of a link without
     * explicitly clicking it and navigating the browser. The action is logged prior to execution.</p>
     *
     * @return The URL string contained within the {@code href} attribute, or {@code null} if the attribute is not present.
     */
    @Override
    public String getHref() {
        highlightElementIfNeeded();
        String href = executeActionReturning(() -> getLocator().getAttribute(HREF_ATTRIBUTE_NAME), "getHref");
        LoggerProvider.getLogger().debugElementAction(
                getElementType(),
                getName(),
                "retrieved href attribute: '%s'",
                href);
        return href;
    }
}