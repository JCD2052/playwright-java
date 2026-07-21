package org.jcd2052.demo.pages;

import org.jcd2052.core.browser.interfaces.IElementFactory;
import org.jcd2052.core.elements.interfaces.IButtonElement;
import org.jcd2052.core.elements.interfaces.ILabelElement;
import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.pages.table.row.AbstractRow;

import java.math.BigDecimal;

/**
 * Row Page Object for a single {@code <tr data-testid="product-row">} inside the bundled
 * {@code demo-site/table.html} products table. Every cell lookup below is scoped to this row's own
 * locator (via {@link #getFormLabel()}), so it can never bleed into another row.
 */
public class DemoProductRow extends AbstractRow<DemoProduct> {
    private final ILabelElement nameCell = getFormLabel().createChildElement(
            ILabelElement.class,
            Selector.bySelector("td.name"),
            "Name cell");
    private final ILabelElement priceCell = getFormLabel().createChildElement(
            ILabelElement.class,
            Selector.bySelector("td.price"),
            "Price cell");
    private final ILabelElement stockCell = getFormLabel().createChildElement(
            ILabelElement.class,
            Selector.bySelector("td.stock"),
            "Stock cell");
    private final IButtonElement removeButton = getFormLabel().createChildElement(
            IButtonElement.class,
            Selector.bySelector("button.remove"),
            "Remove button");

    protected DemoProductRow(int position, Selector cellLocator, Selector rowLocator, IElementFactory elementFactory) {
        super(position, cellLocator, rowLocator, "Product row " + position, elementFactory);
    }

    /**
     * Clicks this row's "Remove" button, deleting it from the table.
     */
    public void remove() {
        removeButton.click();
    }

    @Override
    public DemoProduct getModelFromRow() {
        String priceText = priceCell.getText().replace("$", "").trim();
        return new DemoProduct(
                nameCell.getText(),
                new BigDecimal(priceText),
                Integer.parseInt(stockCell.getText().trim()));
    }
}
