package com.github.bucket4j.statistic;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class StatisticSnapshot implements Serializable {

    private final long consumedTokens;
    private final long rejectedTokens;
    private final long returnedTokens;
    private final long interruptsCount;
    private final long sleepingNanos;

    public StatisticSnapshot(long consumedTokens, long rejectedTokens, long returnedTokens, long interruptsCount, long sleepingNanos) {
        this.consumedTokens = consumedTokens;
        this.rejectedTokens = rejectedTokens;
        this.returnedTokens = returnedTokens;
        this.interruptsCount = interruptsCount;
        this.sleepingNanos = sleepingNanos;
    }

    public long getConsumedTokens() {
        return consumedTokens;
    }

    public long getRejectedTokens() {
        return rejectedTokens;
    }

    public long getReturnedTokens() {
        return returnedTokens;
    }

    public long getInterruptsCount() {
        return interruptsCount;
    }

    public long getSleepingNanos() {
        return sleepingNanos;
    }

    public long getSleepingMicros() {
        return TimeUnit.NANOSECONDS.toMicros(sleepingNanos);
    }

    public long getSleepingMillis() {
        return TimeUnit.NANOSECONDS.toMillis(sleepingNanos);
    }

    public long getSleepingSeconds() {
        return TimeUnit.NANOSECONDS.toSeconds(sleepingNanos);
    }

    @Override
    public String toString() {
        return "StatisticSnapshot{" +
                "consumedTokens=" + consumedTokens +
                ", rejectedTokens=" + rejectedTokens +
                ", returnedTokens=" + returnedTokens +
                ", interruptsCount=" + interruptsCount +
                ", sleepingNanos=" + sleepingNanos +
                '}';
    }
}
