package com.github.bucket4j;

import java.io.Serializable;

public interface Bandwidth extends Serializable {

    double[] getInitialState();

    void refill(State state, long lastRefillTimeNanos, long currentTimeNanos);

    long delayNanosAfterWillBePossibleToConsume(State state, long currentTimeNanos, double tokens);

    long reserve(State state, double tokens);

}
