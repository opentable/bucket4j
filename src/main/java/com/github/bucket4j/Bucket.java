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

import com.github.bucket4j.impl.BucketConfiguration;
import com.github.bucket4j.impl.BucketState;
import com.github.bucket4j.statistic.StatisticSnapshot;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

/**
 * The continuous-state leaky bucket can be viewed as a finite capacity bucket
 * whose real-valued content drains out at a continuous rate of 1 unit of content per time unit
 * and whose content is increased by the increment T for each conforming cell...
 * If at a cell arrival the content of the bucket is less than or equal to the limit value τ, then the cell is conforming;
 * otherwise, the cell is non-conforming.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Token_bucket">Token Bucket</a>
 * @see <a href="http://en.wikipedia.org/wiki/Leaky_bucket">Leaky Bucket</a>
 * @see <a href="http://en.wikipedia.org/wiki/Generic_cell_rate_algorithm">Generic cell rate algorithm</a>
 */
public interface Bucket {

    /**
     * Attempts to consume a single token from the bucket.  If it was consumed then {@code true} is returned, otherwise
     * {@code false} is returned. This is equivalent for {@code tryConsume(1)}
     *
     * @return {@code true} if a token was consumed, {@code false} otherwise.
     */
    boolean tryConsumeSingleToken();

    /**
     * Attempt to consume a specified number of tokens from the bucket.  If the tokens were consumed then {@code true}
     * is returned, otherwise {@code false} is returned.
     *
     * @param numTokens The number of tokens to consume from the bucket, must be a positive number.
     * @return {@code true} if the tokens were consumed, {@code false} otherwise.
     * @throws IllegalArgumentException if the requested number of tokens is negative or zero
     */
    boolean tryConsume(long numTokens);

    /**
     * Consumes as much tokens from bucket as available in the bucket in moment of invocation.
     *
     * @return number of tokens which has been consumed, or zero if was consumed nothing.
     */
    long consumeAsMuchAsPossible();

    /**
     * Consumes as much tokens from bucket as available in the bucket at moment of invocation,
     * but tokens which should be consumed is limited by than not more than {@code limit}.
     *
     * @param limit maximum nubmer of tokens to consume
     * @return number of tokens which has been consumed, or zero if was consumed nothing.
     * @throws IllegalArgumentException if the specified {@code limit} is negative or zero
     */
    long consumeAsMuchAsPossible(long limit);

    /**
     * Consumes single token from this {@link Bucket} if it can be acquired immediately without
     * delay.
     * <p>
     * <p>This is equivalent for {@code tryConsume(1, maxWaiting)}
     *
     * @param maxWaiting limit of time which thread can wait.
     * @return true if token has been consumed or false when token has not been consumed
     * @throws InterruptedException in case of current thread has been interrupted during waiting
     */
    boolean tryConsumeSingleToken(Duration maxWaiting) throws InterruptedException;

    CompletableFuture<Boolean> tryConsumeSingleToken(Duration maxWaiting, ScheduledExecutorService scheduler);

    /**
     * Consumes the given number of tokens from this {@code Bucket} if it can be obtained
     * without exceeding the specified {@code maxWaiting}, or returns {@code false}
     * immediately (without waiting) if the tokens would not have been granted
     * before the timeout expired.
     *
     * @param numTokens  The number of tokens to consume from the bucket.
     * @param maxWaiting limit of time which thread can wait.
     * @return {@code true} if {@code numTokens} has been consumed or {@code false} otherwise
     * @throws InterruptedException     in case of current thread has been interrupted during waiting
     * @throws IllegalArgumentException if the requested number of numTokens is negative or zero
     */
    boolean tryConsume(long numTokens, Duration maxWaiting) throws InterruptedException;

    CompletableFuture<Boolean> tryConsume(long numTokens, Duration maxWaiting, ScheduledExecutorService scheduler) throws InterruptedException;

    /**
     * Consumes a single token from the bucket.  If no token is currently available then this method will block until a
     * token becomes available or current thread is interrupted.
     *
     * <p> This is equivalent for {@code consume(1)}.
     *
     * <p>Be careful when using this method, because time spent in sleeping can be too long,
     * use instead {@code tryConsumeSingleToken(anyDuration)} where possible.
     *
     * @return time spent sleeping to enforce rate, in nanoseconds; 0 if not rate-limited
     * @throws InterruptedException in case of current thread has been interrupted during waiting
     */
    void consumeSingleToken() throws InterruptedException;

    CompletableFuture<Void> consumeSingleTokenAsync(ScheduledExecutorService scheduler);

    /**
     * Consumes numTokens from the bucket. If enough tokens are not currently available then this method will block
     * until required number of tokens will be available or current thread is interrupted.
     *
     * <p>Be careful when using this method, because time spent in sleeping can be too long,
     * use instead {@code tryConsumeSingleToken(numTokens, anyDuration)} where possible.
     *
     * @param numTokens The number of tokens to consumeSingleToken from the bucket, must be a positive number.
     * @return time spent sleeping to enforce rate, in nanoseconds; 0 if not rate-limited
     * @throws InterruptedException     in case of current thread has been interrupted during waiting
     * @throws IllegalArgumentException if the requested number of tokens is negative or zero
     */
    void consume(long numTokens) throws InterruptedException;

    CompletableFuture<Void> consumeAsync(long numTokens, ScheduledExecutorService scheduler);

    /**
     * Returns number of tokens which can be consumed immediately.
     * Pay attention that because of concurrent nature of bucket may happen situation when {@code getAvailableTokens}
     * returns {@code N} but next invocation of {@code tryConsume(N)} return {@code false}
     * because some parallel thread can consume any tokens between {@code getAvailableTokens} and {@code tryConsume} invocations.
     *
     * @return Number of tokens which can be consumed immediately. 0 in case of no tokens available in the bucket
     */
    long getAvailableTokens();

    /**
     * Returns (previously consumed) tokens to the bucket. This method can be used with transactional semantic in following scenario:
     * <ol>
     * <li>Acquire N tokens from bucket.</li>
     * <li>Do something with transactional resource protected by this bucket.</li>
     * <li>If something went wrong with the resource, then return back N tokens.</li>
     * </ol>
     * <p>
     * Be careful when using this method, because:
     * <ul>
     * <li>Bucket is unable to distinguish case when you are trying to return tokens which were not consumed previously.</li>
     * <li>Tokens which consumed from guaranteed bandwidth are never returning back to this type of bandwidth.</li>
     * </ul>
     *
     * @param numTokens number of tokens which need to return to the bucket
     * @throws IllegalArgumentException if the number of numTokens to return is negative or zero
     */
    void returnTokens(long numTokens);

    /**
     * Creates snapshot of bucket statistic which actual on the moment of invocation.
     *
     * @return snapshot of bucket statistic
     * @throws IllegalStateException if statistic collector is not configured for this bucket during construction
     */
    StatisticSnapshot getStatisticSnapshot();

    /**
     * Returns configuration of bucket.
     *
     * @return Bucket configuration
     */
    BucketConfiguration getConfiguration();

    /**
     * Creates snapshot of bucket state.
     *
     * @return Snapshot of bucket state
     */
    BucketState getStateSnapshot();

    /**
     * Restore bucket state from snapshot.
     *
     * @param snapshot previously snapshot
     */
    void applySnapshot(BucketState snapshot);

}
