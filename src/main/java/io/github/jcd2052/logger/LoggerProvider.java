package io.github.jcd2052.logger;

public class LoggerProvider {
    private static final ILogger INSTANCE = new Logger();

    private LoggerProvider() {
    }

    public static ILogger getLogger() {
        return INSTANCE;
    }
}