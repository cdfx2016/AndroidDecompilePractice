package com.jakewharton.rxbinding.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

public final class ViewGroupHierarchyChildViewRemoveEvent extends ViewGroupHierarchyChangeEvent {
    @CheckResult
    @NonNull
    public static ViewGroupHierarchyChildViewRemoveEvent create(@NonNull ViewGroup viewGroup, View child) {
        return new ViewGroupHierarchyChildViewRemoveEvent(viewGroup, child);
    }

    private ViewGroupHierarchyChildViewRemoveEvent(@NonNull ViewGroup viewGroup, View child) {
        super(viewGroup, child);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ViewGroupHierarchyChildViewRemoveEvent)) {
            return false;
        }
        ViewGroupHierarchyChildViewRemoveEvent other = (ViewGroupHierarchyChildViewRemoveEvent) o;
        if (other.view() == view() && other.child() == child()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((ViewGroup) view()).hashCode() + 629) * 37) + child().hashCode();
    }

    public String toString() {
        return "ViewGroupHierarchyChildViewRemoveEvent{view=" + view() + ", child=" + child() + '}';
    }
}
