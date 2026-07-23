package org.jcd2052.core.waiting;

import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

/**
 * Unit tests for {@link ConditionalWait}. Uses short timeouts/polling intervals throughout so the
 * suite stays fast even though this is genuinely exercising real {@code Thread.sleep} polling.
 */
public class ConditionalWaitTest {

    private static ConditionalWait shortWait() {
        return new ConditionalWait(Duration.ofMillis(300), Duration.ofMillis(20));
    }

    @Test
    public void waitForTrueReturnsImmediatelyWhenConditionIsAlreadyTrue() throws TimeoutException {
        shortWait().waitForTrue(() -> true);
        // no exception = success
    }

    @Test
    public void waitForTrueThrowsWithAContextMessageWhenTheConditionNeverBecomesTrue() {
        TimeoutException exception = expectThrows(TimeoutException.class, () ->
                shortWait().waitForTrue(() -> false, "row count reached zero"));

        assertTrue(exception.getMessage().contains("row count reached zero"));
    }

    @Test
    public void waitForTrueSucceedsOnceTheConditionFlipsToTrueDuringPolling() throws TimeoutException {
        AtomicInteger callCount = new AtomicInteger(0);

        shortWait().waitForTrue(() -> callCount.incrementAndGet() >= 3);

        assertTrue(callCount.get() >= 3);
    }

    @Test
    public void waitForReturnsTrueWhenTheConditionIsMet() {
        assertTrue(shortWait().waitFor(() -> true));
    }

    @Test
    public void waitForReturnsFalseInsteadOfThrowingWhenTheConditionTimesOut() {
        assertFalse(shortWait().waitFor(() -> false));
    }

    @Test
    public void waitForTrueRejectsANullCondition() {
        expectThrows(IllegalArgumentException.class, () -> shortWait().waitForTrue(null));
    }

    @Test
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

        assertTrue(callCount.get() >= 3);
    }

    @Test
    public void waitForTruePropagatesAnExceptionThatIsNotInTheIgnoreList() {
        expectThrows(IllegalStateException.class, () -> shortWait().waitForTrue(
                () -> {
                    throw new IllegalStateException("boom");
                },
                null,
                null,
                "unignored exception",
                List.of(NullPointerException.class)));
    }

    @Test
    public void subSecondTimeoutsAreHonoredRatherThanTruncatedToZero() {
        // Regression test: Duration.getSeconds() truncates sub-second durations to 0, which used
        // to make any timeout under 1 second fire on the very first poll instead of waiting the
        // configured duration. 150ms is comfortably above one polling interval (10ms) but nowhere
        // near a full second, so a near-instant failure here would indicate the bug is back.
        long start = System.currentTimeMillis();

        expectThrows(TimeoutException.class, () -> shortWait().waitForTrue(
                () -> false, Duration.ofMillis(150), Duration.ofMillis(10), "sub-second timeout", null));

        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed >= 150, "Expected to wait at least 150ms, but timed out after " + elapsed + "ms");
    }

    @Test
    public void explicitTimeoutAndPollingIntervalOverrideTheDefaults() {
        AtomicInteger callCount = new AtomicInteger(0);

        TimeoutException exception = expectThrows(TimeoutException.class, () -> shortWait().waitForTrue(
                () -> {
                    callCount.incrementAndGet();
                    return false;
                },
                Duration.ofMillis(60),
                Duration.ofMillis(10),
                "overridden timing",
                null));

        assertTrue(exception.getMessage().contains("60 ms"));
        assertTrue(callCount.get() > 1);
    }
}
