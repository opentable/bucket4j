package com.github.bucket4j.statistic;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

public class MetricsCoreStatisticCollector implements StatisticCollector {

    private final Meter consumedTokens;
    private final Meter rejectedTokens;
    private final Meter returnedTokens;
    private final Meter interruptsCount;
    private final Meter sleepingNanos;

    public MetricsCoreStatisticCollector() {
        this(new Meter(), new Meter(), new Meter(), new Meter(), new Meter());
    }

    public MetricsCoreStatisticCollector(Meter consumedTokens, Meter rejectedTokens, Meter returnedTokens, Meter interruptsCount, Meter sleepingNanos) {
        this.consumedTokens = consumedTokens;
        this.rejectedTokens = rejectedTokens;
        this.returnedTokens = returnedTokens;
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
    public void registerReturnedTokens(long numTokens) {
        returnedTokens.mark(numTokens);
    }

    @Override
    public void registerInterrupt() {
        interruptsCount.mark();
    }

    @Override
    public void registerSleepingNanos(long numTokens) {
        sleepingNanos.mark(numTokens);
    }

    @Override
    public StatisticSnapshot getSnapshot() {
        return new StatisticSnapshot(
                consumedTokens.getCount(),
                rejectedTokens.getCount(),
                returnedTokens.getCount(),
                interruptsCount.getCount(),
                sleepingNanos.getCount());
    }

    public void registerMetrics(MetricRegistry registry, String baseName) {
        registry.register(baseName + ".consumedTokens", consumedTokens);
        registry.register(baseName + ".rejectedTokens", rejectedTokens);
        registry.register(baseName + ".returnedTokens", returnedTokens);
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

    public Meter getReturnedTokens() {
        return returnedTokens;
    }

    public Meter getSleepingNanos() {
        return sleepingNanos;
    }

}
