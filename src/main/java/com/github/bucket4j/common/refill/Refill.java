package com.github.bucket4j.common.refill;

import com.github.bucket4j.common.Stateful;

import java.io.Serializable;

public interface Refill extends Serializable, Stateful {

    static Refill smoothly() {
        return SmoothlyRefill.INSTANCE;
    }

}
