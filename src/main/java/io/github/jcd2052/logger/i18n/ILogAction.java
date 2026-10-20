package io.github.jcd2052.logger.i18n;

public interface ILogAction {
    String getKey();

    /**
     * Helper to quickly create an ad-hoc action key without creating a new enum.
     */
    static ILogAction of(String key) {
        return () -> key;
    }
}
