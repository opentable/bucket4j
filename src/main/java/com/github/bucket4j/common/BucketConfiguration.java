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

package com.github.bucket4j.common;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

public final class BucketConfiguration implements Serializable {

    private final BandwidthState[] limitedBandwidths;
    private final BandwidthState guaranteedBandwidth;
    private final TimeMeter timeMeter;

    public BucketConfiguration(BandwidthState[] limitedBandwidths, BandwidthState guaranteedBandwidth, TimeMeter timeMeter) {
        this.timeMeter = Objects.requireNonNull(timeMeter);
        this.limitedBandwidths = Objects.requireNonNull(limitedBandwidths);
        this.guaranteedBandwidth = guaranteedBandwidth;
    }

    public TimeMeter getTimeMeter() {
        return timeMeter;
    }

    public BandwidthState[] getLimitedBandwidths() {
        return limitedBandwidths;
    }

    public BandwidthState getGuaranteedBandwidth() {
        return guaranteedBandwidth;
    }

    @Override
    public String toString() {
        return "BucketConfiguration{" +
                "limitedBandwidths=" + Arrays.toString(limitedBandwidths) +
                ", guaranteedBandwidth=" + guaranteedBandwidth +
                ", timeMeter=" + timeMeter +
                '}';
    }

    public static void checkCompatibility(SmoothlyRenewableBandwidthState[] bandwidths) {
        int countOfLimitedBandwidth = 0;
        int countOfGuaranteedBandwidth = 0;
        SmoothlyRenewableBandwidthState guaranteedBandwidth = null;

        for (SmoothlyRenewableBandwidthState bandwidth : bandwidths) {
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