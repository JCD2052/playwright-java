package org.jcd2052.core.elements.interfaces;

/**
 * Represents a hyperlink element on a web page.
 * <p>
 * This interface extends {@link IElement} and provides specialized functionality
 * for interacting with anchor tags ({@code <a>}) and other navigable links.
 */
public interface ILinkElement extends IElement {

    /**
     * Retrieves the value of the "href" attribute of the link.
     * <p>
     * This represents the URL or destination that the hyperlink points to.
     *
     * @return The target URL string defined in the href attribute.
     */
    String getHref();
}