package org.jcd2052.core.pages.table.row;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.elements.interfaces.ILabelElement;
import org.jcd2052.core.pages.AbstractForm;

import java.util.List;

/**
 * A foundational abstract base class representing a single, index-bound row within a web table or grid.
 * <p>
 * This class extends {@link AbstractForm} to treat an individual row as a fully scoped Component Object.
 * Selector dynamically injecting a 1-based index into the row's root locator, it guarantees that any
 * subsequent element searches (such as finding cells or action buttons) are strictly contained within
 * the exact DOM boundaries of this specific row instance.
 * </p>
 *
 * @param <T> The data model, enum, or DTO type representing the structured data contained in this row.
 */
public abstract class AbstractRow<T> extends AbstractForm implements IRow<T> {
    /**
     * The relative locator strategy used to identify individual child cells inside this specific row.
     */
    protected final Selector cellLocator;

    /**
     * Constructs a scoped row component bound to a specific physical index on the page.
     *
     * @param position        The 1-based numerical index of the row within the DOM (e.g., 1 for the first row).
     * @param cellLocator     The relative Selector locator used to find cells strictly inside this row.
     * @param locatorStrategy The base Selector locator expression for the row element itself, before indexing.
     * @param name            A human-readable descriptive name used for automated action logging.
     * @param elementFactory  The central factory used for dependency injection and lazy element resolution.
     */
    protected AbstractRow(
            int position,
            Selector cellLocator,
            Selector locatorStrategy,
            String name,
            IElementFactory elementFactory) {
        super(createPositionLocator(locatorStrategy, position), name, elementFactory);
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
     * Helper method to format a locator strategy by appending a specific index.
     * <p>
     * This replaces the old XPath string manipulation ({@code [%d]}) by natively wrapping
     * the strategy using Playwright's {@code nth()} function. It converts the 1-based physical
     * position into a 0-based index to ensure the Playwright engine interacts with the exact node intended.
     * </p>
     *
     * @param rowLocator The base Selector locator strategy (e.g., matching all rows).
     * @param position   The 1-based numerical index to target.
     * @return The combined, absolute Selector strategy representing the exact node.
     */
    protected static Selector createPositionLocator(Selector rowLocator, int position) {
        int index = position - 1;

        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return rowLocator.evaluate(page).nth(index);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return rowLocator.evaluate(parent).nth(index);
            }
        };
    }
}