package org.jcd2052.demo.pages;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.pages.table.gridsection.AbstractTableGridForm;
import org.jcd2052.core.waiting.IConditionalWait;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeoutException;

/**
 * Page Object for the bundled {@code demo-site/table.html} products table.
 * <p>
 * Demonstrates {@link AbstractTableGridForm}: each row is exposed both as an interactive
 * {@link DemoProductRow} Page Object and as a plain {@link DemoProduct} data model, without any
 * fragile string-index locator concatenation.
 * </p>
 */
@Component
public class DemoProductsPage extends AbstractTableGridForm<DemoProduct, DemoProductRow> {
    private static final Selector ROW_LOCATOR = Selector.byTestId("product-row");
    private static final Selector CELL_LOCATOR = Selector.bySelector("td");

    private final IConditionalWait conditionalWait;

    protected DemoProductsPage(IElementFactory elementFactory, IConditionalWait conditionalWait) {
        super(Selector.byTestId("products-table"), ROW_LOCATOR, "Products", elementFactory);
        this.conditionalWait = conditionalWait;
    }

    @Override
    public DemoProductRow getRow(int index) {
        return new DemoProductRow(index, CELL_LOCATOR, ROW_LOCATOR, getElementFactory());
    }

    /**
     * Waits until the table has exactly {@code expectedCount} rows.
     * <p>
     * This is the kind of composite, multi-element condition {@link IConditionalWait} is for:
     * "how many rows currently exist" isn't a property of any single {@link org.jcd2052.core.elements.interfaces.IElement},
     * so it can't be expressed as one of the per-element waits on {@code IElement} itself.
     * </p>
     *
     * @param expectedCount the row count to wait for
     * @throws TimeoutException if the row count doesn't reach {@code expectedCount} in time
     */
    public void waitForRowCount(int expectedCount) throws TimeoutException {
        conditionalWait.waitForTrue(
                () -> getModelsFromRows().size() == expectedCount,
                "Products table never reached " + expectedCount + " row(s)");
    }
}

