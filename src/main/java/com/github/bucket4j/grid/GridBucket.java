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

package com.github.bucket4j.grid;

import com.github.bucket4j.common.AbstractBucket;
import com.github.bucket4j.common.BucketState;
import com.github.bucket4j.common.InitialState;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

public class GridBucket extends AbstractBucket {

    private final GridProxy gridProxy;

    public GridBucket(InitialState initialState, GridProxy gridProxy) {
        super(initialState.getConfiguration());
        this.gridProxy = gridProxy;
        gridProxy.setInitialState(initialState.getState());
    }

    @Override
    protected long consumeAsMuchAsPossibleImpl(long limit) {
        return gridProxy.execute(new ConsumeAsMuchAsPossibleCommand(limit));
    }

    @Override
    protected boolean tryConsumeImpl(long tokensToConsume) {
        return gridProxy.execute(new TryConsumeCommand(tokensToConsume));
    }

    @Override
    protected boolean consumeOrAwaitImpl(long tokensToConsume, long waitIfBusyTimeLimit) throws InterruptedException {
        final boolean isWaitingLimited = waitIfBusyTimeLimit > 0;
        final ConsumeOrCalculateTimeToCloseDeficitCommand consumeCommand = new ConsumeOrCalculateTimeToCloseDeficitCommand(tokensToConsume);
        final long methodStartTimeNanos = isWaitingLimited? configuration.getTimeMeter().currentTimeNanos() : 0;

        while (true) {
            long nanosToCloseDeficit = gridProxy.execute(consumeCommand);
            if (nanosToCloseDeficit == 0) {
                return true;
            }
            if (nanosToCloseDeficit == Long.MAX_VALUE) {
                return false;
            }

            if (isWaitingLimited) {
                long currentTimeNanos = configuration.getTimeMeter().currentTimeNanos();
                long methodDuration = currentTimeNanos - methodStartTimeNanos;
                if (methodDuration >= waitIfBusyTimeLimit) {
                    return false;
                }
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
        // TODO
    }

    @Override
    protected CompletableFuture<Boolean> tryConsumeAsyncImpl(long numTokens, long maxWaitNanos, ScheduledExecutorService scheduler) {
        // TODO
        return null;
    }

    @Override
    public BucketState getStateSnapshot() {
        return gridProxy.execute(new CreateSnapshotCommand());
    }

    @Override
    public long getAvailableTokens() {
        return 0;
    }

}
