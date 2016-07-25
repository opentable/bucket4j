package com.github.bucket4j.common;

public class InitialState {

    private final BucketConfiguration configuration;
    private final BucketState state;

    public InitialState(BucketConfiguration configuration, BucketState state) {
        this.configuration = configuration;
        this.state = state;
    }

    public BucketConfiguration getConfiguration() {
        return configuration;
    }

    public BucketState getState() {
        return state;
    }

}
