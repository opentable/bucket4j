/*
 * Copyright 2015 Vladimir Bukhtoyarov
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.bucket4j.builder;

import com.github.bucket4j.*;

/**
 * A builder for buckets. Builder can be reused, i.e. one builder can create multiple buckets with similar configuration.
 *
 * @see com.github.bucket4j.impl.local.LockFreeBucket
 * @see com.github.bucket4j.impl.grid.GridBucket
 */
public interface BucketBuilder {

    /**
     * Configures {@link com.github.bucket4j.TimeMeter#SYSTEM_MILLISECONDS} as time meter.
     *
     * @return
     */
    BucketBuilder withMillisecondPrecision();

    /**
     * Configures {@link com.github.bucket4j.TimeMeter#SYSTEM_NANOTIME} as time meter.
     *
     * @return
     */
    BucketBuilder withNanosecondPrecision();

    /**
     * Configures {@code customTimeMeter} as time meter.
     *
     * @param customTimeMeter object which will measure time.
     *
     * @return
     */
    BucketBuilder withCustomTimeMeter(TimeMeter customTimeMeter);

    /**
     * Adds limited bandwidth for all buckets which will be constructed by this builder instance.
     * <p>
     * You can specify as many limited bandwidth as needed.
     *
     * @param bandwidth
     * @return
     */
    BucketBuilder withLimitedBandwidth(BandwidthDefinition bandwidth);

    /**
     * Adds guaranteed bandwidth for all buckets which will be constructed by this builder instance.
     * <p>
     * Guaranteed bandwidth provides following feature: if tokens can be consumed from guaranteed bandwidth,
     * then bucket4j do not perform checking of any limited bandwidths.
     * <p>
     * Unlike limited bandwidths, you can use only one guaranteed bandwidth per single bucket.
     *
     * @param bandwidth
     * @return
     */
    BucketBuilder withGuaranteedBandwidth(BandwidthDefinition bandwidth);

}
