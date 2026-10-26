package io.github.jcd2052.browser.browser;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import io.github.jcd2052.browser.browser.interfaces.IBrowserTab;
import io.github.jcd2052.browser.browser.interfaces.IBrowserWindow;
import io.github.jcd2052.browser.browser.interfaces.ICookieManager;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete implementation of the {@link IBrowserWindow} interface.
 * <p>
 * Wraps a Playwright {@link BrowserContext}. Its tabs are always read from {@link BrowserContext#pages()};
 * the only state kept here is which tab is current. Two windows are equal when they wrap the same context,
 * so a window kept in a variable can be switched back to later.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BrowserWindow implements IBrowserWindow {
    /**
     * The underlying Playwright BrowserContext associated with this window.
     */
    @Getter
    @EqualsAndHashCode.Include
    private final BrowserContext browserContext;
    /**
     * The current tab, or {@code null} until one is opened or switched to.
     */
    private Page currentPage;

    /**
     * Wraps the given context.
     *
     * @param browserContext The Playwright {@link BrowserContext} this window represents.
     */
    public BrowserWindow(BrowserContext browserContext) {
        this.browserContext = browserContext;
    }

    /**
     * Opens a new tab in this window and makes it the current tab.
     *
     * @return The newly opened {@link IBrowserTab}.
     */
    @Override
    public IBrowserTab openNewTab() {
        currentPage = browserContext.newPage();
        return new BrowserTab(currentPage);
    }

    @Override
    public void closeTab(IBrowserTab tab) {
        tab.getPage().close();
    }

    /**
     * Retrieves all currently open tabs within this window, oldest first, including popups opened by its
     * pages.
     *
     * @return A list of {@link IBrowserTab}s.
     */
    @Override
    public List<IBrowserTab> getBrowserTabs() {
        return browserContext.pages().stream().map(BrowserTab::new).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * If no tab has been made current yet, or the current tab was closed, this window's newest tab
     * becomes current.
     */
    @Override
    public IBrowserTab getCurrentBrowserTab() {
        if (currentPage == null || currentPage.isClosed()) {
            List<Page> pages = browserContext.pages();
            currentPage = pages.isEmpty() ? null : pages.get(pages.size() - 1);
        }
        return currentPage == null ? null : new BrowserTab(currentPage);
    }

    /**
     * Brings the given tab to the front and makes it the current tab.
     *
     * @param tab The {@link IBrowserTab} to switch to.
     */
    @Override
    public void switchToTab(IBrowserTab tab) {
        tab.getPage().bringToFront();
        currentPage = tab.getPage();
    }

    @Override
    public ICookieManager getCookieManager() {
        return new CookieManager(browserContext);
    }

    @Override
    public void closeWindow() {
        browserContext.close();
    }
}
