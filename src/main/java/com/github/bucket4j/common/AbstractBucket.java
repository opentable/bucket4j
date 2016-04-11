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

import com.github.bucket4j.Bucket;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

public abstract class AbstractBucket implements Bucket {

    protected static final long UNSPECIFIED_WAITING_LIMIT = -1;
    protected final BucketConfiguration configuration;

    protected AbstractBucket(BucketConfiguration configuration) {
        this.configuration = configuration;
    }

    protected abstract long consumeAsMuchAsPossibleImpl(long limit);

    protected abstract boolean tryConsumeImpl(long tokensToConsume);

    protected abstract CompletableFuture<Boolean> tryConsumeAsyncImpl(long numTokens, long maxWaitNanos, ScheduledExecutorService scheduler);

    protected abstract boolean consumeOrAwaitImpl(long tokensToConsume, long waitIfBusyNanos) throws InterruptedException;

    protected abstract void applySnapshotImpl(BucketState bucketState);

    /**
     * Returns configuration of bucket.
     *
     * @return Bucket configuration
     */
    public BucketConfiguration getConfiguration() {
        return configuration;
    }


    /**
     * Creates snapshot of bucket state.
     *
     * @return Snapshot of bucket state
     */
    abstract public BucketState getStateSnapshot();

    /**
     * Restore bucket state from snapshot.
     *
     * @param snapshot previously snapshot
     */
    public void applySnapshot(BucketState snapshot) {
        applySnapshotImpl(Objects.requireNonNull(snapshot));
    }

    @Override
    public boolean tryConsumeSingleToken() {
        return tryConsumeImpl(1);
    }

    @Override
    public boolean tryConsume(long tokens) {
        if (tokens <= 0) {
            throw nonPositiveTokensToConsume(tokens);
        }
        return tryConsumeImpl(tokens);
    }

    @Override
    public void consumeSingleToken() throws InterruptedException {
        consume(1);
    }

    @Override
    public void consume(long numTokens) throws InterruptedException {
        if (numTokens <= 0) {
            throw nonPositiveTokensToConsume(numTokens);
        }
        consumeOrAwaitImpl(numTokens, UNSPECIFIED_WAITING_LIMIT);
    }

    @Override
    public boolean tryConsumeSingleToken(Duration maxWaiting) throws InterruptedException {
        return tryConsume(1, maxWaiting);
    }

    @Override
    public boolean tryConsume(long numTokens, Duration maxWaiting) throws InterruptedException {
        long maxWaitNanos = maxWaiting.toNanos();
        if (numTokens <= 0) {
            throw nonPositiveTokensToConsume(numTokens);
        }

        if (maxWaitNanos <= 0) {
            throw nonPositiveNanosToWait(maxWaitNanos);
        }

        return consumeOrAwaitImpl(numTokens, maxWaitNanos);
    }

    @Override
    public long consumeAsMuchAsPossible(long limit) {
        if (limit <= 0) {
            throw nonPositiveTokensToConsume(limit);
        }
        return consumeAsMuchAsPossibleImpl(limit);
    }

    @Override
    public long consumeAsMuchAsPossible() {
        return consumeAsMuchAsPossibleImpl(Long.MAX_VALUE);
    }

    @Override
    public CompletableFuture<Boolean> tryConsumeSingleToken(Duration maxWaiting, ScheduledExecutorService scheduler) {
        return tryConsume(1, maxWaiting, scheduler);
    }

    @Override
    public CompletableFuture<Boolean> tryConsume(long numTokens, Duration maxWaiting, ScheduledExecutorService scheduler) {
        long maxWaitNanos = maxWaiting.toNanos();
        if (numTokens <= 0) {
            throw nonPositiveTokensToConsume(numTokens);
        }
        if (maxWaitNanos <= 0) {
            throw nonPositiveNanosToWait(maxWaitNanos);
        }
        return tryConsumeAsyncImpl(numTokens, maxWaitNanos, scheduler);
    }

    @Override
    public CompletableFuture<Void> consumeSingleTokenAsync(ScheduledExecutorService scheduler) {
        return consumeAsync(1, scheduler);
    }

    @Override
    public CompletableFuture<Void> consumeAsync(long numTokens, ScheduledExecutorService scheduler) {
        if (numTokens <= 0) {
            throw nonPositiveTokensToConsume(numTokens);
        }
        return tryConsumeAsyncImpl(numTokens, UNSPECIFIED_WAITING_LIMIT, scheduler).thenApply(bool -> null);
    }

    private static IllegalArgumentException nonPositiveTokensToConsume(long tokens) {
        String pattern = "Unable to consume {0} tokens, due to number of tokens to consume should be positive";
        String msg = MessageFormat.format(pattern, tokens);
        return new IllegalArgumentException(msg);
    }

    private static IllegalArgumentException nonPositiveNanosToWait(long waitIfBusyNanos) {
        String pattern = "Waiting value should be positive, {0} is wrong waiting period";
        String msg = MessageFormat.format(pattern, waitIfBusyNanos);
        return new IllegalArgumentException(msg);
    }

}