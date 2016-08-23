package com.github.bucket4j.common;


import com.github.bucket4j.common.capacity.Capacity;
import com.github.bucket4j.common.refill.Refill;

import java.io.Serializable;

public class Bandwidth implements Serializable {

    private final Capacity capacity;
    private final int capacityOffset;
    private final Refill refill;
    private final int refillOffset;
    private final long periodNanos;
    private final double initialCapacity;

    public Bandwidth(Capacity capacity, int capacityOffset, Refill refill, int refillOffset, long periodNanos, double initialCapacity) {
        this.capacity = capacity;
        this.capacityOffset = capacityOffset;
        this.refill = refill;
        this.refillOffset = refillOffset;
        this.periodNanos = periodNanos;
        this.initialCapacity = initialCapacity;
    }

    public Capacity getCapacity() {
        return capacity;
    }

    public int getCapacityOffset() {
        return capacityOffset;
    }

    public int getRefillOffset() {
        return refillOffset;
    }

    public Refill getRefill() {
        return refill;
    }

    public double getInitialCapacity() {
        return initialCapacity;
    }

    public long getPeriodNanos() {
        return periodNanos;
    }

}
