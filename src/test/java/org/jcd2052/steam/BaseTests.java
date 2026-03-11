package org.jcd2052.steam;

import org.jcd2052.steam.configuration.PlaywrightSpringTestConfiguration;
import org.jcd2052.core.browser.services.interfaces.IBrowserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

@ContextConfiguration(classes = PlaywrightSpringTestConfiguration.class)
public class BaseTests extends AbstractTestNGSpringContextTests {
    private static final String TRACING_FOLDER_TEMPLATE = "trace_%s_%s.zip";

    @Autowired
    protected IBrowserService browserService;

    @BeforeClass(alwaysRun = true)
    public void setupBrowserApp() {
        browserService.getBrowser();
    }

    @BeforeMethod(firstTimeOnly = true, alwaysRun = true)
    public void setupBrowser(Method method) {
        browserService.start();
        browserService.startTracing();
    }

    @AfterMethod(lastTimeOnly = true, alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (!result.isSuccess()) {
            browserService.getBrowser().takeScreenshot();
            browserService.stopAndSaveTrace(String.format(
                    TRACING_FOLDER_TEMPLATE, result.getMethod().getMethodName(),
                    System.currentTimeMillis()));
        }
        browserService.getBrowser().closeWindow();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownBrowserApp() {
        browserService.close();
    }
}
