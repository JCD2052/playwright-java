package org.jcd2052.browser.services.interfaces;

import org.jcd2052.browser.browser.interfaces.IBrowser;
import org.jcd2052.browser.factory.IBrowserFactory;

public interface IBrowserService {
    IBrowser getBrowser();

    void setBrowser(IBrowserFactory browserFactory);

    void close();

    default void restart() {
        close();
        getBrowser();
    }
}
