package io.github.jcd2052.logger;

/**
 * Framework-level logging abstraction over SLF4J, used throughout the element and browser layers
 * for consistent, printf-style ({@code %s}) log formatting.
 */
public interface ILogger {
    /**
     * Logs a debug-level message describing an action performed on a specific element.
     *
     * @param elementType the element's type (e.g. {@code "Button"})
     * @param elementName the element's human-readable name
     * @param message     a printf-style message describing the action, e.g. {@code "was clicked"}
     * @param args        arguments to interpolate into {@code message}
     */
    void debugElementAction(String elementType, String elementName, String message, Object... args);

    /**
     * Logs an info-level message.
     *
     * @param message a printf-style message
     * @param args    arguments to interpolate into {@code message}
     */
    void info(String message, Object... args);

    /**
     * Logs a debug-level message.
     *
     * @param message a printf-style message
     * @param args    arguments to interpolate into {@code message}
     */
    void debug(String message, Object... args);

    /**
     * Logs a debug-level message together with an associated exception/stack trace.
     *
     * @param message   a printf-style message
     * @param throwable the exception to log alongside the message
     * @param args      arguments to interpolate into {@code message}
     */
    void debug(String message, Throwable throwable, Object... args);

    /**
     * Logs a warning-level message.
     *
     * @param message a printf-style message
     * @param args    arguments to interpolate into {@code message}
     */
    void warn(String message, Object... args);

    /**
     * Logs an error-level message.
     *
     * @param message a printf-style message
     * @param args    arguments to interpolate into {@code message}
     */
    void error(String message, Object... args);

    /**
     * Logs a fatal-level message together with an associated exception/stack trace.
     *
     * @param message   a printf-style message
     * @param throwable the exception to log alongside the message
     * @param args      arguments to interpolate into {@code message}
     */
    void fatal(String message, Throwable throwable, Object... args);
}