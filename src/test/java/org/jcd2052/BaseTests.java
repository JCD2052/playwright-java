package org.jcd2052;

import com.microsoft.playwright.Tracing;
import org.jcd2052.appconfiguration.AppConfiguration;
import org.jcd2052.browser.browser.interfaces.IBrowserWindow;
import org.jcd2052.browser.services.interfaces.IBrowserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Paths;

@ContextConfiguration(classes = AppConfiguration.class)
public class BaseTests extends AbstractTestNGSpringContextTests {
    @Autowired
    protected IBrowserService browserService;

    @BeforeMethod
    public void setUp() {
        IBrowserWindow iBrowserWindow = browserService.getBrowser().openNewWindow();
        iBrowserWindow.getBrowserContext().tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));
        iBrowserWindow.openNewTab().navigateTo("https://store.steampowered.com/");
        browserService.getBrowser().getCurrentBrowserWindow().getBrowserContext();
    }

    @AfterMethod
    public void tearDown() {
        browserService.getBrowser()
                .getCurrentBrowserWindow()
                .getBrowserContext()
                .tracing()
                .stop(new Tracing.StopOptions().setPath(Paths.get(String.format("src/main/resources/trace___%s.zip", System.currentTimeMillis()))));
        browserService.close();

    }
}
