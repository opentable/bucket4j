package com.github.bucket4j;

interface State {

    double getDouble(int partialStateIndex, int offset);

    long getLong(int partialStateIndex, int offset);

    double setDouble(int partialStateIndex, int offset, double value);

    long getLong(int partialStateIndex, int offset, long value);

}
