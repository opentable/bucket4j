package com.github.bucket4j.common;

import java.text.MessageFormat;
import java.util.List;

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

    public static void checkCompatibility(List<Bandwidth> limitedBandwidths, Bandwidth guaranteedBandwidth) {
        if (limitedBandwidths.isEmpty()) {
            String msg = "At list one limited bandwidth should be specified";
            throw new IllegalArgumentException(msg);
        }


        for (int i = 0; i < bandwidths.length - 1; i++) {
            SmoothlyRenewableBandwidthState first = bandwidths[i];
            if (first.isGuaranteed()) {
                continue;
            }

            if (first.getCapacity() instanceof ConstantCapacity) {
                ConstantCapacity firstCapacity = (ConstantCapacity) first.getCapacity();
                for (int j = i + 1; j < bandwidths.length; j++) {
                    SmoothlyRenewableBandwidthState second = bandwidths[j];
                    if (second.isGuaranteed()) {
                        continue;
                    }
                    if (second.getCapacity() instanceof ConstantCapacity) {
                        ConstantCapacity secondCapacity = (ConstantCapacity) second.getCapacity();
                        if (first.getPeriodNanos() < second.getPeriodNanos() && firstCapacity.maxValue >= secondCapacity.maxValue) {
                            throw hasOverlaps(first, second);
                        } else if (first.periodNanos == second.periodNanos) {
                            throw hasOverlaps(first, second);
                        } else if (first.periodNanos > second.periodNanos && firstCapacity.maxValue <= secondCapacity.maxValue) {
                            throw hasOverlaps(first, second);
                        }
                    }
                }
            }
        }

        if (guaranteedBandwidth != null) {
            if (guaranteedBandwidth.getCapacity() instanceof ConstantCapacity) {
                for (SmoothlyRenewableBandwidthState bandwidth : bandwidths) {
                    if (bandwidth != guaranteedBandwidth && bandwidth.getCapacity() instanceof ConstantCapacity) {
                        SmoothlyRenewableBandwidthState limitedBandwidth = bandwidth;
                        if (limitedBandwidth.getTokensPerTimeUnit() <= guaranteedBandwidth.getTokensPerTimeUnit()
                                || limitedBandwidth.getTimeUnitsPerToken() > guaranteedBandwidth.getTimeUnitsPerToken()) {
                            String pattern = "Misconfiguration detected, guaranteed bandwidth {0} has higher rate than limited bandwidth {1}";
                            String msg = MessageFormat.format(pattern, guaranteed, limited);
                            return new IllegalArgumentException(msg);
                        }
                    }
                }
            }
        }
    }

    private static IllegalArgumentException hasOverlaps(SmoothlyRenewableBandwidthState first, SmoothlyRenewableBandwidthState second) {
        String pattern = "Overlap detected between {0} and {1}";
        String msg = MessageFormat.format(pattern, first, second);
        return new IllegalArgumentException(msg);
    }

}
