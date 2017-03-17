package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.SeekBar;

public final class SeekBarProgressChangeEvent extends SeekBarChangeEvent {
    private final boolean fromUser;
    private final int progress;

    @CheckResult
    @NonNull
    public static SeekBarProgressChangeEvent create(@NonNull SeekBar view, int progress, boolean fromUser) {
        return new SeekBarProgressChangeEvent(view, progress, fromUser);
    }

    private SeekBarProgressChangeEvent(@NonNull SeekBar view, int progress, boolean fromUser) {
        super(view);
        this.progress = progress;
        this.fromUser = fromUser;
    }

    public int progress() {
        return this.progress;
    }

    public boolean fromUser() {
        return this.fromUser;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SeekBarProgressChangeEvent)) {
            return false;
        }
        SeekBarProgressChangeEvent other = (SeekBarProgressChangeEvent) o;
        if (other.view() == view() && other.progress == this.progress && other.fromUser == this.fromUser) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((SeekBar) view()).hashCode() + 629) * 37) + this.progress) * 37) + (this.fromUser ? 1 : 0);
    }

    public String toString() {
        return "SeekBarProgressChangeEvent{view=" + view() + ", progress=" + this.progress + ", fromUser=" + this.fromUser + '}';
    }
}
