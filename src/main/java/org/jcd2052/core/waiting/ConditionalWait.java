package org.jcd2052.core.waiting;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

/**
 * A concrete, Spring-managed implementation of the {@link IConditionalWait} interface.
 */
public class ConditionalWait implements IConditionalWait {
    private final Duration defaultTimeout;
    private final Duration defaultPollingInterval;

    /**
     * Manual constructor for instantiating the wait utility with specific time configurations.
     *
     * @param defaultTimeout         The maximum time to wait before timing out.
     * @param defaultPollingInterval The frequency of condition evaluation.
     */
    public ConditionalWait(Duration defaultTimeout, Duration defaultPollingInterval) {
        this.defaultTimeout = defaultTimeout;
        this.defaultPollingInterval = defaultPollingInterval;
    }

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
        } catch (TimeoutException timeoutException) {
            return false;
        }
    }

    @Override
    public void waitForTrue(
            BooleanSupplier condition,
            Duration timeout,
            Duration pollingInterval,
            String message,
            Collection<Class<? extends Throwable>> exceptionsToIgnore) throws TimeoutException {

        BooleanSupplier safeCondition = Optional.ofNullable(condition)
                .orElseThrow(() -> new IllegalArgumentException("Condition cannot be null"));

        Duration actualTimeout = this.resolveConditionTimeout(timeout);
        long actualPollingIntervalMillis = this.resolvePollingInterval(pollingInterval).toMillis();
        String contextMessage = this.resolveMessage(message);

        double startTimeInSeconds = this.getCurrentTimeInSeconds();

        while (!this.isConditionSatisfied(safeCondition, exceptionsToIgnore)) {
            double currentTimeInSeconds = this.getCurrentTimeInSeconds();

            if (currentTimeInSeconds - startTimeInSeconds > (double) actualTimeout.getSeconds()) {
                String exceptionMessage = String.format(
                        "Timed out after %1$s seconds during wait for condition '%2$s'",
                        actualTimeout.getSeconds(),
                        contextMessage);
                throw new TimeoutException(exceptionMessage);
            }

            try {
                Thread.sleep(actualPollingIntervalMillis);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Retrieves the current system time in seconds with high precision.
     */
    private double getCurrentTimeInSeconds() {
        return (double) System.nanoTime() / 1_000_000_000.0;
    }

    private boolean isConditionSatisfied(
            BooleanSupplier safeCondition,
            Collection<Class<? extends Throwable>> exceptionsToIgnore) {
        try {
            return safeCondition.getAsBoolean();
        } catch (Exception evaluationException) {
            if (exceptionsToIgnore != null && exceptionsToIgnore.contains(evaluationException.getClass())) {
                return false;
            } else {
                throw evaluationException;
            }
        }
    }

    private Duration resolveConditionTimeout(Duration timeout) {
        return Optional.ofNullable(timeout).orElse(this.defaultTimeout);
    }

    private Duration resolvePollingInterval(Duration pollingInterval) {
        return Optional.ofNullable(pollingInterval).orElse(this.defaultPollingInterval);
    }

    private String resolveMessage(String message) {
        return Optional.ofNullable(message).orElse("");
    }
}