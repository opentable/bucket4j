package com.github.bucket4j.statistic;

public class DummyStatisticCollector implements StatisticCollector {

    public static final StatisticCollector INSTANCE = new DummyStatisticCollector();

    @Override
    public void registerConsumedTokens(long numTokens) {
        // do nothing
    }

    @Override
    public void registerRejectedTokens(long numTokens) {
        // do nothing
    }

    @Override
    public void registerReturnedTokens(long numTokens) {
        // do nothing
    }

    @Override
    public void registerInterrupt() {
        // do nothing
    }

    @Override
    public void registerSleepingNanos(long sleepingNanos) {
        // do nothing
    }

    @Override
    public StatisticSnapshot getStatisticSnapshot() {
        throw new IllegalStateException("Bucket has been configured without statistic-collector.");
    }

}
