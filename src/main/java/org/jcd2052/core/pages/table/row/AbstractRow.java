package org.jcd2052.core.pages.table.row;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.interfaces.ILabelElement;
import org.jcd2052.core.pages.AbstractForm;

import java.util.List;

/**
 * A foundational abstract base class representing a single, index-bound row within a web table or grid.
 * <p>
 * This class extends {@link AbstractForm} to treat an individual row as a fully scoped Component Object.
 * By dynamically injecting a 1-based index into the row's root locator, it guarantees that any
 * subsequent element searches (such as finding cells or action buttons) are strictly contained within
 * the exact DOM boundaries of this specific row instance.
 * </p>
 *
 * @param <T> The data model, enum, or DTO type representing the structured data contained in this row.
 */
public abstract class AbstractRow<T> extends AbstractForm implements IRow<T> {

    /**
     * The relative locator (CSS or XPath) used to identify individual child cells inside this specific row.
     */
    protected final String cellLocator;

    /**
     * Constructs a scoped row component bound to a specific physical index on the page.
     *
     * @param position        The 1-based numerical index of the row within the DOM (e.g., 1 for the first row).
     * @param cellLocator     The relative CSS or XPath locator used to find cells strictly inside this row (e.g., {@code "td"} or {@code ".grid-cell"}).
     * @param locatorStrategy The base XPath expression for the row element itself, before indexing (e.g., {@code "//tbody/tr"}).
     * @param name            A human-readable descriptive name used for automated action logging.
     * @param elementFactory  The central factory used for dependency injection and lazy element resolution.
     */
    protected AbstractRow(
            int position,
            String cellLocator,
            String locatorStrategy,
            String name,
            IElementFactory elementFactory) {

        super(createPositionLocatorFromXpath(locatorStrategy, position), name, elementFactory);
        this.cellLocator = cellLocator;
    }

    /**
     * Retrieves a dynamically scoped collection of all cell elements inside this specific row.
     * <p>
     * The element search is executed strictly relative to the row's root locator, preventing elements
     * from bleeding in from other rows or tables present on the same page.
     * </p>
     *
     * @return A {@link List} of {@link ILabelElement} wrappers representing the individual cells.
     */
    protected List<ILabelElement> getCellElements() {
        return getFormLabel().createChildElements(ILabelElement.class, cellLocator, getName() + " cell").getElements();
    }

    /**
     * Helper method to format an XPath string by appending a specific 1-based index.
     * <p>
     * This transforms a generic row locator into a strictly indexed locator, ensuring the Playwright
     * engine interacts with the exact node intended.
     * </p>
     *
     * @param rowXpath The base XPath expression (e.g., {@code //tr} or {@code //div[@class='row']}).
     * @param position The 1-based numerical index to append.
     * @return The combined, absolute XPath string representing the exact node (e.g., {@code //tr[5]}).
     */
    protected static String createPositionLocatorFromXpath(String rowXpath, int position) {
        String rowXpathTemplate = String.format("%s[%%d]", rowXpath);
        return String.format(rowXpathTemplate, position);
    }
}