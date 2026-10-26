package io.github.jcd2052.demo.support;

import io.github.jcd2052.browser.configuration.IBrowserProperties;

import java.util.List;

/**
 * Browser settings written directly in code, as an example of implementing {@link IBrowserProperties} without
 * any configuration source: each method returns a fixed value, and {@code null} or {@code false} leaves an
 * optional setting off.
 */
public class HardcodedBrowserProperties implements IBrowserProperties {
    @Override
    public String getName() {
        return "chromium";
    }

    @Override
    public String getTracingSaveFolder() {
        return "target/tracing";
    }

    @Override
    public Long getTimeout() {
        return 5000L;
    }

    @Override
    public Long getPageLoadTimeout() {
        return 10000L;
    }

    @Override
    public Integer getWidth() {
        return 1024;
    }

    @Override
    public Integer getHeight() {
        return 768;
    }

    @Override
    public boolean isHeadless() {
        return true;
    }

    @Override
    public boolean isHighlight() {
        return false;
    }

    @Override
    public boolean isTracing() {
        return false;
    }

    @Override
    public boolean isTracingScreenshotsEnabled() {
        return false;
    }

    @Override
    public boolean isTracingSnapshotsEnabled() {
        return false;
    }

    @Override
    public List<String> getArgs() {
        return List.of();
    }

    @Override
    public String getTestIdAttribute() {
        return null;
    }

    @Override
    public String getLocale() {
        return "en-GB";
    }

    @Override
    public String getTimezoneId() {
        return "Europe/London";
    }

    @Override
    public Double getGeolocationLatitude() {
        return null;
    }

    @Override
    public Double getGeolocationLongitude() {
        return null;
    }

    @Override
    public List<String> getPermissions() {
        return List.of();
    }

    @Override
    public String getUserAgent() {
        return null;
    }

    @Override
    public Double getDeviceScaleFactor() {
        return null;
    }

    @Override
    public boolean isMobile() {
        return false;
    }

    @Override
    public boolean isHasTouch() {
        return false;
    }

    @Override
    public String getStorageStatePath() {
        return null;
    }
}
