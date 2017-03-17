package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;

public final class AdapterViewItemSelectionEvent extends AdapterViewSelectionEvent {
    private final long id;
    private final int position;
    private final View selectedView;

    @CheckResult
    @NonNull
    public static AdapterViewSelectionEvent create(@NonNull AdapterView<?> view, @NonNull View selectedView, int position, long id) {
        return new AdapterViewItemSelectionEvent(view, selectedView, position, id);
    }

    private AdapterViewItemSelectionEvent(@NonNull AdapterView<?> view, @NonNull View selectedView, int position, long id) {
        super(view);
        this.selectedView = selectedView;
        this.position = position;
        this.id = id;
    }

    @NonNull
    public View selectedView() {
        return this.selectedView;
    }

    public int position() {
        return this.position;
    }

    public long id() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AdapterViewItemSelectionEvent)) {
            return false;
        }
        AdapterViewItemSelectionEvent other = (AdapterViewItemSelectionEvent) o;
        if (other.view() == view() && other.selectedView == this.selectedView && other.position == this.position && other.id == this.id) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((((AdapterView) view()).hashCode() + 629) * 37) + this.selectedView.hashCode()) * 37) + this.position) * 37) + ((int) (this.id ^ (this.id >>> 32)));
    }

    public String toString() {
        return "AdapterViewItemSelectionEvent{view=" + view() + ", selectedView=" + this.selectedView + ", position=" + this.position + ", id=" + this.id + '}';
    }
}
