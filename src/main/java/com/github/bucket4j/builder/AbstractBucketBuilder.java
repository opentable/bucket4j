package com.github.bucket4j.builder;

import com.github.bucket4j.common.TimeMeter;
import com.github.bucket4j.impl.ConstantCapacity;
import com.github.bucket4j.common.SmoothlyRenewableBandwidthState;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractBucketBuilder implements BucketBuilder {

    private TimeMeter timeMeter = TimeMeter.SYSTEM_MILLISECONDS;
    private List<SmoothlyRenewableBandwidthState> bandwidths = new ArrayList<>(1);

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
    public BucketBuilder withGuaranteedBandwidth(long maxCapacity, Duration period) {
        return withGuaranteedBandwidth(maxCapacity, maxCapacity, period);
    }

    @Override
    public BucketBuilder withGuaranteedBandwidth(long maxCapacity, long initialCapacity, Duration period) {
        final SmoothlyRenewableBandwidthState bandwidth = new SmoothlyRenewableBandwidthState(maxCapacity, initialCapacity, period, true);
        bandwidths.add(bandwidth);
        return this;
    }

    @Override
    public BucketBuilder withGuaranteedBandwidth(Capacity capacity, Duration period) {
        final SmoothlyRenewableBandwidthState bandwidth = new SmoothlyRenewableBandwidthState(capacity, initialCapacity, period, true);
        bandwidths.add(bandwidth);
        return this;
    }

    @Override
    public BucketBuilder withLimitedBandwidth(long maxCapacity, Duration period) {
        return withLimitedBandwidth(maxCapacity, maxCapacity, period);
    }

    @Override
    public BucketBuilder withLimitedBandwidth(long maxCapacity, long initialCapacity, Duration period) {
        final SmoothlyRenewableBandwidthState bandwidth = new SmoothlyRenewableBandwidthState(maxCapacity, initialCapacity, period, false);
        bandwidths.add(bandwidth);
        return this;
    }

    @Override
    public BucketBuilder withLimitedBandwidth(Capacity capacity, Duration period) {
        final SmoothlyRenewableBandwidthState bandwidth = new SmoothlyRenewableBandwidthState(capacity, period.toNanos(), false);
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

    @Override
    public String toString() {
        return "BucketBuilder{" +
                "timeMeter=" + timeMeter +
                ", bandwidths=" + bandwidths +
                '}';
    }

}
