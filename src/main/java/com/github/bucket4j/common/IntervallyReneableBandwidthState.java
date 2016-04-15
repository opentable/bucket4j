package com.github.bucket4j.common;

import com.google.common.cache.CacheStats;

public class IntervallyReneableBandwidthState implements BandwidthState {

    @Override
    public void refill(State state, long lastRefillTimeNanos, long currentTimeNanos) {
        CacheStats s;
    }

    @Override
    public long delayNanosAfterWillBePossibleToConsume(State state, long currentTimeNanos, double tokens) {
        return 0;
    }

    @Override
    public long reserve(State state, double tokens) {
        return 0;
    }

}
