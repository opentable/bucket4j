package com.github.bucket4j.common;


public class IntervallyReneableBandwidth implements Bandwidth {

    @Override
    public void initializeBandwidthState(StateInitializer stateInitializer, long currentTimeNanos) {
        // TODO
    }

    @Override
    public void refill(BucketState state, long lastRefillTimeNanos, long currentTimeNanos) {
        // TODO
    }

    @Override
    public long delayNanosAfterWillBePossibleToConsume(BucketState state, long currentTimeNanos, double tokens) {
        // TODO
        return 0;
    }

    @Override
    public long reserve(BucketState state, double tokens) {
        // TODO
        return 0;
    }

    @Override
    public void checkCompatibility(Bandwidth another) throws IllegalApiUsageException {
        // TODO
    }

}
