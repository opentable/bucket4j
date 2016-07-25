package com.github.bucket4j.statistic;

/**
 * Collects statistics of the bucket.
 *
 */
public interface BucketStatistic {

    void registerConsumedTokens(long numTokens);

    void registerRejectedTokens(long numTokens);

    void registerInterrupt();

    void registerParkedNanos(long sleepingNanos);

    /**
     * Creates snapshot of bucket statistic which actual on the moment of invocation.
     *
     * @return snapshot of bucket statistic
     * @throws IllegalStateException if statistic collector is not configured for this bucket during construction
     */
    StatisticSnapshot getSnapshot();

}
