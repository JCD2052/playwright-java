package org.jcd2052.demo.pages;

import java.math.BigDecimal;

/**
 * Pure data model extracted from a single row of the bundled {@code demo-site/table.html} products
 * table, produced by {@link DemoProductRow#getModelFromRow()}.
 *
 * @param name  the product name
 * @param price the product price
 * @param stock the number of units in stock
 */
public record DemoProduct(String name, BigDecimal price, int stock) {
}
