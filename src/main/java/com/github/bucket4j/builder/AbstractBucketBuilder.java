package com.github.bucket4j.builder;

import com.github.bucket4j.*;
import com.github.bucket4j.impl.BucketConfiguration;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractBucketBuilder implements BucketBuilder {

    private TimeMeter timeMeter = TimeMeter.SYSTEM_MILLISECONDS;
    private List<BandwidthDefinition> bandwidths = new ArrayList<>(1);

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
    public BucketBuilder withCustomTimePrecision(TimeMeter customTimeMeter) {
        this.timeMeter = Objects.requireNonNull(customTimeMeter);
        return this;
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
