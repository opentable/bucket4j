package com.github.bucket4j.common.capacity;

import com.github.bucket4j.common.BucketState;
import com.github.bucket4j.common.Stateful;

import java.io.Serializable;
import java.time.Duration;

public interface Capacity extends Serializable, Stateful {

    double getCurrentValue(BucketState state, int offset, long currentTimeNanos);

    static Capacity constant(double maxValue) {
        return new ConstantCapacity(maxValue);
    }

    static Capacity warmingUp(double coldValue, double hotValue, Duration warmPeriod) {
        return new WarmingUpCapacity(coldValue, hotValue, warmPeriod.toNanos());
    }

}
