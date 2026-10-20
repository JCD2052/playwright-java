package io.github.jcd2052.demo.tests;

import io.github.jcd2052.demo.pages.DemoFramePage;
import io.github.jcd2052.demo.pages.DemoLoginPage;
import io.github.jcd2052.demo.pages.DemoProductsPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.concurrent.TimeoutException;
import java.math.BigDecimal;

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

    @Test
    public void loginFormFillsAndSubmits() {
        openDemoPage("/index.html");

        String status = loginPage.signIn("qa-engineer", "hunter2", true, "ua");
        Assert.assertEquals(status, "Welcome, qa-engineer! (remembered) [ua]");
    }

    @Test
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

    @Test
    public void productRowRemoveButtonRemovesTheRow() throws TimeoutException {
        openDemoPage("/table.html");

        productsPage.getRowOrThrow(product -> product.name().equals("Keyboard")).remove();
        productsPage.waitForRowCount(2);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(productsPage.getModelsFromRows().size(), 2);
        softAssert.assertFalse(productsPage.findModel(product -> product.name().equals("Keyboard")).isPresent());
        softAssert.assertAll();
    }

    @Test
    public void widgetInsideIframeCanBeFilledAndSubmitted() {
        openDemoPage("/frame.html");
        String status = framePage.confirmPayment("4242 4242 4242 4242");
        Assert.assertEquals(status, "Payment confirmed");
    }
}
