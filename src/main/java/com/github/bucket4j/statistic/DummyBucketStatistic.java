package com.github.bucket4j.statistic;

public class DummyBucketStatistic implements BucketStatistic {

    public static final BucketStatistic INSTANCE = new DummyBucketStatistic();

    @Override
    public void registerConsumedTokens(long numTokens) {
        // do nothing
    }

    @Override
    public void registerRejectedTokens(long numTokens) {
        // do nothing
    }

    @Override
    public void registerInterrupt() {
        // do nothing
    }

    @Override
    public void registerParkedNanos(long sleepingNanos) {
        // do nothing
    }

    @Override
    public StatisticSnapshot getSnapshot() {
        throw new IllegalStateException("Bucket has been configured without statistic-collector.");
    }

}
