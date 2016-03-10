package com.github.bucket4j.builder;

import com.github.bucket4j.Bucket;
import com.github.bucket4j.impl.BucketConfiguration;
import com.github.bucket4j.impl.local.LockFreeBucket;
import com.github.bucket4j.statistic.DummyStatisticCollector;
import com.github.bucket4j.statistic.StatisticCollector;

import java.util.Objects;

public class LocalBucketBuilderImpl extends AbstractBucketBuilder implements LocalBucketBuilder {

    private StatisticCollector statisticCollector = DummyStatisticCollector.INSTANCE;

    @Override
    public BucketBuilder withStatisticCollector(StatisticCollector statisticCollector) {
        this.statisticCollector = Objects.requireNonNull(statisticCollector);
        return this;
    }

    @Override
    public Bucket build() {
        BucketConfiguration configuration = createConfiguration();
        return new LockFreeBucket(configuration);
    }

}
