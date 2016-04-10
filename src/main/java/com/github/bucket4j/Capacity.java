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

import com.github.bucket4j.impl.ConstantCapacity;
import com.github.bucket4j.impl.WarmupCapacity;

import java.io.Serializable;
import java.time.Duration;

/**
 * Provider of bandwidth capacity.
 */
public interface Capacity extends Serializable, PartialState {

    double getInitialValue(long initializationTimeNanos);

    double getMaxValue(long previousAccessTimeNanos, long previousMaxValue, long currentTimeNanos);

    static Capacity constant(final long maxValue) {
        return new ConstantCapacity(maxValue, maxValue);
    }

    static Capacity constant(final long maxValue, final long initialValue) {
        return new ConstantCapacity(maxValue, initialValue);
    }

    static Capacity warmup(long fromValue, long toValue, Duration warmupDuration) {
        return new WarmupCapacity(fromValue, toValue, warmupDuration);
    }

}
