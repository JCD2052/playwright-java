package org.jcd2052.steam;

import org.jcd2052.steam.configuration.SpringContextTestConfiguration;
import org.jcd2052.core.browser.services.interfaces.IBrowserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

@ContextConfiguration(classes = SpringContextTestConfiguration.class)
public class BaseTests extends AbstractTestNGSpringContextTests {
    private static final String TRACING_FOLDER_TEMPLATE = "trace_%s_%s.zip";

    @Autowired
    protected IBrowserService browserService;

    @BeforeMethod(alwaysRun = true)
    public void setupBrowser() {
        browserService.start();
        browserService.startTracing();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (!result.isSuccess()) {
            browserService.getBrowser().takeScreenshot();
            browserService.stopAndSaveTrace(String.format(
                    TRACING_FOLDER_TEMPLATE, result.getMethod().getMethodName(),
                    System.currentTimeMillis()));
        }
        browserService.close();
    }
}
