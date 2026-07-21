package org.jcd2052.demo.pages;

import org.jcd2052.core.browser.interfaces.IElementFactory;
import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.pages.table.gridsection.AbstractTableGridForm;
import org.springframework.stereotype.Component;

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

    protected DemoProductsPage(IElementFactory elementFactory) {
        super(Selector.byTestId("products-table"), ROW_LOCATOR, "Products", elementFactory);
    }

    @Override
    public DemoProductRow getRow(int index) {
        return new DemoProductRow(index, CELL_LOCATOR, ROW_LOCATOR, getElementFactory());
    }
}
