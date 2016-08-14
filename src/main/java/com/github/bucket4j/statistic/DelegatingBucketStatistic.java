package com.github.bucket4j.statistic;

public class DelegatingBucketStatistic implements BucketStatistic {

    private final BucketStatistic[] targets;

    public DelegatingBucketStatistic(BucketStatistic[] targets) {
        this.targets = targets;
    }

    @Override
    public void registerConsumedTokens(long numTokens) {
        for (BucketStatistic target : targets) {
            target.registerConsumedTokens(numTokens);
        }
    }

    @Override
    public void registerRejectedTokens(long numTokens) {
        for (BucketStatistic target : targets) {
            target.registerRejectedTokens(numTokens);
        }
    }

    @Override
    public void registerInterrupt() {
        for (BucketStatistic target : targets) {
            target.registerInterrupt();
        }
    }

    @Override
    public void registerParkedNanos(long sleepingNanos) {
        for (BucketStatistic target : targets) {
            target.registerParkedNanos(sleepingNanos);
        }
    }

}
