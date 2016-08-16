package com.github.bucket4j.common;

import java.text.MessageFormat;

public class Preconditions {

    public static void checkCapacities(long maxCapacity, long initialCapacity) {
        if (maxCapacity <= 0) {
            String pattern = "{0} is wrong value for maxCapacity, because maxCapacity should be > 0";
            String msg = MessageFormat.format(pattern, maxCapacity);
            throw new IllegalArgumentException(msg);
        }
        if (initialCapacity < 0) {
            String pattern = "{0} is wrong value for initial maxCapacity, because initial maxCapacity should be >= 0";
            String msg = MessageFormat.format(pattern, initialCapacity);
            throw new IllegalArgumentException(msg);
        }
        if (initialCapacity > maxCapacity) {
            String pattern = "Initial maxCapacity {0} is greater than max maxCapacity {1}";
            String msg = MessageFormat.format(pattern, initialCapacity, maxCapacity);
            throw new IllegalArgumentException(msg);
        }
    }

    public static void checkPeriod(long periodNanos) {
        if (periodNanos <= 0) {
            String pattern = "{0} nanoseconds is wrong value for period of bandwidth, because period should be positive";
            String msg = MessageFormat.format(pattern, periodNanos);
            throw new IllegalArgumentException(msg);
        }
    }

    private static IllegalArgumentException hasOverlaps(SmoothlyRenewableBandwidth first, SmoothlyRenewableBandwidth second) {
        String pattern = "Overlap detected between {0} and {1}";
        String msg = MessageFormat.format(pattern, first, second);
        return new IllegalArgumentException(msg);
    }

}
