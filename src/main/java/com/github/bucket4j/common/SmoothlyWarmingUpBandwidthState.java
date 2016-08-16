package com.github.bucket4j.common;


import java.time.Duration;

public class SmoothlyWarmingUpBandwidthState implements Bandwidth {

    private final double fromValue;
    private final double toValue;
    private final long warmupNanos;

    public SmoothlyWarmingUpBandwidthState(long fromValue, long toValue, Duration warmupDuration) {
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.warmupNanos = warmupDuration.toNanos();
    }

    public static BandwidthDefinition bandwidth(long periodNanos, long fromCapacity, long toCapacity, long warmingUpNanos, long initialCapacity) {
        return null;
    }

    @Override
    public double getMaxValue(long previousAccessTimeNanos, long previousMaxValue, long currentTimeNanos) {
            double timeSinceLastRefill = currentTimeNanos - previousAccessTimeNanos;
            if (timeSinceLastRefill > warmupNanos) {
                // became cold due to was unused for a long time
                return fromValue;
            }
            if (toValue == previousMaxValue) {
                // already hot
                return toValue;
            }
            double capacityIncrement = timeSinceLastRefill * (toValue - fromValue) / warmupNanos;
            return Math.min(toValue, previousMaxValue + capacityIncrement);
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
}
