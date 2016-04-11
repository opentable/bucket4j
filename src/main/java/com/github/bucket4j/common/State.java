package com.github.bucket4j.common;

interface State {

    double getDouble(int offset);

    long getLong(int offset);

    void setDouble(int offset, double value);

    void setLong(int partialStateIndex, int offset, long value);

    int allocate(double[] array);

    int allocate(long[] array);

}
