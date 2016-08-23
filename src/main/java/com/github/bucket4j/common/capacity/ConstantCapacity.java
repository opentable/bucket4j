package com.github.bucket4j.common.capacity;

import com.github.bucket4j.common.BucketState;
import com.github.bucket4j.common.capacity.Capacity;

public class ConstantCapacity implements Capacity {

    private final double maxValue;

    public ConstantCapacity(double maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public int getStateSize() {
        // constant capacity has not state
        return 0;
    }

    @Override
    public void populateInitialState(BucketState state, int offset, long currentTimeNanos) {
        // do nothing
    }

    @Override
    public double getCurrentValue(BucketState state, int offset, long currentTimeNanos) {
        return maxValue;
    }

}
