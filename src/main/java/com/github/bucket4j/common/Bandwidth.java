package com.github.bucket4j.common;

import java.time.Duration;



public interface Bandwidth {

    Bandwidth createInitialBandwidthState(State state, long currentTimeNanos);

    static Bandwidth smoothlyRenewable(long maxCapacity, Duration period) {
        return smoothlyRenewable(maxCapacity, maxCapacity, period);
    }

    static Bandwidth smoothlyRenewable(long maxCapacity, long initialCapacity, Duration period) {
        return SmoothlyRenewableBandwidthState.bandwidth(maxCapacity, initialCapacity, period.toNanos());
    }

    static Bandwidth smoothlyWarmingUp(Duration period, long fromCapacity, long toCapacity, Duration warmupPeriod) {
        return SmoothlyWarmingUpBandwidthState.bandwidth(period.toNanos(), fromCapacity, toCapacity, warmupPeriod.toNanos());
    }

    static Bandwidth intervallyRenewable(long maxCapacity, long initialCapacity, Duration period) {
        return SmoothlyRenewableBandwidthState.bandwidth(maxCapacity, initialCapacity, period.toNanos());
    }

}
