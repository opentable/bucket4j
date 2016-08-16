package com.github.bucket4j.common;

import java.io.Serializable;

@FunctionalInterface
public interface BandwidthDefinition extends Serializable {

    Bandwidth createBandwidth(StateInitializer stateInitializer, long currentTimeNanos);

}
