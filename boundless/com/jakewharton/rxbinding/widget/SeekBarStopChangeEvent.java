package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.SeekBar;

public final class SeekBarStopChangeEvent extends SeekBarChangeEvent {
    @CheckResult
    @NonNull
    public static SeekBarStopChangeEvent create(@NonNull SeekBar view) {
        return new SeekBarStopChangeEvent(view);
    }

    private SeekBarStopChangeEvent(@NonNull SeekBar view) {
        super(view);
    }

    public boolean equals(Object o) {
        return (o instanceof SeekBarStopChangeEvent) && ((SeekBarStopChangeEvent) o).view() == view();
    }

    public int hashCode() {
        return ((SeekBar) view()).hashCode();
    }

    public String toString() {
        return "SeekBarStopChangeEvent{view=" + view() + '}';
    }
}
