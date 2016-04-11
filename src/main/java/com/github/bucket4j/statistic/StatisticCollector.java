package com.github.bucket4j.statistic;

/**
 * Collects statistics of the bucket.
 *
 */
public interface StatisticCollector {

    void registerConsumedTokens(long numTokens);

    void registerRejectedTokens(long numTokens);

    void registerReturnedTokens(long numTokens);

    void registerInterrupt();

    void registerSleepingNanos(long sleepingNanos);

    /**
     * Creates snapshot of bucket statistic which actual on the moment of invocation.
     *
     * @return snapshot of bucket statistic
     * @throws IllegalStateException if statistic collector is not configured for this bucket during construction
     */
    StatisticSnapshot getSnapshot();

}
