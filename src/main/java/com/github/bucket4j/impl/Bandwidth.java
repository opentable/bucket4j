/*
 * Copyright 2015 Vladimir Bukhtoyarov
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.bucket4j.impl;

import com.github.bucket4j.CapacityAdjuster;

import java.io.Serializable;

public class Bandwidth implements Serializable {

    private final long initialCapacity;
    private final long periodNanos;
    private final boolean guaranteed;
    private final CapacityAdjuster adjuster;

    public Bandwidth(CapacityAdjuster adjuster, long initialCapacity, long periodNanos, boolean guaranteed) {
        this.adjuster = adjuster;
        this.initialCapacity = initialCapacity;
        this.periodNanos = periodNanos;
        this.guaranteed = guaranteed;
    }

    public boolean isGuaranteed() {
        return guaranteed;
    }

    public boolean isLimited() {
        return !guaranteed;
    }

    public long getInitialCapacity() {
        return initialCapacity;
    }

    public double getNewSize(double currentSize, long previousRefillNanos, long currentTimeNanos) {
        long durationSinceLastRefillNanos = currentTimeNanos - previousRefillNanos;
        final double maxCapacity = adjuster.getCapacity(currentTimeNanos);
        double refill = maxCapacity * durationSinceLastRefillNanos / periodNanos;
        double newSize = currentSize + refill;
        return Math.min(newSize, maxCapacity);
    }

    public long delayNanosAfterWillBePossibleToConsume(double currentSize, long currentTimeNanos, double tokens) {
        if (tokens <= currentSize) {
            return 0;
        }
        final double maxCapacity = getMaxCapacity(currentTimeNanos);
        if (tokens > maxCapacity) {
            return Long.MAX_VALUE;
        }
        double deficit = tokens - currentSize;
        double nanosToCloseDeficit = periodNanos * deficit / maxCapacity;
        return (long) nanosToCloseDeficit;
    }

    public double getMaxCapacity(long currentTime) {
        return adjuster.getCapacity(currentTime);
    }

    @Override
    public String toString() {
        return "Bandwidth{" +
                "initialCapacity=" + initialCapacity +
                ", periodNanos=" + periodNanos +
                ", guaranteed=" + guaranteed +
                ", adjuster=" + adjuster +
                '}';
    }

}