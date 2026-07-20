package org.jcd2052.demo.tests;

import org.jcd2052.demo.pages.DemoFramePage;
import org.jcd2052.demo.pages.DemoLoginPage;
import org.jcd2052.demo.pages.DemoProductsPage;
import org.jcd2052.demo.support.DemoServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;

/**
 * Showcases the framework against a small set of static demo pages bundled with the repository
 * (see {@code src/test/resources/demo-site}).
 * <p>
 * This suite is fully offline (no network access beyond {@code localhost}).
 * </p>
 */
public class DemoFrameworkShowcaseTests extends SpringBootBaseTests {
    @Autowired
    private DemoLoginPage loginPage;
    @Autowired
    private DemoProductsPage productsPage;
    @Autowired
    private DemoFramePage framePage;
    private DemoServer demoServer;

    @BeforeClass(alwaysRun = true)
    public void startDemoServer() {
        try {
            demoServer = new DemoServer();
            demoServer.start();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to start the bundled demo server", e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void stopDemoServer() {
        if (demoServer != null) {
            demoServer.stop();
        }
    }

    @Test
    public void loginFormFillsAndSubmits() {
        browserService.navigateTo(demoServer.getBaseUrl() + "/index.html");

        String status = loginPage.signIn("qa-engineer", "hunter2", true, "ua");
        Assert.assertEquals(status, "Welcome, qa-engineer! (remembered) [ua]");
    }

    @Test
    public void productsTableExposesRowsAsData() {
        browserService.navigateTo(demoServer.getBaseUrl() + "/table.html");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(productsPage.getModelsFromRows().size(), 3);
        softAssert.assertTrue(productsPage.findModel(product -> product.name().equals("Monitor")).isPresent());
        softAssert.assertEquals(
                productsPage.findModel(product -> product.name().equals("Mouse")).orElseThrow().price(),
                new BigDecimal("19.99"));
        softAssert.assertAll();
    }

    @Test
    public void productRowRemoveButtonRemovesTheRow() {
        browserService.navigateTo(demoServer.getBaseUrl() + "/table.html");

        productsPage.getRowOrThrow(product -> product.name().equals("Keyboard")).remove();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(productsPage.getModelsFromRows().size(), 2);
        softAssert.assertFalse(productsPage.findModel(product -> product.name().equals("Keyboard")).isPresent());
        softAssert.assertAll();
    }

    @Test
    public void widgetInsideIframeCanBeFilledAndSubmitted() {
        browserService.navigateTo(demoServer.getBaseUrl() + "/frame.html");
        String status = framePage.confirmPayment("4242 4242 4242 4242");
        Assert.assertEquals(status, "Payment confirmed");
    }
}
