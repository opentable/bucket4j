package com.github.bucket4j.common.capacity;

import com.github.bucket4j.common.BucketState;
import com.github.bucket4j.common.capacity.Capacity;

public class WarmingUpCapacity implements Capacity {

    private final double coldValue;
    private final double hotValue;
    private final double warmPeriodNanos;

    public WarmingUpCapacity(double coldValue, double hotValue, double warmPeriodNanos) {
        this.coldValue = coldValue;
        this.hotValue = hotValue;
        this.warmPeriodNanos = warmPeriodNanos;
    }


    @Override
    public int getStateSize() {
        return 2;
    }

    @Override
    public void populateInitialState(BucketState state, int offset, long currentTimeNanos) {
        setPreviousModificationTimeNanos(state, offset, currentTimeNanos);
        setValue(state, offset, coldValue);
    }

    @Override
    public double getCurrentValue(BucketState state, int offset, long currentTimeNanos) {
        long previousModificationTimeNanos = getPreviousModificationTimeNanos(state, offset);

        double timeSinceLastRefill = currentTimeNanos - previousModificationTimeNanos;
        if (timeSinceLastRefill > warmPeriodNanos) {
            // became cold due to was unused for a long time
            return coldValue;
        }

        double previousValue = getValue(state, offset);
        if (hotValue == previousValue) {
            // already hot
            return hotValue;
        }
        double capacityIncrement = timeSinceLastRefill * (hotValue - coldValue) / warmPeriodNanos;
        double newValue = Math.min(hotValue, previousValue + capacityIncrement);
        if (newValue != previousValue) {
            setPreviousModificationTimeNanos(state, offset, currentTimeNanos);
            setValue(state, offset, newValue);
        }
        return newValue;
    }

    private void setPreviousModificationTimeNanos(BucketState state, int offset, long timeNanos) {
        state.setLong(offset, timeNanos);
    }

    private long getPreviousModificationTimeNanos(BucketState state, int offset) {
        return state.getLong(offset);
    }

    private void setValue(BucketState state, int offset, double value) {
        state.setDouble(offset + 1, value);
    }

    private double getValue(BucketState state, int offset) {
        return state.getDouble(offset + 1);
    }

}
