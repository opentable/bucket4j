package com.github.bucket4j.statistic;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

public class MetricsCoreBucketStatistic implements BucketStatistic {

    private final Meter consumedTokens;
    private final Meter rejectedTokens;
    private final Meter interruptsCount;
    private final Meter sleepingNanos;

    public MetricsCoreBucketStatistic() {
        this(new Meter(), new Meter(), new Meter(), new Meter());
    }

    public MetricsCoreBucketStatistic(Meter consumedTokens, Meter rejectedTokens, Meter interruptsCount, Meter sleepingNanos) {
        this.consumedTokens = consumedTokens;
        this.rejectedTokens = rejectedTokens;
        this.interruptsCount = interruptsCount;
        this.sleepingNanos = sleepingNanos;
    }

    @Override
    public void registerConsumedTokens(long numTokens) {
        consumedTokens.mark(numTokens);
    }

    @Override
    public void registerRejectedTokens(long numTokens) {
        rejectedTokens.mark(numTokens);
    }

    @Override
    public void registerInterrupt() {
        interruptsCount.mark();
    }

    @Override
    public void registerParkedNanos(long numTokens) {
        sleepingNanos.mark(numTokens);
    }

    @Override
    public StatisticSnapshot getSnapshot() {
        return new StatisticSnapshot(
                consumedTokens.getCount(),
                rejectedTokens.getCount(),
                interruptsCount.getCount(),
                sleepingNanos.getCount());
    }

    public void registerMetrics(MetricRegistry registry, String baseName) {
        registry.register(baseName + ".consumedTokens", consumedTokens);
        registry.register(baseName + ".rejectedTokens", rejectedTokens);
        registry.register(baseName + ".interruptsCount", interruptsCount);
        registry.register(baseName + ".sleepingNanos", sleepingNanos);
    }

    public Meter getConsumedTokens() {
        return consumedTokens;
    }

    public Meter getInterruptsCount() {
        return interruptsCount;
    }

    public Meter getRejectedTokens() {
        return rejectedTokens;
    }

    public Meter getSleepingNanos() {
        return sleepingNanos;
    }

}
