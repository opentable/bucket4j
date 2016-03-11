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

package com.github.bucket4j.builder;

import com.github.bucket4j.CapacityFunction;
import com.github.bucket4j.TimeMeter;

import java.time.Duration;

/**
 * A builder for buckets. Builder can be reused, i.e. one builder can create multiple buckets with similar configuration.
 *
 * @see com.github.bucket4j.impl.local.LockFreeBucket
 * @see com.github.bucket4j.impl.grid.GridBucket
 */
public interface BucketBuilder {

    /**
     * Configures {@link com.github.bucket4j.TimeMeter#SYSTEM_MILLISECONDS} as time meter.
     *
     * @return
     */
    BucketBuilder withMillisecondPrecision();

    /**
     * Configures {@link com.github.bucket4j.TimeMeter#SYSTEM_NANOTIME} as time meter.
     *
     * @return
     */
    BucketBuilder withNanosecondPrecision();

    /**
     * Configures {@code customTimeMeter} as time meter.
     *
     * @param customTimeMeter object which will measure time.
     *
     * @return
     */
    BucketBuilder withCustomTimePrecision(TimeMeter customTimeMeter);

    /**
     * Adds guaranteed bandwidth for all buckets which will be constructed by this builder instance.
     * <p>
     * Guaranteed bandwidth provides following feature: if tokens can be consumed from guaranteed bandwidth,
     * then bucket4j do not perform checking of any limited bandwidths.
     * <p>
     * Unlike limited bandwidths, you can use only one guaranteed bandwidth per single bucket.
     * <p>
     * Rate(which calculated as {@code maxCapacity/(timeUnit*period)}) of guaranteed bandwidth should be strongly lesser then rate of any limited bandwidth,
     * else you will get {@link java.lang.IllegalArgumentException} during construction of bucket.
     * <p>
     * <pre>
     * {@code
     * // Adds bandwidth which guarantees, that client of bucket will be able to consume 1 tokens per 10 minutes,
     * // regardless of limitations.
     * builder.withGuaranteedBandwidth(1, TimeUnit.MINUTES, 10);
     * }
     * </pre>
     *
     * @param maxCapacity the maximum capacity of bandwidth
     * @param timeUnit Unit for period.
     * @param period Period of bandwidth.
     *
     */
    BucketBuilder withGuaranteedBandwidth(long maxCapacity, Duration period);

    /**
     * Adds guaranteed bandwidth for all buckets which will be constructed by this builder instance.
     * <p>
     * Guaranteed bandwidth provides following feature: if tokens can be consumed from guaranteed bandwidth,
     * then bucket4j do not perform checking of any limited bandwidths.
     * <p>
     * Unlike limited bandwidths, you can use only one guaranteed bandwidth per single bucket.
     * <p>
     * Rate(which calculated as {@code maxCapacity/(timeUnit*period)}) of guaranteed bandwidth should be strongly lesser then rate of any limited bandwidth,
     * else you will get {@link java.lang.IllegalArgumentException} during construction of bucket.
     * <p>
     * <pre>
     * {@code
     * // Adds bandwidth which guarantees, that client of bucket will be able to consume 1 tokens per 10 minutes,
     * // regardless of limitations. Size of bandwidth is 0 after construction
     * builder.withGuaranteedBandwidth(2, TimeUnit.MINUTES, 1, 0);
     * }
     * </pre>
     *
     * @param maxCapacity the maximum capacity of bandwidth
     * @param timeUnit Unit for period.
     * @param initialCapacity initial capacity of bandwidth.
     * @param period Period of bandwidth.
     *
     */
    BucketBuilder withGuaranteedBandwidth(long maxCapacity, long initialCapacity, Duration period);

    /**
     * Adds guaranteed bandwidth for all buckets which will be constructed by this builder instance.
     * <p>
     * Guaranteed bandwidth provides following feature: if tokens can be consumed from guaranteed bandwidth,
     * then bucket4j do not perform checking of any limited bandwidths.
     * <p>
     * Unlike limited bandwidths, you can use only one guaranteed bandwidth per single bucket.
     * <p>
     *
     * In opposite to method {@link BucketBuilder#withGuaranteedBandwidth(long, long, Duration)} ,
     * this method does not perform checking of limitation which disallow to have greater rate of guaranteed than rate of limited bandwidth,
     * because rate is dynamic and depends from <code>bandwidthAdjuster</code>.
     *
     * @param capacityFunction provider of bandwidth capacity
     * @param initialCapacity initial capacity of bandwidth.
     * @param period Period of bandwidth.
     *
     */
    BucketBuilder withGuaranteedBandwidth(CapacityFunction capacityFunction, long initialCapacity, Duration period);

    /**
     * Adds limited bandwidth for all buckets which will be constructed by this builder instance.
     * <p>
     * You can specify as many limited bandwidth as needed, but with following limitation: each limited bandwidth should has unique period,
     * and when period of bandwidth <tt>X</tt> is greater than bandwidth <tt>Y</tt>,
     * then capacity of bandwidth <tt>X</tt> should be greater capacity of bandwidth <tt>Y</tt>,
     * except cases when capacity of bandwidth <tt>X</tt> or <tt>Y</tt> is dynamic(provided by {@link CapacityFunction}).
     * <p>
     * <pre>
     * {@code
     * // Adds bandwidth that restricts to consume not often 1 tokens per 10 minutes,
     * builder.withBandwidth(1, TimeUnit.MINUTES, 10);
     * }
     * </pre>
     *
     * @param maxCapacity the maximum capacity of bandwidth
     * @param timeUnit Unit for period.
     * @param period Period of bandwidth.
     *
     */
    BucketBuilder withBandwidth(long maxCapacity, Duration period);

    /**
     * Adds limited bandwidth for all buckets which will be constructed by this builder instance.
     * <p>
     * You can specify as many limited bandwidth as needed, but with following limitation: each limited bandwidth should has unique period,
     * and when period of bandwidth <tt>X</tt> is greater than bandwidth <tt>Y</tt>,
     * then capacity of bandwidth <tt>X</tt> should be greater capacity of bandwidth <tt>Y</tt>,
     * except cases when capacity of bandwidth <tt>X</tt> or <tt>Y</tt> is dynamic(provided by {@link CapacityFunction}).
     * <p>
     * <pre>
     * {@code
     * // Adds bandwidth that restricts to consume not often 1 tokens per 10 minutes, and initial capacity 0.
     * builder.withBandwidth(1, TimeUnit.MINUTES, 10, 0);
     * }
     * </pre>
     *
     * @param maxCapacity the maximum capacity of bandwidth
     * @param initialCapacity initial capacity
     * @param period Period of bandwidth.
     *
     */
    BucketBuilder withBandwidth(long maxCapacity, long initialCapacity, Duration period);

    /**
     * Adds limited bandwidth for all buckets which will be constructed by this builder instance.
     * <p>
     * You can specify as many limited bandwidth as needed, but with following limitation: each limited bandwidth should has unique period,
     * and when period of bandwidth <tt>X</tt> is greater than bandwidth <tt>Y</tt>,
     * then capacity of bandwidth <tt>X</tt> should be greater capacity of bandwidth <tt>Y</tt>,
     * except cases when capacity of bandwidth <tt>X</tt> or <tt>Y</tt> is dynamic(provided by {@link CapacityFunction}).
     *
     * @param capacityFunction provider of bandwidth capacity
     * @param initialCapacity initial capacity
     * @param period Period of bandwidth.
     *
     */
    BucketBuilder withBandwidth(CapacityFunction capacityFunction, long initialCapacity, Duration period);

}
