package com.github.bucket4j.jdbc;

import com.github.bucket4j.common.AbstractBucket;
import com.github.bucket4j.common.BucketConfiguration;
import com.github.bucket4j.common.BucketState;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

public class JdbcBucket<K, T> extends AbstractBucket {

    public JdbcBucket(BucketConfiguration configuration, JdbcAdapter<K, T> adapter, K primaryKey) {
        super(configuration);
    }

    @Override
    protected long consumeAsMuchAsPossibleImpl(long limit) {
        return 0;
    }

    @Override
    protected boolean tryConsumeImpl(long tokensToConsume) {
        return false;
    }

    @Override
    protected CompletableFuture<Boolean> tryConsumeAsyncImpl(long numTokens, long maxWaitNanos, ScheduledExecutorService scheduler) {
        return null;
    }

    @Override
    protected boolean consumeOrAwaitImpl(long tokensToConsume, long waitIfBusyNanos) throws InterruptedException {
        return false;
    }

    @Override
    protected void applySnapshotImpl(BucketState bucketState) {

    }

    @Override
    public BucketState getStateSnapshot() {
        return null;
    }

    @Override
    public long getAvailableTokens() {
        return 0;
    }
}
