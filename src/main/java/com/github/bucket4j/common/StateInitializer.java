package com.github.bucket4j.common;

import java.util.Arrays;

public class StateInitializer {

    private long[] state;

    public StateInitializer(long[] state) {
        this.state = state;
    }

    public int allocate(double[] array) {
        int previousLengh = state.length;
        state = Arrays.copyOf(state, previousLengh + array.length);
        for (int i = 0; i < array.length; i++) {
            state[previousLengh + i] = Double.doubleToRawLongBits(state[0]);
        }
        return previousLengh;
    }

    public int allocate(long[] array) {
        int previousLengh = state.length;
        state = Arrays.copyOf(state, previousLengh + array.length);
        System.arraycopy(array, 0, state, previousLengh, array.length);
        return previousLengh;
    }

    public long[] getState() {
        return state;
    }

}
