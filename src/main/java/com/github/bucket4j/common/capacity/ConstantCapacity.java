package com.github.bucket4j.common.capacity;

import com.github.bucket4j.common.BucketState;
import com.github.bucket4j.common.capacity.Capacity;

import java.text.MessageFormat;

public class ConstantCapacity extends Capacity {

    private final double maxValue;

    public ConstantCapacity(double maxValue, double initialValue) {
        super(initialValue);
        if (maxValue <= 0) {
            String pattern = "{0} is wrong value for maxCapacity, because maxCapacity should be > 0";
            String msg = MessageFormat.format(pattern, maxValue);
            throw new IllegalArgumentException(msg);
        }
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
    double getCurrentValue(BucketState state, int offset, long previousAccessTimeNanos, long currentTimeNanos) {
        return maxValue;
    }

    @Override
    public String toString() {
        return "ConstantCapacity{" +
                "maxValue=" + maxValue +
                '}';
    }

}
