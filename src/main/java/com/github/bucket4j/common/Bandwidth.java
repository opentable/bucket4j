package com.github.bucket4j.common;


import java.io.Serializable;
import java.time.Duration;

public interface Bandwidth extends Serializable {

    void refill(BucketState state,  long lastRefillTimeNanos, long currentTimeNanos);

    long delayNanosAfterWillBePossibleToConsume(BucketState state, long currentTimeNanos, double tokens);

    long reserve(BucketState state, double tokens);

}
