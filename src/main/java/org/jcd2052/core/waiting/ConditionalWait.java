package org.jcd2052.core.waiting;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

/**
 * A concrete, Spring-managed implementation of the {@link IConditionalWait} interface.
 * <p>
 * This class provides the core polling engine for the framework's synchronization layer.
 * It utilizes high-precision time tracking via {@link System#nanoTime()} and manages
 * the thread sleeping mechanics required to repeatedly evaluate UI conditions without
 * overwhelming the CPU.
 * </p>
 */
public class ConditionalWait implements IConditionalWait {
    /**
     * Executes a "soft wait" by wrapping the strict polling engine in a try-catch block.
     * <p>
     * Instead of halting test execution upon a timeout, this method suppresses the
     * {@link TimeoutException} and gracefully returns {@code false}, allowing the test
     * script to handle the failure logically (e.g., in {@code if/else} statements).
     * </p>
     *
     * @param condition          The logical condition to evaluate.
     * @param timeout            The maximum duration to wait (falls back to default if null).
     * @param pollingInterval    The frequency at which to evaluate the condition (falls back to default if null).
     * @param message            A custom message for logging purposes.
     * @param exceptionsToIgnore A collection of exception classes to silently swallow during evaluation.
     * @return {@code true} if the condition is met within the timeout; {@code false} otherwise.
     */
    @Override
    public boolean waitFor(
            BooleanSupplier condition,
            Duration timeout,
            Duration pollingInterval,
            String message,
            Collection<Class<? extends Throwable>> exceptionsToIgnore) {
        try {
            this.waitForTrue(condition, timeout, pollingInterval, message, exceptionsToIgnore);
            return true;
        } catch (TimeoutException var7) {
            return false;
        }
    }

    /**
     * The core "hard wait" polling loop.
     * <p>
     * This method repeatedly executes the provided condition. It calculates the elapsed time
     * during each iteration and puts the current thread to sleep for the specified polling
     * interval between checks. If the total elapsed time exceeds the timeout duration,
     * it actively fails the test step by throwing an exception.
     * </p>
     *
     * @param condition          The logical condition to evaluate.
     * @param timeout            The maximum duration to wait.
     * @param pollingInterval    The frequency at which to evaluate the condition.
     * @param message            A context message included in the exception if a timeout occurs.
     * @param exceptionsToIgnore A collection of exception classes to silently swallow.
     * @throws TimeoutException         if the condition evaluates to false for the entire timeout duration.
     * @throws IllegalArgumentException if the provided condition is null.
     */
    @Override
    public void waitForTrue(
            BooleanSupplier condition,
            Duration timeout,
            Duration pollingInterval,
            String message,
            Collection<Class<? extends Throwable>> exceptionsToIgnore) throws TimeoutException {

        BooleanSupplier supplier = Optional.ofNullable(condition)
                .orElseThrow(() -> new IllegalArgumentException("Condition cannot be null"));

        Duration currentTimeout = this.resolveConditionTimeout(timeout);
        long pollingIntervalInMilliseconds = this.resolvePollingInterval(pollingInterval).toMillis();
        String exMessage = this.resolveMessage(message);
        double startTime = this.getCurrentTime();

        while (!this.isConditionSatisfied(supplier, exceptionsToIgnore)) {
            double currentTime = this.getCurrentTime();
            if (currentTime - startTime > (double) currentTimeout.getSeconds()) {
                String exceptionMessage = String.format(
                        "Timed out after %1$s seconds during wait for condition '%2$s'",
                        currentTimeout.getSeconds(),
                        exMessage);
                throw new TimeoutException(exceptionMessage);
            }

            try {
                Thread.sleep(pollingIntervalInMilliseconds);
            } catch (InterruptedException var16) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Retrieves the current system time in seconds with high precision.
     *
     * @return The current time in seconds, calculated from system nanoseconds.
     */
    private double getCurrentTime() {
        return (double) System.nanoTime() / Math.pow(10.0F, 9.0F);
    }

    /**
     * Evaluates the boolean condition while safely handling potential runtime exceptions.
     * <p>
     * If an exception is thrown during evaluation (e.g., a Playwright element detached from the DOM),
     * this method checks if it belongs to the ignored list. If ignored, it treats the condition
     * as temporarily {@code false} to allow another polling attempt. If not ignored, the exception
     * is immediately re-thrown, breaking the wait loop.
     * </p>
     *
     * @param condition          The logical condition to execute.
     * @param exceptionsToIgnore The list of exceptions that should not interrupt the polling loop.
     * @return {@code true} if the condition succeeds; {@code false} if it evaluates to false or throws an ignored exception.
     */
    private boolean isConditionSatisfied(
            BooleanSupplier condition,
            Collection<Class<? extends Throwable>> exceptionsToIgnore) {
        try {
            return condition.getAsBoolean();
        } catch (Exception var4) {
            if (exceptionsToIgnore != null && exceptionsToIgnore.contains(var4.getClass())) {
                return false;
            } else {
                throw var4;
            }
        }
    }

    /**
     * Resolves the maximum wait duration, providing a framework-wide default if null is passed.
     *
     * @param timeout The requested timeout duration.
     * @return The provided timeout, or a default of 30 seconds.
     */
    private Duration resolveConditionTimeout(Duration timeout) {
        return Optional.ofNullable(timeout).orElse(Duration.ofSeconds(30));
    }

    /**
     * Resolves the polling frequency, providing a framework-wide default if null is passed.
     *
     * @param pollingInterval The requested polling interval.
     * @return The provided interval, or a default of 500 milliseconds.
     */
    private Duration resolvePollingInterval(Duration pollingInterval) {
        return Optional.ofNullable(pollingInterval).orElse(Duration.ofMillis(500));
    }

    /**
     * Safely resolves the context message to avoid null pointer exceptions during string formatting.
     *
     * @param message The requested context message.
     * @return The provided message, or an empty string if null or empty.
     */
    private String resolveMessage(String message) {
        return Optional.ofNullable(message).orElse("");
    }
}