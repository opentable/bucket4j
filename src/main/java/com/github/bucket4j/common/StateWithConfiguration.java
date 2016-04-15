package com.github.bucket4j.common;

public class StateWithConfiguration {

    private final BucketConfiguration configuration;
    private final BucketState state;

    public StateWithConfiguration(BucketConfiguration configuration, BucketState state) {
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
