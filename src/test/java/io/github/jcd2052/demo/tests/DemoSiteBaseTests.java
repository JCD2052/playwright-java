package io.github.jcd2052.demo.tests;

import io.github.jcd2052.demo.support.DemoServer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Base class for tests that run against the static demo pages bundled with the repository
 * (see {@code src/test/resources/demo-site}). Starts a local {@link DemoServer} once per test class,
 * so these suites are fully offline (no network access beyond {@code localhost}).
 */
public abstract class DemoSiteBaseTests extends SpringBootBaseTests {
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

    /**
     * Navigates the current tab to one of the bundled demo pages.
     *
     * @param page the page path relative to the demo site root, e.g. {@code "/index.html"}
     */
    protected void openDemoPage(String page) {
        browserService.getBrowser().navigateTo(demoServer.getBaseUrl() + page);
    }
}
