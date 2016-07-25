/*
 * Copyright 2016 Vladimir Bukhtoyarov
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

import com.github.bucket4j.common.BucketBuilder;
import com.github.bucket4j.common.BucketBuilderImpl;
import com.github.bucket4j.builder.DistributedBucketBuilder;
import com.github.bucket4j.builder.LocalBucketBuilder;
import com.github.bucket4j.common.IllegalApiUsageException;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

/**
 * The continuous-state leaky bucket can be viewed as a finite capacity bucket
 * whose real-valued content drains out at a continuous rate of 1 unit of content per time unit
 * and whose content is increased by the increment T for each conforming cell...
 * If at a cell arrival the content of the bucket is less than or equal to the limit value τ, then the cell is conforming;
 * otherwise, the cell is non-conforming.
 * <p>
 * <p>Simple example of usage:
 * <code><pre>
 * Bucket bucket = Bucket.builder().addLimit(100, Duration.ofMinutes(1)).build();
 * ...
 * if (bucket.tryConsumeSingleToken()) {
 *     doSomething();
 * }
 * <pre/>
 * </code>
 * <p>
 * @see <a href="http://en.wikipedia.org/wiki/Token_bucket">Token Bucket</a>
 * @see <a href="http://en.wikipedia.org/wiki/Leaky_bucket">Leaky Bucket</a>
 * @see <a href="http://en.wikipedia.org/wiki/Generic_cell_rate_algorithm">Generic cell rate algorithm</a>
 */
public interface Bucket {

    /**
     * @return builder which performs bucket construction.
     */
    static BucketBuilder builder() {
        return new BucketBuilderImpl();
    }

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
     * @param numTokens The number of tokens to consume from the bucket.
     * @return {@code true} if the tokens were consumed, {@code false} otherwise.
     * @throws IllegalApiUsageException if the requested number of tokens is negative or zero
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
     * @param limit maximum number of tokens to consume
     * @return number of tokens which has been consumed, or zero if was consumed nothing.
     * @throws IllegalApiUsageException if the specified {@code limit} is negative or zero
     */
    long consumeAsMuchAsPossible(long limit);

    /**
     * Consumes the single token from this {@code Bucket} if it can be obtained
     * without exceeding the specified {@code maxWaiting}, or returns {@code false}
     * immediately (without waiting) if the tokens would not have been granted
     * before the timeout expired.
     * <p>
     * <p>This is equivalent for {@code tryConsume(1, maxWaiting)}
     *
     * @param maxWaiting limit of time which thread can wait.
     * @return true if token has been consumed or false when token has not been consumed
     * @throws InterruptedException in case of current thread has been interrupted during waiting
     * @throws IllegalApiUsageException if the maxWaiting represents a negative or zero duration
     */
    boolean tryConsumeSingleToken(Duration maxWaiting) throws InterruptedException;

    /**
     * This is asynchronous version for {@code tryConsumeSingleToken(maxWaiting)} allows to avoid blocking execution thread.
     * <p>
     * <ol> Algorithm is following:
     * <li>If token is currently available then this method will consume token and return {@code CompletableFuture.completedFuture(true)} immediately.<li/>
     * <li>If the token would not have been granted before the {@code maxWaiting} timeout expired then this method will return {@code CompletableFuture.completedFuture(false)} immediately.</li>
     * <li>If token is not currently available, but it can be obtained without exceeding the specified {@code maxWaiting} then this method will submit task to {@code scheduler}
     * with delay required to refill token, when task will be executed by scheduler then future returned by this method will be completed by {@code true}</li>
     * </ol>
     * <p>
     * <p>This is equivalent for {@code tryConsume(1, maxWaiting, scheduler)}
     *
     * @param maxWaiting limit of time which thread can wait.
     * @param scheduler  it will be used to avoid blocling current execution  thread in case of
     * @return CompletableFuture which represents result
     * @throws IllegalApiUsageException if the maxWaiting represents a negative or zero duration
     */
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
     * @throws IllegalApiUsageException if the requested number of numTokens is negative or zero
     * @throws IllegalApiUsageException if the maxWaiting represents a negative or zero duration
     */
    boolean tryConsume(long numTokens, Duration maxWaiting) throws InterruptedException;

    /**
     * This is asynchronous version for {@code tryConsumeSingleToken(numTokens, maxWaiting)} allows to avoid blocking execution thread.
     * <p>
     * <ol> Algorithm is following:
     * <li>If numTokens are currently available then this method will consume numTokens and return {@code CompletableFuture.completedFuture(true)} immediately.<li/>
     * <li>If the numTokens would not have been granted before the {@code maxWaiting} timeout expired then this method will return {@code CompletableFuture.completedFuture(false)} immediately.</li>
     * <li>If numTokens are not currently available, but it can be obtained without exceeding the specified {@code maxWaiting} then this method will submit task to {@code scheduler}
     * with delay required to refill numTokens, when task will be executed by scheduler then future returned by this method will be completed by {@code true}</li>
     * </ol>
     *
     * @param numTokens  The number of tokens to consumeSingleToken from the bucket.
     * @param maxWaiting limit of time which thread can wait.
     * @param scheduler  it will be used to avoid blocking current execution  thread
     * @return CompletableFuture which represents result
     * @throws IllegalApiUsageException if the requested number of numTokens is negative or zero
     * @throws IllegalApiUsageException if the maxWaiting represents a negative or zero duration
     */
    CompletableFuture<Boolean> tryConsume(long numTokens, Duration maxWaiting, ScheduledExecutorService scheduler);

    /**
     * Consumes a single token from the bucket.  If no token is currently available then this method will block until a
     * token becomes available or current thread is interrupted.
     * <p>
     * <p> This is equivalent for {@code consume(1)}.
     * <p>
     * <p>Be careful when using this method, because time spent in sleeping can be too long,
     * use instead {@code tryConsumeSingleToken(anyDuration)} where possible.
     *
     * @throws InterruptedException in case of current thread has been interrupted during waiting
     */
    void consumeSingleToken() throws InterruptedException;

    /**
     * This is asynchronous version for {@code consumeSingleToken()} allows to avoid blocking execution thread.
     * <p>
     * <p>Consumes a single token from the bucket.
     * <ol> Algorithm is following:
     * <li>If numTokens are currently available then this method will consume numTokens and return {@code CompletableFuture.completedFuture(true)} immediately.<li/>
     * <li>Otherwise this method will submit task to {@code scheduler} with delay required to refill one token, and after task will be executed future returned by this method will be completed</li>
     * </ol>
     *
     * @param scheduler it will be used to avoid blocking current execution thread
     * @return representation of future result
     */
    CompletableFuture<Void> consumeSingleTokenAsync(ScheduledExecutorService scheduler);

    /**
     * Consumes numTokens from the bucket. If enough tokens are not currently available then this method will block
     * until required number of tokens will be available or current thread is interrupted.
     * <p>
     * <p>Be careful when using this method, because time spent in sleeping can be too long,
     * use instead {@code tryConsumeSingleToken(numTokens, anyDuration)} where possible.
     *
     * @param numTokens The number of tokens to consumeSingleToken from the bucket.
     * @throws InterruptedException     in case of current thread has been interrupted during waiting
     * @throws IllegalApiUsageException if the requested number of numTokens is negative or zero
     */
    void consume(long numTokens) throws InterruptedException;

    /**
     * This is asynchronous version for {@code consume(numTokens)} allows to avoid blocking execution thread.
     * <p>
     * <p>Consumes numTokens from the bucket.
     * <ol> Algorithm is following:
     * <li>If numTokens are currently available then this method will consume numTokens and return {@code CompletableFuture.completedFuture(Void)} immediately.<li/>
     * <li>Otherwise this method will submit task to {@code scheduler} with delay required to refill one numTokens, and after task will be executed future returned by this method will be completed</li>
     * </ol>
     *
     * @param scheduler it will be used to avoid blocking current execution thread
     * @return representation of future result
     * @throws IllegalApiUsageException if the requested number of numTokens is negative or zero
     */
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

}
