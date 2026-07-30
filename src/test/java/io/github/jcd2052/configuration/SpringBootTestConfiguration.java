package io.github.jcd2052.configuration;

import io.github.jcd2052.browser.configuration.BrowserProperties;
import io.github.jcd2052.browser.configuration.IBrowserProperties;
import io.github.jcd2052.browser.factory.BrowserFactory;
import io.github.jcd2052.browser.factory.IBrowserFactory;
import io.github.jcd2052.browser.launcher.BrowserLauncherRegistry;
import io.github.jcd2052.browser.launcher.IBrowserLauncherRegistry;

import io.github.jcd2052.browser.services.BrowserService;
import io.github.jcd2052.browser.services.ElementFactory;
import io.github.jcd2052.browser.services.ElementFinderService;
import io.github.jcd2052.browser.services.interfaces.IBrowserService;
import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.browser.services.interfaces.IElementFinderService;
import io.github.jcd2052.waiting.ConditionalWait;
import io.github.jcd2052.waiting.IConditionalWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan(basePackages = {"io.github.jcd2052"})
public class SpringBootTestConfiguration {
    @Value("${playwright.browser.headless:true}")
    private boolean isHeadless;
    @Value("${playwright.browser.name:chrome}")
    private String browserName;
    @Value("${playwright.browser.tracing:false}")
    private boolean isTracingEnabled;
    @Value("${playwright.browser.timeout:30000}")
    private long timeout;
    @Value("${playwright.browser.tracing.folder:target/tracing}")
    private String tracingFolder;
    @Value("${playwright.browser.highlight:false}")
    private boolean highlight;
    @Value("${playwright.browser.screenshots:true}")
    private boolean screenshots;
    @Value("${playwright.browser.snapshots:true}")
    private boolean snapshots;
    @Value("${playwright.browser.viewport.width:1600}")
    private Integer width;
    @Value("${playwright.browser.viewport.height:900}")
    private Integer height;
    @Value("${playwright.browser.page.load.timeout:30000}")
    private long pageLoadTimeout;
    @Value("${playwright.browser.tracing.args:}")
    private String args;
    @Value("${playwright.browser.testid.attribute:}")
    private String testIdAttribute;
    @Value("${playwright.browser.locale:}")
    private String locale;
    @Value("${playwright.browser.timezone:}")
    private String timezoneId;
    @Value("${playwright.browser.geolocation.latitude:#{null}}")
    private Double geolocationLatitude;
    @Value("${playwright.browser.geolocation.longitude:#{null}}")
    private Double geolocationLongitude;
    @Value("${playwright.browser.permissions:}")
    private String permissions;
    @Value("${playwright.browser.user-agent:}")
    private String userAgent;
    @Value("${playwright.browser.device.scale.factor:#{null}}")
    private Double deviceScaleFactor;
    @Value("${playwright.browser.mobile:false}")
    private boolean mobile;
    @Value("${playwright.browser.has.touch:false}")
    private boolean hasTouch;
    @Value("${playwright.browser.storage.state.path:}")
    private String storageStatePath;

    @Bean
    public IBrowserLauncherRegistry browserLauncherRegistry() {
        return new BrowserLauncherRegistry();
    }

    @Bean
    public IBrowserFactory<IBrowserProperties> browserFactory(
            IBrowserProperties browserProperties, IBrowserLauncherRegistry registry) {
        return new BrowserFactory<>(browserProperties, registry);
    }

    @Bean
    public IElementFactory elementFactory(
            IElementFinderService elementFinderService,
            IBrowserProperties browserProperties) {
        return new ElementFactory(elementFinderService, browserProperties);
    }

    @Bean
    public IElementFinderService elementFinderService(IBrowserService<IBrowserProperties> browserService) {
        return new ElementFinderService(browserService);
    }

    @Bean
    public IBrowserService<IBrowserProperties> browserService(
            IBrowserProperties browserProperties, IBrowserFactory<IBrowserProperties> browserFactory) {
        return new BrowserService<>(browserProperties, browserFactory);
    }

    @Bean
    public IConditionalWait conditionalWait() {
        return new ConditionalWait(Duration.ofMillis(timeout), Duration.ofMillis(500));
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
                .setArgs(parseCsv(args))
                .setTestIdAttribute(testIdAttribute)
                .setLocale(locale)
                .setTimezoneId(timezoneId)
                .setGeolocationLatitude(geolocationLatitude)
                .setGeolocationLongitude(geolocationLongitude)
                .setPermissions(parseCsv(permissions))
                .setUserAgent(userAgent)
                .setDeviceScaleFactor(deviceScaleFactor)
                .setMobile(mobile)
                .setHasTouch(hasTouch)
                .setStorageStatePath(storageStatePath);
    }

    /**
     * Splits a comma-separated property value into a trimmed, blank-filtered list
     * (e.g. browser launch args or granted permissions).
     *
     * @param csv the raw comma-separated value, possibly {@code null} or empty
     * @return a list of trimmed, non-blank entries; empty if {@code csv} has none
     */
    private static List<String> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .toList();
    }
}
