package io.github.jcd2052.logger;

/**
 * Static access point for the framework's single, shared {@link ILogger} instance.
 */
public class LoggerProvider {
    private static final ILogger INSTANCE = new Logger();

    private LoggerProvider() {
    }

    /**
     * The framework's shared logger instance.
     *
     * @return the singleton {@link ILogger}
     */
    public static ILogger getLogger() {
        return INSTANCE;
    }
}