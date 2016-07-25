package com.github.bucket4j.common;

import com.github.bucket4j.Bucket;
import com.github.bucket4j.grid.GridBucket;
import com.github.bucket4j.grid.GridProxy;
import com.github.bucket4j.grid.coherence.CoherenceProxy;
import com.github.bucket4j.grid.hazelcast.HazelcastProxy;
import com.github.bucket4j.grid.ignite.IgniteProxy;
import com.github.bucket4j.jdbc.JdbcAdapter;
import com.github.bucket4j.jdbc.JdbcBucket;
import com.github.bucket4j.local.LockFreeBucket;
import com.github.bucket4j.statistic.DummyBucketStatistic;
import com.github.bucket4j.statistic.BucketStatistic;
import com.hazelcast.core.IMap;
import com.tangosol.net.NamedCache;
import org.apache.ignite.IgniteCache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class BucketBuilderImpl implements BucketBuilder {

    private BucketStatistic statistic = DummyBucketStatistic.INSTANCE;
    private TimeMeter timeMeter = TimeMeter.SYSTEM_MILLISECONDS;
    private List<Bandwidth> limitedBandwidths = new ArrayList<>(1);
    private Bandwidth guaranteedBandwidth;

    @Override
    public BucketBuilder addLimit(Bandwidth bandwidth) {
        limitedBandwidths.add(bandwidth);
        return this;
    }

    @Override
    public BucketBuilder withGuarantee(Bandwidth bandwidth) {
        this.guaranteedBandwidth = bandwidth;
        return this;
    }

    @Override
    public BucketBuilder withStatistic(BucketStatistic statistic) {
        this.statistic = Objects.requireNonNull(statistic);
        return this;
    }

    @Override
    public BucketBuilder withMillisecondPrecision() {
        this.timeMeter = TimeMeter.SYSTEM_MILLISECONDS;
        return this;
    }

    @Override
    public BucketBuilder withNanosecondPrecision() {
        this.timeMeter = TimeMeter.SYSTEM_NANOTIME;
        return this;
    }

    @Override
    public BucketBuilder withCustomTimeMeter(TimeMeter customTimeMeter) {
        this.timeMeter = Objects.requireNonNull(customTimeMeter);
        return this;
    }

    @Override
    public BucketBuilder withStatisticCollector(BucketStatistic bucketStatistic) {
        this.statisticSupplier =
        return this;
    }

    @Override
    public Bucket build() {
        return new LockFreeBucket(createStateWithConfiguration(), statisticSupplier);
    }

    @Override
    public Bucket buildHazelcast(IMap<Object, BucketState> imap, Serializable key) {
        return new GridBucket(createStateWithConfiguration(), new HazelcastProxy(imap, key));
    }

    @Override
    public Bucket buildIgnite(IgniteCache<Object, BucketState> cache, Serializable key) {
        return new GridBucket(createStateWithConfiguration(), new IgniteProxy(cache, key));
    }

    @Override
    public Bucket buildCoherence(NamedCache cache, Serializable key) {
        return new GridBucket(createStateWithConfiguration(), new CoherenceProxy(cache, key));
    }

    @Override
    public Bucket buildForCustomGrid(GridProxy gridProxy) {
        return new GridBucket(createStateWithConfiguration(), gridProxy);
    }

    @Override
    public <K, T> Bucket buildJdbcBucket(K primaryKey, JdbcAdapter<K, T> jdbcAdapter) {
        return new JdbcBucket(createStateWithConfiguration(), jdbcAdapter, primaryKey);
    }

    private InitialState createStateWithConfiguration() {
        return BucketState.createInitialState(timeMeter, limitedBandwidths, guaranteedBandwidth);
    }

    @Override
    public String toString() {
        return "BucketBuilder{" +
                "timeMeter=" + timeMeter +
                ", bandwidths=" + limitedBandwidths +
                '}';
    }

}
