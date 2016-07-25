package com.github.bucket4j.statistic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleBucketStatistic implements BucketStatistic, SimpleStatisticCollectorMBean {

    private final AtomicLong consumedTokens = new AtomicLong();
    private final AtomicLong rejectedTokens = new AtomicLong();
    private final AtomicLong interruptsCount = new AtomicLong();
    private final AtomicLong sleepingNanos = new AtomicLong();

    @Override
    public void registerConsumedTokens(long numTokens) {
        consumedTokens.addAndGet(numTokens);
    }

    @Override
    public void registerRejectedTokens(long numTokens) {
        rejectedTokens.addAndGet(numTokens);
    }

    @Override
    public void registerInterrupt() {
        interruptsCount.incrementAndGet();
    }

    @Override
    public void registerParkedNanos(long numTokens) {
        sleepingNanos.addAndGet(numTokens);
    }

    @Override
    public StatisticSnapshot getSnapshot() {
        return new StatisticSnapshot(getConsumedTokens(), getRejectedTokens(), getInterruptsCount(), getSleepingNanos());
    }

    @Override
    public long getConsumedTokens() {
        return consumedTokens.get();
    }

    @Override
    public long getRejectedTokens() {
        return rejectedTokens.get();
    }

    @Override
    public long getInterruptsCount() {
        return interruptsCount.get();
    }

    @Override
    public long getSleepingNanos() {
        return sleepingNanos.get();
    }

    @Override
    public long getSleepingMicros() {
        return TimeUnit.MICROSECONDS.toMicros(sleepingNanos.get());
    }

    @Override
    public long getSleepingMillis() {
        return TimeUnit.MILLISECONDS.toMillis(sleepingNanos.get());
    }

    @Override
    public long getSleepingSeconds() {
        return TimeUnit.SECONDS.toMillis(sleepingNanos.get());
    }

}
