package org.jcd2052.core.logger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Logger implements ILogger {
    @Override
    public void debugElementAction(String elementType, String elementName, String message, Object... args) {
        String actionMessage = formatMessage(message, args);
        debug("{} '{}' {}", elementType, elementName, actionMessage);
    }

    @Override
    public void info(String message, Object... args) {
        log.info(formatMessage(message, args));
    }

    @Override
    public void debug(String message, Object... args) {
        log.debug(formatMessage(message, args));
    }

    @Override
    public void debug(String message, Throwable throwable, Object... args) {
        log.debug(formatMessage(message, args), throwable);
    }

    @Override
    public void warn(String message, Object... args) {
        log.warn(formatMessage(message, args));
    }

    @Override
    public void error(String message, Object... args) {
        log.error(formatMessage(message, args));
    }

    @Override
    public void fatal(String message, Throwable throwable, Object... args) {
        log.error("FATAL: {}", formatMessage(message, args), throwable);
    }

    private String formatMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        try {
            return String.format(message, args);
        } catch (Exception e) {
            return message + " [Format Error]";
        }
    }

}