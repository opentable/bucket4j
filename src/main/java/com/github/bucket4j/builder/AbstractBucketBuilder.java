package com.github.bucket4j.builder;

import com.github.bucket4j.*;
import com.github.bucket4j.impl.ConstantCapacity;
import com.github.bucket4j.impl.SmoothlyRefillingBandwidthBandwidth;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractBucketBuilder implements BucketBuilder {

    private TimeMeter timeMeter = TimeMeter.SYSTEM_MILLISECONDS;
    private List<SmoothlyRefillingBandwidthBandwidth> bandwidths = new ArrayList<>(1);

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
        final SmoothlyRefillingBandwidthBandwidth bandwidth = new SmoothlyRefillingBandwidthBandwidth(maxCapacity, initialCapacity, period, true);
        bandwidths.add(bandwidth);
        return this;
    }

    @Override
    public BucketBuilder withGuaranteedBandwidth(Capacity capacity, Duration period) {
        final SmoothlyRefillingBandwidthBandwidth bandwidth = new SmoothlyRefillingBandwidthBandwidth(capacity, initialCapacity, period, true);
        bandwidths.add(bandwidth);
        return this;
    }

    @Override
    public BucketBuilder withLimitedBandwidth(long maxCapacity, Duration period) {
        return withLimitedBandwidth(maxCapacity, maxCapacity, period);
    }

    @Override
    public BucketBuilder withLimitedBandwidth(long maxCapacity, long initialCapacity, Duration period) {
        final SmoothlyRefillingBandwidthBandwidth bandwidth = new SmoothlyRefillingBandwidthBandwidth(maxCapacity, initialCapacity, period, false);
        bandwidths.add(bandwidth);
        return this;
    }

    @Override
    public BucketBuilder withLimitedBandwidth(Capacity capacity, Duration period) {
        final SmoothlyRefillingBandwidthBandwidth bandwidth = new SmoothlyRefillingBandwidthBandwidth(capacity, period.toNanos(), false);
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

    public static void checkCompatibility(SmoothlyRefillingBandwidthBandwidth[] bandwidths) {
        int countOfLimitedBandwidth = 0;
        int countOfGuaranteedBandwidth = 0;
        SmoothlyRefillingBandwidthBandwidth guaranteedBandwidth = null;

        for (SmoothlyRefillingBandwidthBandwidth bandwidth : bandwidths) {
            if (bandwidth.isLimited()) {
                countOfLimitedBandwidth++;
            } else {
                guaranteedBandwidth = bandwidth;
                countOfGuaranteedBandwidth++;
            }
        }

        if (countOfLimitedBandwidth == 0) {
            String msg = "At list one limited bandwidth should be specified";
            throw new IllegalArgumentException(msg);
        }

        if (countOfGuaranteedBandwidth > 1) {
            String msg = "Only one guaranteed bandwidth supported";
            throw new  IllegalArgumentException(msg);
        }

        for (int i = 0; i < bandwidths.length - 1; i++) {
            SmoothlyRefillingBandwidthBandwidth first = bandwidths[i];
            if (first.isGuaranteed()) {
                continue;
            }

            if (first.getCapacity() instanceof ConstantCapacity) {
                ConstantCapacity firstCapacity = (ConstantCapacity) first.getCapacity();
                for (int j = i + 1; j < bandwidths.length; j++) {
                    SmoothlyRefillingBandwidthBandwidth second = bandwidths[j];
                    if (second.isGuaranteed()) {
                        continue;
                    }
                    if (second.getCapacity() instanceof ConstantCapacity) {
                        ConstantCapacity secondCapacity = (ConstantCapacity) second.getCapacity();
                        if (first.getPeriodNanos() < second.getPeriodNanos() && firstCapacity.maxValue >= secondCapacity.maxValue) {
                            throw hasOverlaps(first, second);
                        } else if (first.periodNanos == second.periodNanos) {
                            throw hasOverlaps(first, second);
                        } else if (first.periodNanos > second.periodNanos && firstCapacity.maxValue <= secondCapacity.maxValue) {
                            throw hasOverlaps(first, second);
                        }
                    }
                }
            }
        }

        if (guaranteedBandwidth != null) {
            if (guaranteedBandwidth.getCapacity() instanceof ConstantCapacity) {
                for (SmoothlyRefillingBandwidthBandwidth bandwidth : bandwidths) {
                    if (bandwidth != guaranteedBandwidth && bandwidth.getCapacity() instanceof ConstantCapacity) {
                        SmoothlyRefillingBandwidthBandwidth limitedBandwidth = bandwidth;
                        if (limitedBandwidth.getTokensPerTimeUnit() <= guaranteedBandwidth.getTokensPerTimeUnit()
                                || limitedBandwidth.getTimeUnitsPerToken() > guaranteedBandwidth.getTimeUnitsPerToken()) {
                            String pattern = "Misconfiguration detected, guaranteed bandwidth {0} has higher rate than limited bandwidth {1}";
                            String msg = MessageFormat.format(pattern, guaranteed, limited);
                            return new IllegalArgumentException(msg);
                        }
                    }
                }
            }
        }
    }

    private static IllegalArgumentException hasOverlaps(SmoothlyRefillingBandwidthBandwidth first, SmoothlyRefillingBandwidthBandwidth second) {
        String pattern = "Overlap detected between {0} and {1}";
        String msg = MessageFormat.format(pattern, first, second);
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
