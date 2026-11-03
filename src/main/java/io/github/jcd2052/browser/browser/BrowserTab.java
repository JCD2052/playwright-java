package io.github.jcd2052.browser.browser;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import io.github.jcd2052.browser.browser.interfaces.IAlert;
import io.github.jcd2052.browser.browser.interfaces.IBrowserTab;
import io.github.jcd2052.browser.browser.interfaces.IKeyboardActions;
import io.github.jcd2052.browser.browser.interfaces.IMouseActions;
import io.github.jcd2052.browser.browser.interfaces.IStorageManager;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Concrete implementation of the {@link IBrowserTab} interface.
 * <p>
 * A thin, stateless view over a Playwright {@link Page}, representing a single browser tab. Page-level
 * actions (navigation, screenshots, mouse, keyboard, dialogs, ...) are performed here. Two tabs are equal
 * when they wrap the same page, so a tab kept in a variable can be switched back to later.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BrowserTab implements IBrowserTab {
    /**
     * The Playwright page this tab represents.
     */
    @Getter
    @EqualsAndHashCode.Include
    private final Page page;
    @Getter
    private final IMouseActions mouseActions;
    @Getter
    private final IKeyboardActions keyboardActions;

    /**
     * Wraps the given page.
     *
     * @param page The Playwright {@link Page} this tab represents.
     */
    public BrowserTab(Page page) {
        this.page = page;
        this.mouseActions = new MouseActions(page.mouse());
        this.keyboardActions = new KeyboardActions(page.keyboard());
    }

    @Override
    public IAlert handleNextAlert(Consumer<IAlert> alertHandler, Runnable actionThatTriggersAlert) {
        AtomicReference<IAlert> handledAlert = new AtomicReference<>();
        AtomicReference<RuntimeException> handlerFailure = new AtomicReference<>();
        Consumer<Dialog> listener = dialog -> {
            if (handledAlert.get() != null) {
                dialog.dismiss();
                return;
            }
            IAlert alert = new BrowserAlert(dialog);
            try {
                alertHandler.accept(alert);
            } catch (RuntimeException e) {
                handlerFailure.set(e);
                try {
                    dialog.dismiss();
                } catch (PlaywrightException alreadyHandled) {
                    e.addSuppressed(alreadyHandled);
                }
            }
            handledAlert.set(alert);
        };
        page.onDialog(listener);
        try {
            actionThatTriggersAlert.run();
            page.waitForCondition(() -> handledAlert.get() != null);
            if (handlerFailure.get() != null) {
                throw handlerFailure.get();
            }
            return handledAlert.get();
        } finally {
            page.offDialog(listener);
        }
    }

    @Override
    public IStorageManager getLocalStorage() {
        return new StorageManager(page.localStorage(), "Local Storage");
    }

    @Override
    public IStorageManager getSessionStorage() {
        return new StorageManager(page.sessionStorage(), "Session Storage");
    }

    @Override
    public String readClipboard() {
        Object result = evaluate("async () => await navigator.clipboard.readText()");
        return result != null ? result.toString() : "";
    }
}