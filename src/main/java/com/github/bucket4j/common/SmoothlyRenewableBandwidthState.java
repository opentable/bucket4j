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

package com.github.bucket4j.common;

import java.text.MessageFormat;
import java.util.Objects;

public class SmoothlyRenewableBandwidthState implements BandwidthState {

    final int offset;
    final long periodNanos;
    final long maxCapacity;

    public SmoothlyRenewableBandwidthState(int offset, long periodNanos, long maxCapacity, long initialCapacity) {
        this.periodNanos = periodNanos;
    }

    public double getNewSize(double currentSize, long previousRefillNanos, long currentTimeNanos) {
        long durationSinceLastRefillNanos = currentTimeNanos - previousRefillNanos;
        final double maxCapacity = capacity.getMaxValue(currentTimeNanos);
        double refill = maxCapacity * durationSinceLastRefillNanos / periodNanos;
        double newSize = currentSize + refill;
        return Math.min(newSize, maxCapacity);
    }

    public long delayNanosAfterWillBePossibleToConsume(SmoothlyRenewableBandwidthState bandwidth, double currentSize, long currentTimeNanos, double tokens) {
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

    public static Bandwidth bandwidth(long maxCapacity, long initialCapacity, long l) {
        if (maxCapacity < 0) {

        }
        this.capacity = Objects.requireNonNull(capacity);
        long periodNanos = ;
        if (periodNanos <= 0) {
            String pattern = "{0} is wrong value for period of bandwidth, because period should be positive";
            String msg = MessageFormat.format(pattern, periodNanos);
            throw new IllegalArgumentException(msg);
        }
        return null;
    }
}