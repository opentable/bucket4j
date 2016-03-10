package com.github.bucket4j.builder;

import com.github.bucket4j.Bucket;
import com.github.bucket4j.impl.grid.GridBucketState;
import com.github.bucket4j.impl.grid.GridProxy;
import com.github.bucket4j.statistic.StatisticCollector;
import com.hazelcast.core.IMap;
import com.tangosol.net.NamedCache;
import org.apache.ignite.IgniteCache;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * Created by vladimir.bukhtoyarov on 29.02.2016.
 */
public interface DistributedBucketBuilder extends BucketBuilder {

    BucketBuilder withLocalStatisticCollector(StatisticCollector statisticCollector);

    <T extends Supplier<StatisticCollector> & Serializable> BucketBuilder withRemoteStatisticCollector(T statisticCollectorSupplier);

    /**
     * Constructs an instance of {@link com.github.bucket4j.impl.grid.GridBucket} which responsible to limit rate inside Hazelcast cluster.
     *
     * @param imap distributed map which will hold bucket inside cluster.
     *             Feel free to store inside single {@code imap} as mush buckets as you need.
     * @param key  for storing bucket inside {@code imap}.
     *             If you plan to store multiple buckets inside single {@code imap}, then each bucket should has own unique {@code key}.
     *
     * @see com.github.bucket4j.impl.grid.hazelcast.HazelcastProxy
     */
    Bucket buildHazelcast(IMap<Object, GridBucketState> imap, Serializable key);

    /**
     * Constructs an instance of {@link com.github.bucket4j.impl.grid.GridBucket} which responsible to limit rate inside Apache Ignite(GridGain) cluster.
     *
     * @param cache distributed cache which will hold bucket inside cluster.
     *             Feel free to store inside single {@code cache} as mush buckets as you need.
     * @param key  for storing bucket inside {@code cache}.
     *             If you plan to store multiple buckets inside single {@code cache}, then each bucket should has own unique {@code key}.
     *
     * @see com.github.bucket4j.impl.grid.ignite.IgniteProxy
     */
    Bucket buildIgnite(IgniteCache<Object, GridBucketState> cache, Serializable key);

    /**
     * Constructs an instance of {@link com.github.bucket4j.impl.grid.GridBucket} which responsible to limit rate inside Oracle Coherence cluster.
     *
     * @param cache distributed cache which will hold bucket inside cluster.
     *             Feel free to store inside single {@code cache} as mush buckets as you need.
     * @param key  for storing bucket inside {@code cache}.
     *             If you plan to store multiple buckets inside single {@code cache}, then each bucket should has own unique {@code key}.
     *
     * @see com.github.bucket4j.impl.grid.coherence.CoherenceProxy
     */
    Bucket buildCoherence(NamedCache cache, Serializable key);

    /**
     * Build distributed bucket for custom grid which is not supported out of the box.
     *
     * @param gridProxy delegate for accessing to your grid.
     *
     * @see com.github.bucket4j.impl.grid.GridProxy
     */
    Bucket buildForCustomGrid(GridProxy gridProxy);

}
