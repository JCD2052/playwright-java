package org.jcd2052.core.browser.browser.interfaces;

/**
 * Manages browser storage (Local Storage or Session Storage) for a specific tab's origin.
 */
public interface IStorageManager {
    /**
     * Adds a key-value pair to the storage.
     *
     * @param key The name of the key you want to create/update.
     * @param value The value you want to give the key.
     */
    void setItem(String key, String value);

    /**
     * Retrieves the value of the specified key from the storage.
     *
     * @param key The name of the key you want to retrieve.
     * @return The string value of the key, or null if it doesn't exist.
     */
    String getItem(String key);

    /**
     * Removes the specified key from the storage.
     *
     * @param key The name of the key you want to remove.
     */
    void removeItem(String key);

    /**
     * Clears all data out of the storage for the current origin.
     */
    void clear();
}