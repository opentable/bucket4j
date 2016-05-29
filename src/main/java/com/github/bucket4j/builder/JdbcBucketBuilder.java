package com.github.bucket4j.builder;

import com.github.bucket4j.Bucket;
import com.github.bucket4j.jdbc.JdbcAdapter;

public interface JdbcBucketBuilder extends BucketBuilder {

    <K, T> Bucket build(K premaryKey, JdbcAdapter<K, T> jdbcAdapter);

}
