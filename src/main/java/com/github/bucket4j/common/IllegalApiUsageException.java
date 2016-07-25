package com.github.bucket4j.common;

public class IllegalApiUsageException extends IllegalArgumentException {

    private IllegalApiUsageException(String message) {
        super(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IllegalApiUsageException that = (IllegalApiUsageException) o;

        return getMessage().equals(that.getMessage());
    }

    @Override
    public int hashCode() {
        return getMessage().hashCode();
    }

}
