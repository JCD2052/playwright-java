package org.jcd2052.appconfiguration;

import org.jcd2052.browser.configuration.BrowserProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"org.jcd2052"})
public class AppConfiguration {

    @Bean
    public BrowserProperties browserProperties() {
        return new BrowserProperties().setHeadless(false).setName("chrome").setTracing(true).setRecordVideo(false).setTimeout(30000L);
    }
}
