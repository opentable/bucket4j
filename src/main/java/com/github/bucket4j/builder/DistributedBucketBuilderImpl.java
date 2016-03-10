package com.github.bucket4j.builder;

import com.github.bucket4j.Bucket;
import com.github.bucket4j.impl.BucketConfiguration;
import com.github.bucket4j.impl.grid.GridBucket;
import com.github.bucket4j.impl.grid.GridBucketState;
import com.github.bucket4j.impl.grid.GridProxy;
import com.github.bucket4j.impl.grid.coherence.CoherenceProxy;
import com.github.bucket4j.impl.grid.hazelcast.HazelcastProxy;
import com.github.bucket4j.impl.grid.ignite.IgniteProxy;
import com.github.bucket4j.statistic.StatisticCollector;
import com.hazelcast.core.IMap;
import com.tangosol.net.NamedCache;
import org.apache.ignite.IgniteCache;

import java.io.Serializable;
import java.util.function.Supplier;

public class DistributedBucketBuilderImpl extends AbstractBucketBuilder implements DistributedBucketBuilder {

    @Override
    public BucketBuilder withLocalStatisticCollector(StatisticCollector statisticCollector) {
        // TODO
        return null;
    }

    @Override
    public <T extends Supplier<StatisticCollector> & Serializable> BucketBuilder withRemoteStatisticCollector(T statisticCollectorSupplier) {
        // TODO
        return null;
    }

    @Override
    public Bucket buildHazelcast(IMap<Object, GridBucketState> imap, Serializable key) {
        BucketConfiguration configuration = createConfiguration();
        return new GridBucket(configuration, new HazelcastProxy(imap, key));
    }

    @Override
    public Bucket buildIgnite(IgniteCache<Object, GridBucketState> cache, Serializable key) {
        BucketConfiguration configuration = createConfiguration();
        return new GridBucket(configuration, new IgniteProxy(cache, key));
    }

    @Override
    public Bucket buildCoherence(NamedCache cache, Serializable key) {
        BucketConfiguration configuration = createConfiguration();
        return new GridBucket(configuration, new CoherenceProxy(cache, key));
    }

    @Override
    public Bucket buildForCustomGrid(GridProxy gridProxy) {
        BucketConfiguration configuration = createConfiguration();
        return new GridBucket(configuration, gridProxy);
    }

}
