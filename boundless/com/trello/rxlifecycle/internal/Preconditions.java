package com.trello.rxlifecycle.internal;

public final class Preconditions {
    public static <T> T checkNotNull(T value, String message) {
        if (value != null) {
            return value;
        }
        throw new NullPointerException(message);
    }

    private Preconditions() {
        throw new AssertionError("No instances.");
    }
}
