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

import java.util.Optional;

public class SmoothlyRenewableBandwidthState implements BandwidthState {

    final int offset;
    final long periodNanos;
    final long maxCapacity;

    public static Bandwidth bandwidth(long maxCapacity, long initialCapacity, long periodNanos) {
        Preconditions.checkPeriod(periodNanos);
        Preconditions.checkCapacities(maxCapacity, initialCapacity);
        return new Bandwidth() {
            @Override
            public BandwidthState createInitialBandwidthState(StateInitializer stateInitializer, long currentTimeNanos) {
                return new SmoothlyRenewableBandwidthState(stateInitializer, periodNanos, maxCapacity, initialCapacity);
            }
            @Override
            public Optional<Long> getPeriodInNanos() {
                return Optional.of(periodNanos);
            }
            @Override
            public Optional<Long> getMaxCapacity() {
                return Optional.of(maxCapacity);;
            }
        };
    }

    private SmoothlyRenewableBandwidthState(StateInitializer stateInitializer, long periodNanos, long maxCapacity, long initialCapacity) {
        this.periodNanos = periodNanos;
        this.maxCapacity = maxCapacity;
        this.offset = stateInitializer.allocate(new long[] {initialCapacity});
    }

    public double getNewSize(double currentSize, long previousRefillNanos, long currentTimeNanos) {
        long durationSinceLastRefillNanos = currentTimeNanos - previousRefillNanos;
        double refill = maxCapacity * durationSinceLastRefillNanos / periodNanos;
        double newSize = currentSize + refill;
        return Math.min(newSize, maxCapacity);
    }

    public long delayNanosAfterWillBePossibleToConsume(SmoothlyRenewableBandwidthState bandwidth, double currentSize, long currentTimeNanos, double tokens) {
        if (tokens <= currentSize) {
            return 0;
        }
        if (tokens > maxCapacity) {
            return Long.MAX_VALUE;
        }
        double deficit = tokens - currentSize;
        double nanosToCloseDeficit = periodNanos * deficit / maxCapacity;
        return (long) nanosToCloseDeficit;
    }

    @Override
    public String toString() {
        return "SmoothlyRenewableBandwidthState{" +
                "offset=" + offset +
                ", periodNanos=" + periodNanos +
                ", maxCapacity=" + maxCapacity +
                '}';
    }

}