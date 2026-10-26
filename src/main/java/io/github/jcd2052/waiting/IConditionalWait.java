package io.github.jcd2052.waiting;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

/**
 * Polls a condition until it becomes {@code true}, for conditions an element wait can't express (e.g. "the
 * table has exactly three rows").
 * <p>
 * There are two kinds of wait:
 * </p>
 * <ul>
 * <li><b>{@code waitFor}</b> returns {@code false} if the condition isn't met in time, for conditional logic.</li>
 * <li><b>{@code waitForTrue}</b> throws a {@link TimeoutException} if it isn't, for test prerequisites.</li>
 * </ul>
 * <p>
 * Timeout and polling interval default to the values the wait was created with. The condition may throw: an
 * exception of an ignored type (or a subclass) counts as "not yet", any other is thrown out of the wait.
 * </p>
 */
public interface IConditionalWait {
    /**
     * Waits for the given condition to become true using default timeout and polling configurations.
     *
     * @param condition The condition to poll.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition) {
        return this.waitFor(condition, null, null, null, null);
    }

    /**
     * Waits for the given condition to become true, with a description of the condition.
     *
     * @param condition The condition to poll.
     * @param message   A description of the condition; not reported by this soft wait, which just returns {@code false}.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition, String message) {
        return this.waitFor(condition, null, null, message, null);
    }

    /**
     * Waits for the given condition, silently ignoring specific exceptions that might occur during evaluation.
     *
     * @param condition          The condition to poll.
     * @param exceptionsToIgnore Exception types (and their subclasses) that count as "not yet" when the condition throws them.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition, Collection<Class<? extends Throwable>> exceptionsToIgnore) {
        return this.waitFor(condition, null, null, null, exceptionsToIgnore);
    }

    /**
     * Waits for the given condition, with a description of it, treating the given exceptions as "not yet".
     *
     * @param condition          The condition to poll.
     * @param message            A description of the condition; not reported by this soft wait, which just returns {@code false}.
     * @param exceptionsToIgnore Exception types (and their subclasses) that count as "not yet" when the condition throws them.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition, String message, Collection<Class<? extends Throwable>> exceptionsToIgnore) {
        return this.waitFor(condition, null, null, message, exceptionsToIgnore);
    }

    /**
     * Waits for the given condition using a specific timeout and polling interval.
     *
     * @param condition       The condition to poll.
     * @param timeout         The maximum duration to wait.
     * @param pollingInterval How long to pause between evaluations.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition, Duration timeout, Duration pollingInterval) {
        return this.waitFor(condition, timeout, pollingInterval, null, null);
    }

    /**
     * Waits for the given condition using a specific timeout and the default polling interval.
     *
     * @param condition The condition to poll.
     * @param timeout   The maximum duration to wait.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition, Duration timeout) {
        return this.waitFor(condition, timeout, null);
    }

    /**
     * Waits for the given condition with custom timing and a description of the condition.
     *
     * @param condition       The condition to poll.
     * @param timeout         The maximum duration to wait.
     * @param pollingInterval How long to pause between evaluations.
     * @param message         A description of the condition; not reported by this soft wait, which just returns {@code false}.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition, Duration timeout, Duration pollingInterval, String message) {
        return this.waitFor(condition, timeout, pollingInterval, message, null);
    }

    /**
     * Waits for the given condition with custom timing configurations while ignoring specific exceptions.
     *
     * @param condition          The condition to poll.
     * @param timeout            The maximum duration to wait.
     * @param pollingInterval    How long to pause between evaluations.
     * @param exceptionsToIgnore Exception types (and their subclasses) that count as "not yet" when the condition throws them.
     * @return {@code true} if the condition becomes true before the timeout; {@code false} otherwise.
     */
    default boolean waitFor(BooleanSupplier condition, Duration timeout, Duration pollingInterval, Collection<Class<? extends Throwable>> exceptionsToIgnore) {
        return this.waitFor(condition, timeout, pollingInterval, null, exceptionsToIgnore);
    }

    /**
     * Polls the condition until it returns {@code true} or the timeout is reached, returning {@code false} instead
     * of throwing when it isn't met (including when the waiting thread is interrupted).
     *
     * @param condition          The condition to poll.
     * @param timeout            The maximum duration to wait ({@code null} for the default).
     * @param pollingInterval    The frequency at which to evaluate the condition ({@code null} for the default).
     * @param message            A description of the condition; not reported by this soft wait.
     * @param exceptionsToIgnore Exception types (and their subclasses) that count as "not yet"; {@code null} for none.
     * @return {@code true} if the condition became true in time; {@code false} otherwise.
     */
    boolean waitFor(
            BooleanSupplier condition,
            Duration timeout,
            Duration pollingInterval,
            String message,
            Collection<Class<? extends Throwable>> exceptionsToIgnore);

    /**
     * Strictly waits for the given condition using default configurations. Throws an exception on failure.
     *
     * @param condition The condition to poll.
     * @throws TimeoutException if the condition does not become true within the default timeout.
     */
    default void waitForTrue(BooleanSupplier condition) throws TimeoutException {
        this.waitForTrue(condition, null, null, null, null);
    }

    /**
     * Strictly waits for the given condition and appends a custom message to the exception if it fails.
     *
     * @param condition The condition to poll.
     * @param message   A description of the condition, included in the {@link TimeoutException} message.
     * @throws TimeoutException if the condition does not become true within the default timeout.
     */
    default void waitForTrue(BooleanSupplier condition, String message) throws TimeoutException {
        this.waitForTrue(condition, null, null, message, null);
    }

    /**
     * Strictly waits for the given condition while silently ignoring specific exceptions during evaluation.
     *
     * @param condition          The condition to poll.
     * @param exceptionsToIgnore Exception types (and their subclasses) that count as "not yet" when the condition throws them.
     * @throws TimeoutException if the condition does not become true within the default timeout.
     */
    default void waitForTrue(BooleanSupplier condition, Collection<Class<? extends Throwable>> exceptionsToIgnore) throws TimeoutException {
        this.waitForTrue(condition, null, null, null, exceptionsToIgnore);
    }

    /**
     * Strictly waits for the condition, utilizing a custom message and ignoring specific exceptions.
     *
     * @param condition          The condition to poll.
     * @param message            A description of the condition, included in the {@link TimeoutException} message.
     * @param exceptionsToIgnore Exception types (and their subclasses) that count as "not yet" when the condition throws them.
     * @throws TimeoutException if the condition does not become true within the default timeout.
     */
    default void waitForTrue(BooleanSupplier condition, String message, Collection<Class<? extends Throwable>> exceptionsToIgnore) throws TimeoutException {
        this.waitForTrue(condition, null, null, message, exceptionsToIgnore);
    }

    /**
     * Strictly waits for the condition using custom timing configurations.
     *
     * @param condition       The condition to poll.
     * @param timeout         The maximum duration to wait.
     * @param pollingInterval How long to pause between evaluations.
     * @throws TimeoutException if the condition does not become true within the specified timeout.
     */
    default void waitForTrue(BooleanSupplier condition, Duration timeout, Duration pollingInterval) throws TimeoutException {
        this.waitForTrue(condition, timeout, pollingInterval, null, null);
    }

    /**
     * Strictly waits for the condition using a specific timeout and default polling interval.
     *
     * @param condition The condition to poll.
     * @param timeout   The maximum duration to wait.
     * @throws TimeoutException if the condition does not become true within the specified timeout.
     */
    default void waitForTrue(BooleanSupplier condition, Duration timeout) throws TimeoutException {
        this.waitForTrue(condition, timeout, null);
    }

    /**
     * Strictly waits for the condition with custom timing configurations and a custom exception message.
     *
     * @param condition       The condition to poll.
     * @param timeout         The maximum duration to wait.
     * @param pollingInterval How long to pause between evaluations.
     * @param message         A description of the condition, included in the {@link TimeoutException} message.
     * @throws TimeoutException if the condition does not become true within the specified timeout.
     */
    default void waitForTrue(BooleanSupplier condition, Duration timeout, Duration pollingInterval, String message) throws TimeoutException {
        this.waitForTrue(condition, timeout, pollingInterval, message, null);
    }

    /**
     * Strictly waits for the condition with custom timing configurations while ignoring specific exceptions.
     *
     * @param condition          The condition to poll.
     * @param timeout            The maximum duration to wait.
     * @param pollingInterval    How long to pause between evaluations.
     * @param exceptionsToIgnore Exception types (and their subclasses) that count as "not yet" when the condition throws them.
     * @throws TimeoutException if the condition does not become true within the specified timeout.
     */
    default void waitForTrue(BooleanSupplier condition, Duration timeout, Duration pollingInterval, Collection<Class<? extends Throwable>> exceptionsToIgnore) throws TimeoutException {
        this.waitForTrue(condition, timeout, pollingInterval, null, exceptionsToIgnore);
    }

    /**
     * Polls the condition until it returns {@code true}, throwing if the timeout is reached first.
     *
     * @param condition          The condition to poll.
     * @param timeout            The maximum duration to wait ({@code null} for the default).
     * @param pollingInterval    The frequency at which to evaluate the condition ({@code null} for the default).
     * @param message            A description of the condition, included in the {@link TimeoutException} message.
     * @param exceptionsToIgnore Exception types (and their subclasses) that count as "not yet"; {@code null} for none.
     * @throws TimeoutException if the timeout is reached before the condition becomes true, or the waiting thread
     *                          is interrupted (then the cause is the {@link InterruptedException} and the thread's
     *                          interrupted flag stays set).
     */
    void waitForTrue(
            BooleanSupplier condition,
            Duration timeout,
            Duration pollingInterval,
            String message,
            Collection<Class<? extends Throwable>> exceptionsToIgnore) throws TimeoutException;
}