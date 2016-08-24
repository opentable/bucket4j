package com.github.bucket4j.common.refill;

import com.github.bucket4j.common.Stateful;

import java.io.Serializable;
import java.time.Duration;

public interface Refill extends Serializable, Stateful {

    static Refill smoothlyWithPeriod(Duration period) {
        return new SmoothlyRefill(period.toNanos());
    }

}
