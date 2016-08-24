package com.github.bucket4j.common.refill;

import java.text.MessageFormat;

public class SmoothlyRefill implements Refill {

    private final long periodNanos;

    public SmoothlyRefill(long periodNanos) {
        if (periodNanos <= 0) {
            String pattern = "{0} nanoseconds is wrong value for period of bandwidth, because period should be > 0";
            String msg = MessageFormat.format(pattern, periodNanos);
            throw new IllegalArgumentException(msg);
        }
        this.periodNanos = periodNanos;
    }

}
