package com.github.bucket4j.statistic;

public interface SimpleStatisticCollectorMBean {

    long getConsumedTokens();

    long getRejectedTokens();

    long getInterruptsCount();

    long getSleepingNanos();

    long getSleepingMillis();

    long getSleepingMicros();

    long getSleepingSeconds();

}
