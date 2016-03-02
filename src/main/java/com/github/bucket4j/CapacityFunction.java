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

package com.github.bucket4j;

import java.io.Serializable;
import java.time.Duration;

/**
 * Provider of bandwidth capacity.
 */
public interface CapacityFunction extends Serializable {

    /**
     * Returns current capacity of bandwidth
     *
     *
     * @param lastRefillTime
     * @param currentTime
     * @param currentMaxCapacity
     * @return
     */
    double getCapacity(long lastRefillTime, long currentTime, double currentMaxCapacity);

    static CapacityFunction immutable(double value) {
        return new CapacityFunction() {
            @Override
            public double getCapacity(long lastRefillTime, long currentTime, double currentMaxCapacity) {
                return value;
            }
            @Override
            public String toString() {
                return "ImmutableCapacity{" +
                        "value=" + value +
                        '}';
            }
        };
    }

    static CapacityFunction warmup(long fromValue, long toValue, Duration warmupDuration) {
        double warmupNanos = warmupDuration.toNanos();
        return new CapacityFunction() {
            @Override
            public double getCapacity(long lastRefillTime, long currentTime, double currentMaxCapacity) {
                double timeSinceLastRefill = currentTime - lastRefillTime;
                if (timeSinceLastRefill > warmupNanos) {
                    // became cold due to was unused for long time
                    return fromValue;
                }
                if (toValue == currentMaxCapacity) {
                    // already hot
                    return toValue;
                }
                double capacityIncrement = timeSinceLastRefill * (toValue - fromValue) / warmupNanos;
                return Math.min(toValue, currentMaxCapacity + capacityIncrement);
            }
            @Override
            public String toString() {
                return "WarmupCapacity{" +
                        "fromValue=" + fromValue +
                        ", toValue=" + toValue +
                        ", warmupDuration=" + warmupDuration +
                        '}';
            }
        };
    }

}
