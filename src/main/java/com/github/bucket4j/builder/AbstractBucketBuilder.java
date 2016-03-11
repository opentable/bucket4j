package com.github.bucket4j.builder;

import com.github.bucket4j.*;
import com.github.bucket4j.impl.BucketConfiguration;

import java.text.MessageFormat;
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
    public BucketBuilder withBandwidth(long maxCapacity, Duration period) {
        return withBandwidth(maxCapacity, maxCapacity, period);
    }

    @Override
    public BucketBuilder withBandwidth(long maxCapacity, long initialCapacity, Duration period) {
        final BandwidthDefinition bandwidth = new BandwidthDefinition(maxCapacity, initialCapacity, period, false);
        bandwidths.add(bandwidth);
        return this;
    }

    @Override
    public BucketBuilder withBandwidth(CapacityFunction capacityFunction, long initialCapacity, Duration period) {
        final BandwidthDefinition bandwidth = new BandwidthDefinition(capacityFunction, initialCapacity, period, false);
        bandwidths.add(bandwidth);
        return this;
    }

    private static IllegalArgumentException nonPositiveCapacity(long capacity) {
        String pattern = "{0} is wrong value for capacity, because capacity should be positive";
        String msg = MessageFormat.format(pattern, capacity);
        return new IllegalArgumentException(msg);
    }

    private static IllegalArgumentException nonPositiveInitialCapacity(long initialCapacity) {
        String pattern = "{0} is wrong value for initial capacity, because initial capacity should be positive";
        String msg = MessageFormat.format(pattern, initialCapacity);
        return new IllegalArgumentException(msg);
    }

    private static IllegalArgumentException nullBandwidthAdjuster() {
        String msg = "Bandwidth adjuster can not be null";
        return new IllegalArgumentException(msg);
    }

    private static IllegalArgumentException nonPositivePeriod(long period) {
        String pattern = "{0} is wrong value for period of bandwidth, because period should be positive";
        String msg = MessageFormat.format(pattern, period);
        return new IllegalArgumentException(msg);
    }

    private static IllegalArgumentException restrictionsNotSpecified() {
        String msg = "At list one limited bandwidth should be specified";
        return new IllegalArgumentException(msg);
    }

    private static IllegalArgumentException onlyOneGuarantedBandwidthSupported() {
        String msg = "Only one guaranteed bandwidth supported";
        return new IllegalArgumentException(msg);
    }

    private static IllegalArgumentException guarantedHasGreaterRateThanLimited(BandwidthDefinition guaranteed, BandwidthDefinition limited) {
        String pattern = "Misconfiguration detected, guaranteed bandwidth {0} has higher rate than limited bandwidth {1}";
        String msg = MessageFormat.format(pattern, guaranteed, limited);
        return new IllegalArgumentException(msg);
    }

    private static IllegalArgumentException hasOverlaps(BandwidthDefinition first, BandwidthDefinition second) {
        String pattern = "Overlap detected between {0} and {1}";
        String msg = MessageFormat.format(pattern, first, second);
        return new IllegalArgumentException(msg);
    }

    private BandwidthDefinition getBandwidthDefinition(int index) {
        return bandwidths.get(index);
    }

    private BucketConfiguration createConfiguration() {
        return new BucketConfiguration(this.bandwidths, timeMeter);
    }

    private TimeMeter getTimeMeter() {
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
