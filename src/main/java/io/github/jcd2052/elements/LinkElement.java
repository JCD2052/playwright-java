package io.github.jcd2052.elements;

import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.ILinkElement;
import io.github.jcd2052.logger.i18n.LogAction;

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
     * @param selector       The selector the element is found with.
     * @param name           A human-readable name for the link (e.g., "Forgot Password Link", "Home"), used in log messages and errors.
     * @param elementFactory The factory creating the element.
     */
    protected LinkElement(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    /**
     * Gets the link's {@code href} attribute, e.g. to check where a link leads without following it.
     *
     * @return The URL string contained within the {@code href} attribute, or {@code null} if the attribute is not present.
     */
    @Override
    public String getHref() {
        highlightElementIfNeeded();
        return executeActionReturning(() -> getLocator().getAttribute(HREF_ATTRIBUTE_NAME), LogAction.LINK_HREF_READ);
    }
}