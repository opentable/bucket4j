package com.github.bucket4j.impl;

import java.text.MessageFormat;

public class BucketAlgorithms {

    public static void checkCompatibility(Bandwidth[] bandwidths) {
        int countOfLimitedBandwidth = 0;
        int countOfGuaranteedBandwidth = 0;
        Bandwidth guaranteedBandwidth = null;

        for (Bandwidth bandwidth : bandwidths) {
            if (bandwidth.isLimited()) {
                countOfLimitedBandwidth++;
            } else {
                guaranteedBandwidth = bandwidth;
                countOfGuaranteedBandwidth++;
            }
        }

        if (countOfLimitedBandwidth == 0) {
            String msg = "At list one limited bandwidth should be specified";
            throw new IllegalArgumentException(msg);
        }

        if (countOfGuaranteedBandwidth > 1) {
            String msg = "Only one guaranteed bandwidth supported";
            throw new  IllegalArgumentException(msg);
        }

        for (int i = 0; i < bandwidths.length - 1; i++) {
            Bandwidth first = bandwidths[i];
            if (first.isGuaranteed()) {
                continue;
            }

            if (first.getCapacity() instanceof ConstantCapacity) {
                ConstantCapacity firstCapacity = (ConstantCapacity) first.getCapacity();
                for (int j = i + 1; j < bandwidths.length; j++) {
                    Bandwidth second = bandwidths[j];
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
                for (Bandwidth bandwidth : bandwidths) {
                    if (bandwidth != guaranteedBandwidth && bandwidth.getCapacity() instanceof ConstantCapacity) {
                        Bandwidth limitedBandwidth = bandwidth;
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

    private static IllegalArgumentException hasOverlaps(Bandwidth first, Bandwidth second) {
        String pattern = "Overlap detected between {0} and {1}";
        String msg = MessageFormat.format(pattern, first, second);
        return new IllegalArgumentException(msg);
    }

}
