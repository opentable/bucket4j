package com.github.bucket4j;

import java.io.Serializable;

public interface BandwidthDefinition extends Serializable {

    Bandwidth createBandwidth(int offset);

}
