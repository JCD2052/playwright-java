package io.github.jcd2052.demo.tests;

import io.github.jcd2052.browser.configuration.IBrowserProperties;
import io.github.jcd2052.demo.support.JsonBrowserProperties;

/**
 * Runs the demo without Spring, with the browser settings read from {@code browser-properties.json} on the
 * test classpath.
 */
public class JsonPropertiesDemoTests extends WithoutSpringDemoTests {
    /**
     * Reads the settings from {@code src/test/resources/browser-properties.json}.
     */
    @Override
    protected IBrowserProperties createBrowserProperties() {
        return JsonBrowserProperties.load("browser-properties.json");
    }
}
