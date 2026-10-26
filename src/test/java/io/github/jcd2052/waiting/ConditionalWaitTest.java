package io.github.jcd2052.waiting;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit tests for {@link ConditionalWait}. Uses short timeouts/polling intervals throughout so the
 * suite stays fast even though this is genuinely exercising real {@code Thread.sleep} polling.
 */
public class ConditionalWaitTest {
    /**
     * A wait with a 300 ms timeout and a 20 ms polling interval, short enough to keep the suite fast.
     */
    private static ConditionalWait shortWait() {
        return new ConditionalWait(Duration.ofMillis(300), Duration.ofMillis(20));
    }

    /**
     * Verifies that waiting for a condition that is already true returns without throwing.
     */
    @Test(description = "waitForTrue returns immediately when the condition is already true")
    public void waitForTrueReturnsImmediatelyWhenConditionIsAlreadyTrue() throws TimeoutException {
        shortWait().waitForTrue(() -> true);
    }

    /**
     * Verifies that a condition that never becomes true fails with a {@link TimeoutException} whose message includes
     * the given context.
     */
    @Test(description = "waitForTrue throws with a context message when the condition never becomes true")
    public void waitForTrueThrowsWithAContextMessageWhenTheConditionNeverBecomesTrue() {
        TimeoutException exception = Assert.expectThrows(TimeoutException.class, () ->
                shortWait().waitForTrue(() -> false, "row count reached zero"));

        Assert.assertTrue(exception.getMessage().contains("row count reached zero"));
    }

    /**
     * Verifies that the condition is polled until it becomes true.
     */
    @Test(description = "waitForTrue succeeds once the condition flips to true during polling")
    public void waitForTrueSucceedsOnceTheConditionFlipsToTrueDuringPolling() throws TimeoutException {
        AtomicInteger callCount = new AtomicInteger(0);

        shortWait().waitForTrue(() -> callCount.incrementAndGet() >= 3);

        Assert.assertTrue(callCount.get() >= 3);
    }

    /**
     * Verifies that the boolean variant returns {@code true} when the condition is met.
     */
    @Test(description = "waitFor returns true when the condition is met")
    public void waitForReturnsTrueWhenTheConditionIsMet() {
        Assert.assertTrue(shortWait().waitFor(() -> true));
    }

    /**
     * Verifies that the boolean variant returns {@code false}, instead of throwing, when the condition times out.
     */
    @Test(description = "waitFor returns false instead of throwing when the condition times out")
    public void waitForReturnsFalseInsteadOfThrowingWhenTheConditionTimesOut() {
        Assert.assertFalse(shortWait().waitFor(() -> false));
    }

    /**
     * Verifies that a {@code null} condition is rejected with an {@link IllegalArgumentException}.
     */
    @Test(description = "waitForTrue rejects a null condition")
    public void waitForTrueRejectsANullCondition() {
        Assert.expectThrows(IllegalArgumentException.class, () -> shortWait().waitForTrue(null));
    }

    /**
     * Verifies that an exception of an ignored type thrown by the condition counts as "not yet" and the condition is
     * evaluated again.
     */
    @Test(description = "waitForTrue retries when an ignored exception is thrown during evaluation")
    public void waitForTrueRetriesWhenAnIgnoredExceptionIsThrownDuringEvaluation() throws TimeoutException {
        AtomicInteger callCount = new AtomicInteger(0);

        shortWait().waitForTrue(
                () -> {
                    if (callCount.incrementAndGet() < 3) {
                        throw new IllegalStateException("element not ready yet");
                    }
                    return true;
                },
                null,
                null,
                "condition with a flaky evaluation",
                List.of(IllegalStateException.class));

        Assert.assertTrue(callCount.get() >= 3);
    }

    /**
     * Verifies that an exception of a type that is not ignored is thrown straight out of the wait.
     */
    @Test(description = "waitForTrue propagates an exception that is not in the ignore list")
    public void waitForTruePropagatesAnExceptionThatIsNotInTheIgnoreList() {
        Assert.expectThrows(IllegalStateException.class, () -> shortWait().waitForTrue(
                () -> {
                    throw new IllegalStateException("boom");
                },
                null,
                null,
                "unignored exception",
                List.of(NullPointerException.class)));
    }

    /**
     * Verifies that subclasses of an ignored exception type are ignored too.
     */
    @Test(description = "waitForTrue ignores subclasses of an ignored exception")
    public void waitForTrueIgnoresSubclassesOfAnIgnoredException() throws TimeoutException {
        AtomicInteger callCount = new AtomicInteger(0);

        shortWait().waitForTrue(
                () -> {
                    if (callCount.incrementAndGet() < 3) {
                        throw new IllegalStateException("element not ready yet");
                    }
                    return true;
                },
                null,
                null,
                "condition with a flaky evaluation",
                List.of(RuntimeException.class));

        Assert.assertTrue(callCount.get() >= 3);
    }

    /**
     * Verifies that interrupting the waiting thread stops the wait with a {@link TimeoutException} caused by the
     * interruption, after a single evaluation, and keeps the thread's interrupted flag set.
     */
    @Test(description = "waitForTrue stops polling when the thread is interrupted")
    public void waitForTrueStopsPollingWhenTheThreadIsInterrupted() {
        AtomicInteger callCount = new AtomicInteger(0);
        Thread.currentThread().interrupt();
        try {
            TimeoutException exception = Assert.expectThrows(TimeoutException.class, () -> shortWait().waitForTrue(
                    () -> {
                        callCount.incrementAndGet();
                        return false;
                    },
                    "interrupted wait"));

            Assert.assertTrue(exception.getCause() instanceof InterruptedException);
            Assert.assertEquals(callCount.get(), 1);
            Assert.assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    /**
     * Verifies that a timeout below one second is waited out in full instead of being truncated to zero.
     */
    @Test(description = "Sub-second timeouts are honored rather than truncated to zero")
    public void subSecondTimeoutsAreHonoredRatherThanTruncatedToZero() {
        long start = System.currentTimeMillis();

        Assert.expectThrows(TimeoutException.class, () -> shortWait().waitForTrue(
                () -> false, Duration.ofMillis(150), Duration.ofMillis(10), "sub-second timeout", null));

        long elapsed = System.currentTimeMillis() - start;
        Assert.assertTrue(elapsed >= 150, "Expected to wait at least 150ms, but timed out after " + elapsed + "ms");
    }

    /**
     * Verifies that a timeout and polling interval given to a single wait override the wait's defaults.
     */
    @Test(description = "An explicit timeout and polling interval override the defaults")
    public void explicitTimeoutAndPollingIntervalOverrideTheDefaults() {
        AtomicInteger callCount = new AtomicInteger(0);

        TimeoutException exception = Assert.expectThrows(TimeoutException.class, () -> shortWait().waitForTrue(
                () -> {
                    callCount.incrementAndGet();
                    return false;
                },
                Duration.ofMillis(60),
                Duration.ofMillis(10),
                "overridden timing",
                null));

        Assert.assertTrue(exception.getMessage().contains("60 ms"));
        Assert.assertTrue(callCount.get() > 1);
    }
}
