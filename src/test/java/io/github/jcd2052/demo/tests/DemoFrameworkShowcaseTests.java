package io.github.jcd2052.demo.tests;

import io.github.jcd2052.demo.pages.DemoFramePage;
import io.github.jcd2052.demo.pages.DemoLoginPage;
import io.github.jcd2052.demo.pages.DemoProductsPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.math.BigDecimal;
import java.util.concurrent.TimeoutException;

/**
 * Showcases the framework against a small set of static demo pages bundled with the repository
 * (see {@code src/test/resources/demo-site}).
 * <p>
 * This suite is fully offline (no network access beyond {@code localhost}).
 * </p>
 */
public class DemoFrameworkShowcaseTests extends DemoSiteBaseTests {
    @Autowired
    private DemoLoginPage loginPage;
    @Autowired
    private DemoProductsPage productsPage;
    @Autowired
    private DemoFramePage framePage;

    /**
     * Verifies the sign-in page object end to end: text boxes, checkbox and dropdown are filled, the form is
     * submitted, and the status message reflects every entered value.
     */
    @Test(description = "The login form fills and submits")
    public void loginFormFillsAndSubmits() {
        openDemoPage("/index.html");

        String status = loginPage.signIn("qa-engineer", "hunter2", true, "ua");
        Assert.assertEquals(status, "Welcome, qa-engineer! (remembered) [ua]");
    }

    /**
     * Verifies that the products table is read into plain data models, one per row, that can be searched by
     * their values.
     */
    @Test(description = "The products table exposes its rows as data")
    public void productsTableExposesRowsAsData() {
        openDemoPage("/table.html");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(productsPage.getModelsFromRows().size(), 3);
        softAssert.assertTrue(productsPage.findModel(product -> product.name().equals("Monitor")).isPresent());
        softAssert.assertEquals(
                productsPage.findModel(product -> product.name().equals("Mouse")).orElseThrow().price(),
                new BigDecimal("19.99"));
        softAssert.assertAll();
    }

    /**
     * Verifies that a row found by its data can be acted on: clicking its remove button removes exactly that row
     * from the table.
     */
    @Test(description = "A product row's remove button removes the row")
    public void productRowRemoveButtonRemovesTheRow() throws TimeoutException {
        openDemoPage("/table.html");

        productsPage.getRowOrThrow(product -> product.name().equals("Keyboard")).remove();
        productsPage.waitForRowCount(2);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(productsPage.getModelsFromRows().size(), 2);
        softAssert.assertFalse(productsPage.findModel(product -> product.name().equals("Keyboard")).isPresent());
        softAssert.assertAll();
    }

    /**
     * Verifies that elements inside an {@code <iframe>} can be filled and clicked through
     * {@code Selector.byFrame(...)}, like elements on the main page.
     */
    @Test(description = "A widget inside an iframe can be filled and submitted")
    public void widgetInsideIframeCanBeFilledAndSubmitted() {
        openDemoPage("/frame.html");
        String status = framePage.confirmPayment("4242 4242 4242 4242");
        Assert.assertEquals(status, "Payment confirmed");
    }
}
