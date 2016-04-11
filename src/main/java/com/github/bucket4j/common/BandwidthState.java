package com.github.bucket4j.common;

import java.io.Serializable;

public interface BandwidthState extends Serializable {

    void refill(State state, long lastRefillTimeNanos, long currentTimeNanos);

    long delayNanosAfterWillBePossibleToConsume(State state, long currentTimeNanos, double tokens);

    long reserve(State state, double tokens);

}
