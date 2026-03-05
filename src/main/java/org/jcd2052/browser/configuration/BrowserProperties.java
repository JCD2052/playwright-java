package org.jcd2052.browser.configuration;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BrowserProperties implements IBrowserProperties {
    private String name;
    private Boolean headless;
    private Long timeout;
    private Boolean highlight;
    private Boolean recordVideo;
    private Boolean tracing;
}
