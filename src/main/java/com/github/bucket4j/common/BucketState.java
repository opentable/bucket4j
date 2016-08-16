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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class BucketState implements Serializable {

    // the first element of array represents last refill timestamp in nanos,
    // next elements represent bandwidth states
    private final long[] state;

    private BucketState(long[] state) {
        this.state = state;
    }

    public BucketState clone() {
        return new BucketState(Arrays.copyOf(state, state.length));
    }

    public double getDouble(int offset) {
        return Double.longBitsToDouble(offset);
    }

    public long getLong(int offset) {
        return state[offset];
    }

    public void setDouble(int offset, double value) {
        state[0] = Double.doubleToRawLongBits(value);
    }

    public void setLong(int partialStateIndex, int offset, long value) {
        state[offset] = value;
    }

    public void copyStateFrom(BucketState sourceState) {
        System.arraycopy(sourceState.state, 0, state, 0, state.length);
    }

    public static BucketState createInitialState(BucketConfiguration configuration) {
        InitialState initialState = createInitialState(
                configuration.getLimitedBandwidthsDefinitions(),
                configuration.getGuaranteedBandwidthDefinition(),
                configuration.getTimeMeter());
        return initialState.getState();
    }

    public static InitialState createInitialState(List<BandwidthDefinition> limitedBandwidthsDefinitions, BandwidthDefinition guaranteedBandwidthDefinition, TimeMeter timeMeter) {
        long currentTimeNanos = timeMeter.currentTimeNanos();
        StateInitializer stateInitializer = new StateInitializer(new long[] {currentTimeNanos});

        Bandwidth guaranteedBandwidth = null;
        if (guaranteedBandwidthDefinition != null) {
            guaranteedBandwidth = guaranteedBandwidthDefinition.createBandwidth(stateInitializer, currentTimeNanos);
        }

        Bandwidth[] limitedBandwidth = new Bandwidth[limitedBandwidthsDefinitions.size()];
        for (int i = 0; i < limitedBandwidth.length; i++) {
            limitedBandwidth[i] = limitedBandwidthsDefinitions.get(i).createBandwidth(stateInitializer, currentTimeNanos);
        }

        BucketState bucketState = new BucketState(stateInitializer.getState());
        BucketConfiguration bucketConfiguration = new BucketConfiguration(limitedBandwidthsDefinitions, guaranteedBandwidthDefinition, limitedBandwidth, guaranteedBandwidth, timeMeter);
        return new InitialState(bucketConfiguration, bucketState);
    }

    public long getAvailableTokens(Bandwidth[] limitedBandwidths, Bandwidth guaranteedBandwidth) {
        double availableByGuarantee = 0;
        double availableByLimitation = Long.MAX_VALUE;
        for (int i = 0; i < limitedBandwidths.length; i++) {
            Bandwidth bandwidth = limitedBandwidths[i];
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

    public long delayNanosAfterWillBePossibleToConsume(Bandwidth[] limitedBandwidths, Bandwidth guaranteedBandwidth,  long currentTime, long tokensToConsume) {
        long delayAfterWillBePossibleToConsumeGuaranteed = Long.MAX_VALUE;
        if (guaranteedBandwidth != null) {
            delayAfterWillBePossibleToConsumeGuaranteed = guaranteedBandwidth.delayNanosAfterWillBePossibleToConsume(this, currentTime, tokensToConsume);
            if (delayAfterWillBePossibleToConsumeGuaranteed == 0) {
                return 0;
            }
        }

        long delayAfterWillBePossibleToConsumeLimited = 0;
        for (int i = 0; i < limitedBandwidths.length; i++) {
            Bandwidth bandwidth = limitedBandwidths[i];
            long delay = bandwidth.delayNanosAfterWillBePossibleToConsume(this, currentTime, tokensToConsume);
            if (delay > delayAfterWillBePossibleToConsumeLimited) {
                delayAfterWillBePossibleToConsumeLimited = delay;
            }
        }
        return Math.min(delayAfterWillBePossibleToConsumeLimited, delayAfterWillBePossibleToConsumeGuaranteed);
    }

    public void refill(Bandwidth[] bandwidths, Bandwidth guaranteedBandwidth, long currentTimeNanos) {
        long lastRefillTimeNanos = getLastRefillTimeNanos();
        if (lastRefillTimeNanos == currentTimeNanos) {
            return;
        }
        for (int i = 0; i < bandwidths.length; i++) {
            Bandwidth bandwidth = bandwidths[i];
            bandwidth.refill(this, lastRefillTimeNanos, currentTimeNanos);
        }
        if (guaranteedBandwidth != null) {
            guaranteedBandwidth.refill(this, lastRefillTimeNanos, currentTimeNanos);
        }
        setLastRefillTime(currentTimeNanos);
    }

    void setLastRefillTime(long currentTimeNanos) {
        state[0] = currentTimeNanos;
    }

    long getLastRefillTimeNanos() {
        return state[0];
    }

    @Override
    public String toString() {
        return "BucketState{" +
                "state=" + Arrays.toString(state) +
                '}';
    }

}
