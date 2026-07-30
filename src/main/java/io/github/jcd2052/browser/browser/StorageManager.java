package io.github.jcd2052.browser.browser;

import com.microsoft.playwright.WebStorage;
import io.github.jcd2052.browser.browser.interfaces.IStorageManager;
import io.github.jcd2052.logger.LoggerProvider;

/**
 * Default {@link IStorageManager} implementation, wrapping Playwright's {@link WebStorage} for
 * either {@code localStorage} or {@code sessionStorage}, depending on how it was constructed.
 */
public class StorageManager implements IStorageManager {
    private final WebStorage webStorage;
    private final String storageType;

    /**
     * Wraps the given web storage.
     *
     * @param webStorage  the Playwright {@link WebStorage} instance to delegate to
     * @param storageType a human-readable label for this storage (e.g. {@code "localStorage"}), used in log messages
     */
    public StorageManager(WebStorage webStorage, String storageType) {
        this.webStorage = webStorage;
        this.storageType = storageType;
    }

    @Override
    public void setItem(String key, String value) {
        LoggerProvider.getLogger().info("Setting %s item: ['%s' = '%s']", storageType, key, value);
        webStorage.setItem(key, value);
    }

    @Override
    public String getItem(String key) {
        LoggerProvider.getLogger().info("Getting %s item: '%s'", storageType, key);
        return webStorage.getItem(key);
    }

    @Override
    public void removeItem(String key) {
        LoggerProvider.getLogger().info("Removing %s item: '%s'", storageType, key);
        webStorage.removeItem(key);
    }

    @Override
    public void clear() {
        LoggerProvider.getLogger().info("Clearing all data from %s", storageType);
        webStorage.clear();
    }
}