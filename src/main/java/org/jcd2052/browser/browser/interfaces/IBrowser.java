package org.jcd2052.browser.browser.interfaces;

import com.microsoft.playwright.Browser;
import org.jcd2052.browser.browser.BrowserWindow;

import java.util.Collection;

public interface IBrowser {
    IBrowserWindow openNewWindow();

    Collection<IBrowserWindow> getBrowserWindows();

    String getBrowserName();

    byte[] takeScreenshot();

    Browser getBrowser();

    IBrowserWindow getCurrentBrowserWindow();

    boolean isClosed();

    void setCurrentBrowserWindow(IBrowserWindow currentBrowserWindow);

    void close();

    void switchToWindow(BrowserWindow window);
//
//    void startTracing();
//
//    void stopTracing();
}
