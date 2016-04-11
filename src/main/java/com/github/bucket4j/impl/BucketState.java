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

import java.io.Serializable;
import java.util.Arrays;

public class BucketState implements Serializable {

    // the first element of array represents last getNewSize timestamp in nanos,
    // next elements represent bandwidth current size
    //private final double[] state;

    private BucketState(double[] state) {
        this.state = state;
    }

    @Override
    public BucketState clone() {
        return new BucketState(Arrays.copyOf(state, state.length));
    }

    public void copyStateFrom(BucketState sourceState) {
        System.arraycopy(sourceState.state, 0, state, 0, state.length);
    }

    public static BucketState createInitialState(BucketConfiguration configuration) {
        SmoothlyRefillingBandwidthBandwidth[] bandwidths = configuration.getBandwidths();
        double state[] = new double[bandwidths.length + 1];
        long currentTimeNanos = configuration.getTimeMeter().currentTimeNanos();
        for(int i = 0; i < bandwidths.length; i++) {
            state[i + 1] = bandwidths[i].getCapacity().getInitialValue(currentTimeNanos);
        }
        BucketState bucketState = new BucketState(state);
        bucketState.setLastRefillTime(currentTimeNanos);
        return bucketState;
    }

    public long getAvailableTokens(SmoothlyRefillingBandwidthBandwidth[] bandwidths) {
        double availableByLimitation = Long.MAX_VALUE;
        double availableByGuarantee = 0;
        for (int i = 0; i < bandwidths.length; i++) {
            SmoothlyRefillingBandwidthBandwidth bandwidth = bandwidths[i];
            double currentSize = state[i + 1];
            if (bandwidth.isLimited()) {
                availableByLimitation = Math.min(availableByLimitation, currentSize);
            } else {
                availableByGuarantee = currentSize;
            }
        }
        return (long) Math.max(availableByLimitation, availableByGuarantee);
    }

    public void consume(long toConsume) {
        for (int i = 1; i < state.length; i++) {
            state[i] = Math.max(0, state[i] - toConsume);
        }
    }

    public long delayNanosAfterWillBePossibleToConsume(SmoothlyRefillingBandwidthBandwidth[] bandwidths, long currentTime, long tokensToConsume) {
        long delayAfterWillBePossibleToConsumeLimited = 0;
        long delayAfterWillBePossibleToConsumeGuaranteed = Long.MAX_VALUE;
        for (int i = 0; i < bandwidths.length; i++) {
            SmoothlyRefillingBandwidthBandwidth bandwidth = bandwidths[i];
            double currentSize = state[i + 1];
            long delay = bandwidth.delayNanosAfterWillBePossibleToConsume(currentSize, currentTime, tokensToConsume);
            if (bandwidth.isGuaranteed()) {
                if (delay == 0) {
                    return 0;
                } else {
                    delayAfterWillBePossibleToConsumeGuaranteed = delay;
                }
                continue;
            }
            if (delay > delayAfterWillBePossibleToConsumeLimited) {
                delayAfterWillBePossibleToConsumeLimited = delay;
            }
        }
        return Math.min(delayAfterWillBePossibleToConsumeLimited, delayAfterWillBePossibleToConsumeGuaranteed);
    }

    public void refill(SmoothlyRefillingBandwidthBandwidth[] bandwidths, long currentTimeNanos) {
        long lastRefillTimeNanos = getLastRefillTimeNanos();
        if (lastRefillTimeNanos == currentTimeNanos) {
            return;
        }
        for (int i = 0; i < bandwidths.length; i++) {
            SmoothlyRefillingBandwidthBandwidth bandwidth = bandwidths[i];
            double currentSize = state[i + 1];
            state[i + 1] = bandwidth.getNewSize(currentSize, lastRefillTimeNanos, currentTimeNanos);
        }
        setLastRefillTime(currentTimeNanos);
    }

    void setLastRefillTime(long currentTimeNanos) {
        state[0] = Double.longBitsToDouble(currentTimeNanos);
    }

    long getLastRefillTimeNanos() {
        return Double.doubleToRawLongBits(state[0]);
    }

    @Override
    public String toString() {
        return "BucketState{" +
                "state=" + Arrays.toString(state) +
                '}';
    }

}
