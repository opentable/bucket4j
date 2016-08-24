package com.github.bucket4j.common.capacity;

import com.github.bucket4j.common.BucketState;
import com.github.bucket4j.common.Stateful;

import java.io.Serializable;
import java.text.MessageFormat;
import java.time.Duration;

public abstract class Capacity implements Serializable, Stateful {

    public static Capacity constant(double maxValue) {
        return new ConstantCapacity(maxValue, maxValue);
    }

    public static Capacity constant(double maxValue, double initialValue) {
        return new ConstantCapacity(maxValue, initialValue);
    }

    public static Capacity warmingUp(double coldValue, double hotValue, Duration warmPeriod) {
        return new WarmingUpCapacity(coldValue, hotValue, warmPeriod.toNanos(), coldValue);
    }

    public static Capacity warmingUp(double coldValue, double hotValue, Duration warmPeriod, double initialValue) {
        return new WarmingUpCapacity(coldValue, hotValue, warmPeriod.toNanos(), initialValue);
    }

    private final double initialValue;

    public Capacity(double initialValue) {
        if (initialValue < 0) {
            String pattern = "{0} is wrong value for initial maxCapacity, because initial maxCapacity should be >= 0";
            String msg = MessageFormat.format(pattern, initialValue);
            throw new IllegalArgumentException(msg);
        }
        this.initialValue = initialValue;
    }

    public final double getInitialValue() {
        return initialValue;
    }

    abstract double getCurrentValue(BucketState state, int offset, long previousAccessTimeNanos, long currentTimeNanos);

}
