package com.github.bucket4j.common;

import java.time.Duration;
import java.util.Optional;


public interface Bandwidth {

    static Bandwidth smoothlyRenewable(long maxCapacity, Duration period) {
        return smoothlyRenewable(maxCapacity, period, maxCapacity);
    }

    static Bandwidth smoothlyRenewable(long maxCapacity, Duration period, long initialCapacity) {
        return SmoothlyRenewableBandwidthState.bandwidth(maxCapacity, initialCapacity, period.toNanos());
    }

    static Bandwidth smoothlyWarmingUp(Duration period, long fromCapacity, long toCapacity, Duration warmupPeriod) {
        return smoothlyWarmingUp(period, fromCapacity, toCapacity, warmupPeriod, fromCapacity);
    }

    static Bandwidth smoothlyWarmingUp(Duration period, long fromCapacity, long toCapacity, Duration warmupPeriod, long initialCapacity) {
        return SmoothlyWarmingUpBandwidthState.bandwidth(period.toNanos(), fromCapacity, toCapacity, warmupPeriod.toNanos(), initialCapacity);
    }

    static Bandwidth intervallyRenewable(long maxCapacity, long initialCapacity, Duration period) {
        return SmoothlyRenewableBandwidthState.bandwidth(maxCapacity, initialCapacity, period.toNanos());
    }

    BandwidthState createInitialBandwidthState(StateInitializer stateInitializer, long currentTimeNanos);

    Optional<Long> getPeriodInNanos();

    Optional<Long> getMaxCapacity();

}
