package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.SeekBar;

public final class SeekBarStartChangeEvent extends SeekBarChangeEvent {
    @CheckResult
    @NonNull
    public static SeekBarStartChangeEvent create(@NonNull SeekBar view) {
        return new SeekBarStartChangeEvent(view);
    }

    private SeekBarStartChangeEvent(@NonNull SeekBar view) {
        super(view);
    }

    public boolean equals(Object o) {
        return (o instanceof SeekBarStartChangeEvent) && ((SeekBarStartChangeEvent) o).view() == view();
    }

    public int hashCode() {
        return ((SeekBar) view()).hashCode();
    }

    public String toString() {
        return "SeekBarStartChangeEvent{view=" + view() + '}';
    }
}
