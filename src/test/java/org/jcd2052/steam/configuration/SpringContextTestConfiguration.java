package org.jcd2052.steam.configuration;

import org.jcd2052.core.browser.configuration.BrowserProperties;
import org.jcd2052.core.browser.configuration.IBrowserProperties;
import org.jcd2052.core.browser.factory.BrowserFactory;
import org.jcd2052.core.browser.factory.IBrowserFactory;
import org.jcd2052.core.browser.launcher.BrowserLauncherRegistry;
import org.jcd2052.core.browser.launcher.IBrowserLauncherRegistry;
import org.jcd2052.core.browser.services.BrowserService;
import org.jcd2052.core.browser.services.ElementFactory;
import org.jcd2052.core.browser.services.ElementFinderService;
import org.jcd2052.core.browser.services.interfaces.IBrowserService;
import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.browser.services.interfaces.IElementFinderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;

@Configuration
@PropertySource("classpath:application.properties")
@PropertySource(
        value = "classpath:application-${spring.profiles.active:default}.properties",
        ignoreResourceNotFound = true)
@ComponentScan(basePackages = {"org.jcd2052"})
public class SpringContextTestConfiguration {
    /**
     * Indicates whether the browser should run without a visible UI.
     */
    @Value("${playwright.browser.headless}")
    private boolean isHeadless;
    /**
     * The name of the browser engine to launch (e.g., "chrome", "firefox").
     */
    @Value("${playwright.browser.name}")
    private String browserName;
    /**
     * Indicates whether Playwright tracing should be enabled for test sessions.
     */
    @Value("${playwright.browser.tracing}")
    private boolean isTracingEnabled;
    /**
     * The maximum timeout duration in milliseconds for Playwright operations.
     */
    @Value("${playwright.browser.timeout}")
    private long timeout;
    /**
     * The directory path where Playwright trace ZIP files will be saved.
     */
    @Value("${playwright.browser.tracing.folder}")
    private String tracingFolder;
    /**
     * Indicates whether elements should be visually highlighted when interacted with.
     */
    @Value("${playwright.browser.highlight}")
    private boolean highlight;
    /**
     * Indicates whether screenshots should be captured during execution.
     */
    @Value("${playwright.browser.screenshots}")
    private boolean screenshots;
    /**
     * Indicates whether DOM snapshots should be captured during execution.
     */
    @Value("${playwright.browser.snapshots}")
    private boolean snapshots;
    /**
     * The initial width of the browser viewport in pixels.
     */
    @Value("${playwright.browser.viewport.width}")
    private Integer width;
    /**
     * The initial height of the browser viewport in pixels.
     */
    @Value("${playwright.browser.viewport.height}")
    private Integer height;
    /**
     * The maximum timeout duration in milliseconds for page loading.
     */
    @Value("${playwright.browser.page.load.timeout}")
    private long pageLoadTimeout;
    @Value("${playwright.browser.tracing.args}")
    private String args;

    @Bean
    public IBrowserLauncherRegistry browserLauncherRegistry() {
        return new BrowserLauncherRegistry();
    }

    @Bean
    public IBrowserFactory browserFactory(IBrowserProperties browserProperties, IBrowserLauncherRegistry registry) {
        return new BrowserFactory(browserProperties, registry);
    }

    @Bean
    public IElementFactory elementFactory(
            IElementFinderService elementFinderService,
            IBrowserProperties browserProperties) {
        return new ElementFactory(elementFinderService, browserProperties);
    }

    @Bean
    public IElementFinderService elementFinderService(IBrowserService browserService) {
        return new ElementFinderService(browserService);
    }

    @Bean
    public IBrowserService browserService(IBrowserProperties browserProperties, IBrowserFactory browserFactory) {
        return new BrowserService(browserProperties, browserFactory);
    }

    @Bean
    public IBrowserProperties browserProperties() {
        return new BrowserProperties()
                .setHeadless(isHeadless)
                .setName(browserName)
                .setTracing(isTracingEnabled)
                .setTimeout(timeout)
                .setPageLoadTimeout(pageLoadTimeout)
                .setWidth(width)
                .setHeight(height)
                .setTracingSaveFolder(tracingFolder)
                .setHighlight(highlight)
                .setScreenshots(screenshots)
                .setSnapshots(snapshots)
                .setArgs(Arrays.stream(args.split(",")).toList());
    }
}
