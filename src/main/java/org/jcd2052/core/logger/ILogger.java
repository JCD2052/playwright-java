package org.jcd2052.core.logger;

public interface ILogger {
    void debugElementAction(String elementType, String elementName, String message, Object... args);

    void info(String message, Object... args);

    void debug(String message, Object... args);

    void debug(String message, Throwable throwable, Object... args);

    void warn(String message, Object... args);

    void error(String message, Object... args);

    void fatal(String message, Throwable throwable, Object... args);
}