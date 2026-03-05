package org.jcd2052.browser.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Configuration {
    private final BrowserProperties browserProperties;

    @Autowired
    public Configuration(BrowserProperties browserProperties) {
        this.browserProperties = browserProperties;
    }
}
