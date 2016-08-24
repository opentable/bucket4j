package com.github.bucket4j.common.capacity;

import com.github.bucket4j.common.BucketState;

import java.text.MessageFormat;

public class WarmingUpCapacity extends Capacity {

    private final double coldValue;
    private final double hotValue;
    private final double warmPeriodNanos;

    public WarmingUpCapacity(double coldValue, double hotValue, double warmPeriodNanos, double initialValue) {
        super(initialValue);

        if (coldValue <= 0) {
            String pattern = "{0} is wrong value for coldValue, because maxCapacity should be > 0";
            String msg = MessageFormat.format(pattern, coldValue);
            throw new IllegalArgumentException(msg);
        }
        this.coldValue = coldValue;

        if (hotValue <= 0) {
            String pattern = "{0} is wrong value for hotValue, because maxCapacity should be > 0";
            String msg = MessageFormat.format(pattern, hotValue);
            throw new IllegalArgumentException(msg);
        }
        this.hotValue = hotValue;

        if (warmPeriodNanos <= 0) {
            String pattern = "{0} is wrong value for warmPeriodNanos, because warmPeriodNanos should be > 0";
            String msg = MessageFormat.format(pattern, warmPeriodNanos);
            throw new IllegalArgumentException(msg);
        }
        this.warmPeriodNanos = warmPeriodNanos;
    }

    @Override
    public int getStateSize() {
        return 1;
    }

    @Override
    public void populateInitialState(BucketState state, int offset, long currentTimeNanos) {
        state.setDouble(offset, coldValue);
    }

    @Override
    double getCurrentValue(BucketState state, int offset, long previousAccessTimeNanos, long currentTimeNanos) {
        double timeSinceLastRefill = currentTimeNanos - previousAccessTimeNanos;
        if (timeSinceLastRefill > warmPeriodNanos) {
            // became cold due to was unused for a long time
            return coldValue;
        }

        double previousValue = state.getDouble(offset);
        if (hotValue == previousValue) {
            // already hot
            return hotValue;
        }
        double capacityIncrement = timeSinceLastRefill * (hotValue - coldValue) / warmPeriodNanos;
        double newValue = Math.min(hotValue, previousValue + capacityIncrement);
        if (newValue != previousValue) {
            state.setDouble(offset, newValue);
        }
        return newValue;
    }

    @Override
    public String toString() {
        return "WarmingUpCapacity{" +
                "coldValue=" + coldValue +
                ", hotValue=" + hotValue +
                ", warmPeriodNanos=" + warmPeriodNanos +
                '}';
    }

}
