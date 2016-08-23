package com.github.bucket4j.common;

public interface Stateful {
    
    int getStateSize();
    
    void populateInitialState(BucketState state, int offset, long currentTimeNanos);
    
}
