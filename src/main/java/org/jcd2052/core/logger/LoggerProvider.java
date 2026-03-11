package org.jcd2052.core.logger;

public class LoggerProvider {
    private static final ILogger INSTANCE = new Logger();

    private LoggerProvider() {
    }

    public static ILogger getLogger() {
        return INSTANCE;
    }
}