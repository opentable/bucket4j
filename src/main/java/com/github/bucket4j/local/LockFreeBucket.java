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
package com.github.bucket4j.local;


import com.github.bucket4j.common.*;
import com.github.bucket4j.statistic.BucketStatistic;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeBucket extends AbstractBucket {

    private final AtomicReference<BucketState> stateReference;
    private final BucketStatistic bucketStatistic;

    public LockFreeBucket(InitialState stateWithConfiguration, BucketStatistic bucketStatistic) {
        super(stateWithConfiguration.getConfiguration());
        BucketState initialState = stateWithConfiguration.getState();
        this.stateReference = new AtomicReference<>(initialState);
        this.bucketStatistic = bucketStatistic;
    }

    @Override
    public long getAvailableTokens() {
        return 0;
    }

    @Override
    protected long consumeAsMuchAsPossibleImpl(long limit) {
        BucketState previousState = stateReference.get();
        BucketState newState = previousState.clone();
        BandwidthState[] bandwidths = configuration.getLimitedBandwidths();
        long currentTimeNanos = configuration.getTimeMeter().currentTimeNanos();

        while (true) {
            newState.refill(bandwidths, currentTimeNanos);
            long availableToConsume = newState.getAvailableTokens(bandwidths);
            long toConsume = Math.min(limit, availableToConsume);
            if (toConsume == 0) {
                return 0;
            }
            newState.consume(toConsume);
            if (stateReference.compareAndSet(previousState, newState)) {
                return toConsume;
            } else {
                previousState = stateReference.get();
                newState.copyStateFrom(previousState);
            }
        }
    }

    @Override
    protected boolean tryConsumeImpl(long tokensToConsume) {
        BucketState previousState = stateReference.get();
        BucketState newState = previousState.clone();
        BandwidthState[] bandwidths = configuration.getLimitedBandwidths();
        long currentTimeNanos = configuration.getTimeMeter().currentTimeNanos();

        while (true) {
            newState.refill(bandwidths, currentTimeNanos);
            long availableToConsume = newState.getAvailableTokens(bandwidths);
            if (tokensToConsume > availableToConsume) {
                return false;
            }
            newState.consume(tokensToConsume);
            if (stateReference.compareAndSet(previousState, newState)) {
                return true;
            } else {
                previousState = stateReference.get();
                newState.copyStateFrom(previousState);
            }
        }
    }

    @Override
    protected CompletableFuture<Boolean> tryConsumeAsyncImpl(long numTokens, long maxWaitNanos, ScheduledExecutorService scheduler) {
        return null;
    }

    @Override
    protected boolean consumeOrAwaitImpl(long tokensToConsume, long waitIfBusyTimeLimit) throws InterruptedException {
        SmoothlyRenewableBandwidth[] bandwidths = configuration.getBandwidths();
        boolean isWaitingLimited = waitIfBusyTimeLimit > 0;

        final long methodStartTimeNanos = configuration.getTimeMeter().currentTimeNanos();
        long currentTimeNanos = methodStartTimeNanos;
        long methodDuration = 0;
        boolean isFirstCycle = true;

        BucketState previousState = stateReference.get();
        BucketState newState = previousState.clone();

        while (true) {
            if (isFirstCycle) {
                isFirstCycle = false;
            } else {
                currentTimeNanos = configuration.getTimeMeter().currentTimeNanos();
                methodDuration = currentTimeNanos - methodStartTimeNanos;
                if (isWaitingLimited && methodDuration >= waitIfBusyTimeLimit) {
                    return false;
                }
                previousState = stateReference.get();
                newState.copyStateFrom(previousState);
            }

            newState.refill(bandwidths, currentTimeNanos);
            long nanosToCloseDeficit = newState.delayNanosAfterWillBePossibleToConsume(bandwidths, currentTimeNanos, tokensToConsume);
            if (nanosToCloseDeficit == Long.MAX_VALUE) {
                return false;
            }
            if (nanosToCloseDeficit == 0) {
                newState.consume(tokensToConsume);
                if (stateReference.compareAndSet(previousState, newState)) {
                    return true;
                } else {
                    continue;
                }
            }

            if (isWaitingLimited) {
                long sleepingTimeLimit = waitIfBusyTimeLimit - methodDuration;
                if (nanosToCloseDeficit >= sleepingTimeLimit) {
                    return false;
                }
            }
            configuration.getTimeMeter().parkNanos(nanosToCloseDeficit);
        }
    }

    @Override
    protected void applySnapshotImpl(BucketState bucketState) {
        stateReference.set(bucketState);
    }

    @Override
    public BucketState getStateSnapshot() {
        return stateReference.get().clone();
    }

    @Override
    public String toString() {
        return "LockFreeBucket{" +
                "state=" + stateReference.get() +
                ", configuration=" + configuration +
                '}';
    }

}