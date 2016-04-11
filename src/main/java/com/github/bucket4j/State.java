package com.github.bucket4j;

interface State {

    double getDouble(int offset);

    long getLong(int offset);

    void setDouble(int offset, double value);

    void setLong(int partialStateIndex, int offset, long value);

}
