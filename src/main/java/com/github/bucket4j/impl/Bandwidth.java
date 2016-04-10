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

import com.github.bucket4j.Capacity;
import com.github.bucket4j.RefillStrategy;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

public class Bandwidth implements Serializable {

    final long periodNanos;
    final boolean guaranteed;
    final Capacity capacity;
    final RefillStrategy refillStrategy;

    public Bandwidth(Capacity capacity, RefillStrategy refillStrategy, long periodNanos, boolean guaranteed) {
        this.capacity = Objects.requireNonNull(capacity);
        if (periodNanos <= 0) {
            String pattern = "{0} is wrong value for period of bandwidth, because period should be positive";
            String msg = MessageFormat.format(pattern, periodNanos);
            throw new IllegalArgumentException(msg);
        }
        this.periodNanos = periodNanos;
        this.guaranteed = guaranteed;
    }

    public boolean isGuaranteed() {
        return guaranteed;
    }

    public boolean isLimited() {
        return !guaranteed;
    }

    public long getPeriodNanos() {
        return periodNanos;
    }

    public Capacity getCapacity() {
        return capacity;
    }

    public double getNewSize(double currentSize, long previousRefillNanos, long currentTimeNanos) {
        long durationSinceLastRefillNanos = currentTimeNanos - previousRefillNanos;
        final double maxCapacity = capacity.getMaxValue(currentTimeNanos);
        double refill = maxCapacity * durationSinceLastRefillNanos / periodNanos;
        double newSize = currentSize + refill;
        return Math.min(newSize, maxCapacity);
    }

    public long delayNanosAfterWillBePossibleToConsume(Bandwidth bandwidth, double currentSize, long currentTimeNanos, double tokens) {
        if (tokens <= currentSize) {
            return 0;
        }
        final double maxCapacity = capacity.getMaxValue(currentTimeNanos);
        if (tokens > maxCapacity) {
            return Long.MAX_VALUE;
        }
        double deficit = tokens - currentSize;
        double nanosToCloseDeficit = periodNanos * deficit / maxCapacity;
        return (long) nanosToCloseDeficit;
    }

    @Override
    public String toString() {
        return "Bandwidth{" +
                ", periodNanos=" + periodNanos +
                ", guaranteed=" + guaranteed +
                ", capacity=" + capacity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bandwidth bandwidth = (Bandwidth) o;

        if (periodNanos != bandwidth.periodNanos) return false;
        if (guaranteed != bandwidth.guaranteed) return false;
        return capacity.equals(bandwidth.capacity);
    }

    @Override
    public int hashCode() {
        int result = (int) (periodNanos ^ (periodNanos >>> 32));
        result = 31 * result + (guaranteed ? 1 : 0);
        result = 31 * result + capacity.hashCode();
        return result;
    }

}