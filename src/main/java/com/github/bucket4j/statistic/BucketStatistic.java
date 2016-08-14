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

}
