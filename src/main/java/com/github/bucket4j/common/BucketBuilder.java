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
import com.github.bucket4j.grid.GridProxy;
import com.github.bucket4j.jdbc.JdbcAdapter;
import com.github.bucket4j.statistic.BucketStatistic;
import com.hazelcast.core.IMap;
import com.tangosol.net.NamedCache;
import org.apache.ignite.IgniteCache;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * A builder for buckets. Builder can be reused, i.e. one builder can create multiple buckets with similar configuration.
 *
 * @see com.github.bucket4j.local.LockFreeBucket
 * @see com.github.bucket4j.grid.GridBucket
 * @see com.github.bucket4j.jdbc.JdbcBucket
 */
public interface BucketBuilder {

    /**
     * Adds limited bandwidth for all buckets which will be constructed by this builder instance.
     * <p>
     * You can specify as many limited bandwidth as needed.
     *
     * @param bandwidth
     *
     * @return this builder instance
     */
    BucketBuilder addLimit(Bandwidth bandwidth);

    /**
     * Set guaranteed bandwidth for all buckets which will be constructed by this builder instance.
     * <p>
     * Guaranteed bandwidth provides following feature: if tokens can be consumed from guaranteed bandwidth,
     * then bucket4j do not perform checking of any limited bandwidths.
     * <p>
     * Unlike limited bandwidths, you can use only one guaranteed bandwidth per single bucket.
     *
     * @param bandwidth
     *
     * @return this builder instance
     * @throws IllegalApiUsageException if guarantee already set
     */
    BucketBuilder withGuarantee(Bandwidth bandwidth);

    /**
     * Configures the bucket static.
     *
     * @param statistic bucket static.
     *
     * @return this builder instance
     */
    BucketBuilder withStatistic(BucketStatistic statistic);

    /**
     * Configures {@link TimeMeter#SYSTEM_MILLISECONDS} as time meter.
     *
     * @return this builder instance
     */
    BucketBuilder withMillisecondPrecision();

    /**
     * Configures {@link TimeMeter#SYSTEM_NANOTIME} as time meter.
     *
     * @return this builder instance
     */
    BucketBuilder withNanosecondPrecision();

    /**
     * Configures {@code customTimeMeter} as time meter.
     *
     * @param customTimeMeter object which will measure time.
     *
     * @return this builder instance
     */
    BucketBuilder withCustomTimeMeter(TimeMeter customTimeMeter);

    /**
     * Constructs an instance of {@link com.github.bucket4j.local.LockFreeBucket}
     *
     * @return an instance of {@link com.github.bucket4j.local.LockFreeBucket}
     */
    Bucket build();

    <K, T> Bucket buildJdbcBucket(K premaryKey, JdbcAdapter<K, T> jdbcAdapter);

    /**
     * Constructs an instance of {@link com.github.bucket4j.grid.GridBucket} which responsible to limit rate inside Hazelcast cluster.
     *
     * @param imap distributed map which will hold bucket inside cluster.
     *             Feel free to store inside single {@code imap} as mush buckets as you need.
     * @param key  for storing bucket inside {@code imap}.
     *             If you plan to store multiple buckets inside single {@code imap}, then each bucket should has own unique {@code key}.
     *
     * @see com.github.bucket4j.grid.hazelcast.HazelcastProxy
     */
    Bucket buildHazelcast(IMap<Object, BucketState> imap, Serializable key);

    /**
     * Constructs an instance of {@link com.github.bucket4j.grid.GridBucket} which responsible to limit rate inside Apache Ignite(GridGain) cluster.
     *
     * @param cache distributed cache which will hold bucket inside cluster.
     *             Feel free to store inside single {@code cache} as mush buckets as you need.
     * @param key  for storing bucket inside {@code cache}.
     *             If you plan to store multiple buckets inside single {@code cache}, then each bucket should has own unique {@code key}.
     *
     * @see com.github.bucket4j.grid.ignite.IgniteProxy
     */
    Bucket buildIgnite(IgniteCache<Object, BucketState> cache, Serializable key);

    /**
     * Constructs an instance of {@link com.github.bucket4j.grid.GridBucket} which responsible to limit rate inside Oracle Coherence cluster.
     *
     * @param cache distributed cache which will hold bucket inside cluster.
     *             Feel free to store inside single {@code cache} as mush buckets as you need.
     * @param key  for storing bucket inside {@code cache}.
     *             If you plan to store multiple buckets inside single {@code cache}, then each bucket should has own unique {@code key}.
     *
     * @see com.github.bucket4j.grid.coherence.CoherenceProxy
     */
    Bucket buildCoherence(NamedCache cache, Serializable key);

    /**
     * Build distributed bucket for custom grid which is not supported out of the box.
     *
     * @param gridProxy delegate for accessing to your grid.
     *
     * @see com.github.bucket4j.grid.GridProxy
     */
    Bucket buildForCustomGrid(GridProxy gridProxy);

}
