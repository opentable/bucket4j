package com.github.bucket4j.statistic;

public interface StatisticCollector {

    void registerConsumedTokens(long numTokens);

    void registerRejectedTokens(long numTokens);

    void registerReturnedTokens(long numTokens);

    void registerInterrupt();

    void registerSleepingNanos(long sleepingNanos);

    StatisticSnapshot createStatisticSnapshot();

}
