package com.github.bucket4j.common;

import java.time.Duration;

public class Bandwidths {

    static BandwidthDefinition smoothlyRenewable(long maxCapacity, Duration period) {
        return smoothlyRenewable(maxCapacity, period, maxCapacity);
    }

    static BandwidthDefinition smoothlyRenewable(long maxCapacity, Duration period, long initialCapacity) {
        return SmoothlyRenewableBandwidth.bandwidth(maxCapacity, initialCapacity, period.toNanos());
    }

    static BandwidthDefinition smoothlyWarmingUp(Duration period, long fromCapacity, long toCapacity, Duration warmupPeriod) {
        return smoothlyWarmingUp(period, fromCapacity, toCapacity, warmupPeriod, fromCapacity);
    }

    static BandwidthDefinition smoothlyWarmingUp(Duration period, long fromCapacity, long toCapacity, Duration warmupPeriod, long initialCapacity) {
        return SmoothlyWarmingUpBandwidthState.bandwidth(period.toNanos(), fromCapacity, toCapacity, warmupPeriod.toNanos(), initialCapacity);
    }

    static BandwidthDefinition intervallyRenewable(long maxCapacity, Duration period) {
        return intervallyRenewable(maxCapacity, maxCapacity, period);
    }

    static BandwidthDefinition intervallyRenewable(long maxCapacity, long initialCapacity, Duration period) {
        return SmoothlyRenewableBandwidth.bandwidth(maxCapacity, initialCapacity, period.toNanos());
    }

}
