package com.github.bucket4j.impl;

import com.github.bucket4j.Capacity;

public final class ConstantCapacity implements Capacity {

    final long maxValue;
    final long initialValue;

    public ConstantCapacity(long maxValue, long initialValue) {
        this.maxValue = maxValue;
        this.initialValue = initialValue;
    }

    @Override
    public double getInitialValue(long initializationTimeNanos) {
        return initialValue;
    }

    @Override
    public double getMaxValue(long previousAccessTimeNanos, long previousMaxValue, long currentTimeNanos) {
        return maxValue;
    }

    @Override
    public String toString() {
        return "ConstantCapacity{" +
                "maxValue=" + maxValue +
                ", initialValue=" + initialValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConstantCapacity that = (ConstantCapacity) o;

        if (maxValue != that.maxValue) return false;
        return initialValue == that.initialValue;
    }

    @Override
    public int hashCode() {
        int result = (int) (maxValue ^ (maxValue >>> 32));
        result = 31 * result + (int) (initialValue ^ (initialValue >>> 32));
        return result;
    }

}
