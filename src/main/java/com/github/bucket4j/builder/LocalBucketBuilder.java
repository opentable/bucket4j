package com.github.bucket4j.builder;

import com.github.bucket4j.Bucket;
import com.github.bucket4j.statistic.StatisticCollector;

/**
 * Created by vladimir.bukhtoyarov on 29.02.2016.
 */
public interface LocalBucketBuilder extends BucketBuilder {

    BucketBuilder withStatisticCollector(StatisticCollector statisticCollector);

    /**
     * Constructs an instance of {@link com.github.bucket4j.impl.local.LockFreeBucket}
     *
     * @return an instance of {@link com.github.bucket4j.impl.local.LockFreeBucket}
     */
    Bucket build();

}
