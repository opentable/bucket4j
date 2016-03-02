package com.github.bucket4j.impl;

import com.github.bucket4j.*;
import com.github.bucket4j.impl.grid.GridBucket;
import com.github.bucket4j.impl.grid.GridBucketState;
import com.github.bucket4j.impl.grid.GridProxy;
import com.github.bucket4j.impl.grid.coherence.CoherenceProxy;
import com.github.bucket4j.impl.grid.hazelcast.HazelcastProxy;
import com.github.bucket4j.impl.grid.ignite.IgniteProxy;
import com.github.bucket4j.impl.local.LockFreeBucket;
import com.github.bucket4j.statistic.DummyStatisticCollector;
import com.github.bucket4j.statistic.StatisticCollector;
import com.hazelcast.core.IMap;
import com.tangosol.net.NamedCache;
import org.apache.ignite.IgniteCache;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.github.bucket4j.impl.BucketExceptions.nullTimeMetter;

public class BucketBuilderImpl implements BucketBuilder, DistributedBucketBuilder, LocalBucketBuilder {

    private TimeMeter timeMeter = TimeMeter.SYSTEM_MILLISECONDS;
    private List<BandwidthDefinition> bandwidths = new ArrayList<>(1);
    private StatisticCollector statisticCollector = DummyStatisticCollector.INSTANCE;

    @Override
    public BucketBuilder useMillisecondPrecision() {
        this.timeMeter = TimeMeter.SYSTEM_MILLISECONDS;
        return this;
    }

    @Override
    public BucketBuilder useNanosecondPrecision() {
        this.timeMeter = TimeMeter.SYSTEM_NANOTIME;
        return this;
    }

    @Override
    public BucketBuilder withCustomTimePrecision(TimeMeter customTimeMeter) {
        if (timeMeter == null) {
            throw nullTimeMetter();
        }
        this.timeMeter = customTimeMeter;
        return this;
    }

    @Override
    public BucketBuilder withStatisticCollector(StatisticCollector statisticCollector) {
        if (statisticCollector == null) {
            throw new NullPointerException("Statistic collector can not be NULL");
        }
        this.statisticCollector = statisticCollector;
        return this;
    }

    @Override
    public Bucket build() {
        BucketConfiguration configuration = createConfiguration();
        return new LockFreeBucket(configuration);
    }

    @Override
    public BucketBuilder withLocalStatisticCollector(StatisticCollector statisticCollector) {
        // TODO
        return null;
    }

    @Override
    public BucketBuilder withRemoteStatisticCollector(Supplier<StatisticCollector> statisticCollector) {
        // TODO
        return null;
    }

    @Override
    public Bucket buildHazelcast(IMap<Object, GridBucketState> imap, Serializable key) {
        BucketConfiguration configuration = createConfiguration();
        return new GridBucket(configuration, new HazelcastProxy(imap, key));
    }

    @Override
    public Bucket buildIgnite(IgniteCache<Object, GridBucketState> cache, Object key) {
        BucketConfiguration configuration = createConfiguration();
        return new GridBucket(configuration, new IgniteProxy(cache, key));
    }

    @Override
    public Bucket buildCoherence(NamedCache cache, Object key) {
        BucketConfiguration configuration = createConfiguration();
        return new GridBucket(configuration, new CoherenceProxy(cache, key));
    }

    @Override
    public Bucket buildCustomGrid(GridProxy gridProxy) {
        BucketConfiguration configuration = createConfiguration();
        return new GridBucket(configuration, gridProxy);
    }

    @Override
    public BucketBuilder withGuaranteedBandwidth(long maxCapacity, Duration period) {
        return withGuaranteedBandwidth(maxCapacity, maxCapacity, period);
    }

    @Override
    public BucketBuilder withGuaranteedBandwidth(long maxCapacity, long initialCapacity, Duration period) {
        final BandwidthDefinition bandwidth = new BandwidthDefinition(maxCapacity, initialCapacity, period, true);
        bandwidths.add(bandwidth);
        return this;
    }

    @Override
    public BucketBuilder withGuaranteedBandwidth(CapacityFunction capacityFunction, long initialCapacity, Duration period) {
        final BandwidthDefinition bandwidth = new BandwidthDefinition(capacityFunction, initialCapacity, period, true);
        bandwidths.add(bandwidth);
        return this;
    }

    @Override
    public BucketBuilder withLimitedBandwidth(long maxCapacity, Duration period) {
        return withLimitedBandwidth(maxCapacity, maxCapacity, period);
    }

    @Override
    public BucketBuilder withLimitedBandwidth(long maxCapacity, long initialCapacity, Duration period) {
        final BandwidthDefinition bandwidth = new BandwidthDefinition(maxCapacity, initialCapacity, period, false);
        bandwidths.add(bandwidth);
        return this;
    }

    @Override
    public BucketBuilder withLimitedBandwidth(CapacityFunction capacityFunction, long initialCapacity, Duration period) {
        final BandwidthDefinition bandwidth = new BandwidthDefinition(capacityFunction, initialCapacity, period, false);
        bandwidths.add(bandwidth);
        return this;
    }

    public BandwidthDefinition getBandwidthDefinition(int index) {
        return bandwidths.get(index);
    }

    public BucketConfiguration createConfiguration() {
        return new BucketConfiguration(this.bandwidths, timeMeter);
    }

    public TimeMeter getTimeMeter() {
        return timeMeter;
    }

    @Override
    public String toString() {
        return "BucketBuilder{" +
                "timeMeter=" + timeMeter +
                ", bandwidths=" + bandwidths +
                '}';
    }

}
