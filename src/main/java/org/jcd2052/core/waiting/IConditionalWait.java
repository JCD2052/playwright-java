package org.jcd2052.core.waiting;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

/**
 * Defines a robust, polling-based synchronization contract for the framework.
 * <p>
 * This interface provides flexible mechanisms to halt execution until a specific
 * logical condition evaluates to {@code true}. It provides two distinct strategies:
 * <ul>
 * <li><b>Soft Waits ({@code waitFor}):</b> Returns a boolean indicating success or failure. Ideal for conditional logic.</li>
 * <li><b>Hard Waits ({@code waitForTrue}):</b> Throws a {@link TimeoutException} upon failure. Ideal for strict assertions and test prerequisites.</li>
 * </ul>
 * </p>
 */
public interface IConditionalWait {
    /**
     * Waits for the given condition to become true using default timeout and polling configurations.
     *
     * @param condition The logical condition to evaluate repeatedly.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition) {
        return this.waitFor(condition, null, null, null, null);
    }

    /**
     * Waits for the given condition to become true, providing a custom message for logging on failure.
     *
     * @param condition The logical condition to evaluate.
     * @param message   A custom message appended to the logs if the wait times out.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition, String message) {
        return this.waitFor(condition, null, null, message, null);
    }

    /**
     * Waits for the given condition, silently ignoring specific exceptions that might occur during evaluation.
     *
     * @param condition          The logical condition to evaluate.
     * @param exceptionsToIgnore A collection of exception classes to catch and ignore (e.g., StaleElement exceptions).
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition, Collection<Class<? extends Throwable>> exceptionsToIgnore) {
        return this.waitFor(condition, null, null, null, exceptionsToIgnore);
    }

    /**
     * Waits for the given condition, utilizing a custom log message and ignoring specific exceptions.
     *
     * @param condition          The logical condition to evaluate.
     * @param message            A custom message appended to the logs if the wait times out.
     * @param exceptionsToIgnore A collection of exception classes to catch and ignore.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition, String message, Collection<Class<? extends Throwable>> exceptionsToIgnore) {
        return this.waitFor(condition, null, null, message, exceptionsToIgnore);
    }

    /**
     * Waits for the given condition using a specific timeout and polling interval.
     *
     * @param condition       The logical condition to evaluate.
     * @param timeout         The maximum duration to wait.
     * @param pollingInterval The frequency at which to evaluate the condition.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition, Duration timeout, Duration pollingInterval) {
        return this.waitFor(condition, timeout, pollingInterval, null, null);
    }

    /**
     * Waits for the given condition using a specific timeout and the default polling interval.
     *
     * @param condition The logical condition to evaluate.
     * @param timeout   The maximum duration to wait.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition, Duration timeout) {
        return this.waitFor(condition, timeout, null);
    }

    /**
     * Waits for the given condition with custom timing configurations and a custom log message.
     *
     * @param condition       The logical condition to evaluate.
     * @param timeout         The maximum duration to wait.
     * @param pollingInterval The frequency at which to evaluate the condition.
     * @param message         A custom message appended to the logs if the wait times out.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition, Duration timeout, Duration pollingInterval, String message) {
        return this.waitFor(condition, timeout, pollingInterval, message, null);
    }

    /**
     * Waits for the given condition with custom timing configurations while ignoring specific exceptions.
     *
     * @param condition          The logical condition to evaluate.
     * @param timeout            The maximum duration to wait.
     * @param pollingInterval    The frequency at which to evaluate the condition.
     * @param exceptionsToIgnore A collection of exception classes to catch and ignore.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition, Duration timeout, Duration pollingInterval, Collection<Class<? extends Throwable>> exceptionsToIgnore) {
        return this.waitFor(condition, timeout, pollingInterval, null, exceptionsToIgnore);
    }

    /**
     * The core implementation signature for a soft wait.
     * <p>
     * Repeatedly evaluates the condition until it returns {@code true} or the timeout is reached.
     * Does not interrupt the test execution flow if the timeout is reached.
     * </p>
     *
     * @param condition          The logical condition to evaluate.
     * @param timeout            The maximum duration to wait (null uses framework default).
     * @param pollingInterval    The frequency at which to evaluate the condition (null uses framework default).
     * @param message            A custom message for logging purposes upon failure.
     * @param exceptionsToIgnore A collection of exception classes to silently swallow during evaluation.
     * @return {@code true} if the condition resolves to true; {@code false} if the timeout is reached.
     */
    boolean waitFor(BooleanSupplier condition, Duration timeout, Duration pollingInterval, String message, Collection<Class<? extends Throwable>> exceptionsToIgnore);

    /**
     * Strictly waits for the given condition using default configurations. Throws an exception on failure.
     *
     * @param condition The logical condition to evaluate.
     * @throws TimeoutException if the condition does not become true within the default timeout.
     */
    default void waitForTrue(BooleanSupplier condition) throws TimeoutException {
        this.waitForTrue(condition, null, null, null, null);
    }

    /**
     * Strictly waits for the given condition and appends a custom message to the exception if it fails.
     *
     * @param condition The logical condition to evaluate.
     * @param message   The context message added to the TimeoutException.
     * @throws TimeoutException if the condition does not become true within the default timeout.
     */
    default void waitForTrue(BooleanSupplier condition, String message) throws TimeoutException {
        this.waitForTrue(condition, null, null, message, null);
    }

    /**
     * Strictly waits for the given condition while silently ignoring specific exceptions during evaluation.
     *
     * @param condition          The logical condition to evaluate.
     * @param exceptionsToIgnore Exceptions to silently swallow during evaluation.
     * @throws TimeoutException if the condition does not become true within the default timeout.
     */
    default void waitForTrue(BooleanSupplier condition, Collection<Class<? extends Throwable>> exceptionsToIgnore) throws TimeoutException {
        this.waitForTrue(condition, null, null, null, exceptionsToIgnore);
    }

    /**
     * Strictly waits for the condition, utilizing a custom message and ignoring specific exceptions.
     *
     * @param condition          The logical condition to evaluate.
     * @param message            The context message added to the TimeoutException.
     * @param exceptionsToIgnore Exceptions to silently swallow during evaluation.
     * @throws TimeoutException if the condition does not become true within the default timeout.
     */
    default void waitForTrue(BooleanSupplier condition, String message, Collection<Class<? extends Throwable>> exceptionsToIgnore) throws TimeoutException {
        this.waitForTrue(condition, null, null, message, exceptionsToIgnore);
    }

    /**
     * Strictly waits for the condition using custom timing configurations.
     *
     * @param condition       The logical condition to evaluate.
     * @param timeout         The maximum duration to wait.
     * @param pollingInterval The frequency at which to evaluate the condition.
     * @throws TimeoutException if the condition does not become true within the specified timeout.
     */
    default void waitForTrue(BooleanSupplier condition, Duration timeout, Duration pollingInterval) throws TimeoutException {
        this.waitForTrue(condition, timeout, pollingInterval, null, null);
    }

    /**
     * Strictly waits for the condition using a specific timeout and default polling interval.
     *
     * @param condition The logical condition to evaluate.
     * @param timeout   The maximum duration to wait.
     * @throws TimeoutException if the condition does not become true within the specified timeout.
     */
    default void waitForTrue(BooleanSupplier condition, Duration timeout) throws TimeoutException {
        this.waitForTrue(condition, timeout, null);
    }

    /**
     * Strictly waits for the condition with custom timing configurations and a custom exception message.
     *
     * @param condition       The logical condition to evaluate.
     * @param timeout         The maximum duration to wait.
     * @param pollingInterval The frequency at which to evaluate the condition.
     * @param message         The context message added to the TimeoutException.
     * @throws TimeoutException if the condition does not become true within the specified timeout.
     */
    default void waitForTrue(BooleanSupplier condition, Duration timeout, Duration pollingInterval, String message) throws TimeoutException {
        this.waitForTrue(condition, timeout, pollingInterval, message, null);
    }

    /**
     * Strictly waits for the condition with custom timing configurations while ignoring specific exceptions.
     *
     * @param condition          The logical condition to evaluate.
     * @param timeout            The maximum duration to wait.
     * @param pollingInterval    The frequency at which to evaluate the condition.
     * @param exceptionsToIgnore Exceptions to silently swallow during evaluation.
     * @throws TimeoutException if the condition does not become true within the specified timeout.
     */
    default void waitForTrue(BooleanSupplier condition, Duration timeout, Duration pollingInterval, Collection<Class<? extends Throwable>> exceptionsToIgnore) throws TimeoutException {
        this.waitForTrue(condition, timeout, pollingInterval, null, exceptionsToIgnore);
    }

    /**
     * The core implementation signature for a hard wait.
     * <p>
     * Repeatedly evaluates the condition until it returns {@code true}. If the specified timeout
     * is exceeded, this method immediately interrupts execution by throwing an exception.
     * </p>
     *
     * @param condition          The logical condition to evaluate.
     * @param timeout            The maximum duration to wait (null uses framework default).
     * @param pollingInterval    The frequency at which to evaluate the condition (null uses framework default).
     * @param message            The context message to include in the thrown exception.
     * @param exceptionsToIgnore A collection of exception classes to silently swallow during evaluation.
     * @throws TimeoutException if the timeout duration is reached before the condition evaluates to true.
     */
    void waitForTrue(BooleanSupplier condition, Duration timeout, Duration pollingInterval, String message, Collection<Class<? extends Throwable>> exceptionsToIgnore) throws TimeoutException;
}