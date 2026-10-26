package io.github.jcd2052.demo.tests;

import io.github.jcd2052.browser.configuration.IBrowserProperties;
import io.github.jcd2052.demo.support.HardcodedBrowserProperties;

/**
 * Runs the demo without Spring, with the browser settings written directly in code.
 */
public class HardcodedPropertiesDemoTests extends WithoutSpringDemoTests {
    /**
     * Uses the fixed values of {@link HardcodedBrowserProperties}.
     */
    @Override
    protected IBrowserProperties createBrowserProperties() {
        return new HardcodedBrowserProperties();
    }
}
