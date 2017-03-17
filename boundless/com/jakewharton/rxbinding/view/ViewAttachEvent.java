package com.jakewharton.rxbinding.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;

public final class ViewAttachEvent extends ViewEvent<View> {
    private final Kind kind;

    public enum Kind {
        ATTACH,
        DETACH
    }

    @CheckResult
    @NonNull
    public static ViewAttachEvent create(@NonNull View view, @NonNull Kind kind) {
        return new ViewAttachEvent(view, kind);
    }

    private ViewAttachEvent(@NonNull View view, @NonNull Kind kind) {
        super(view);
        this.kind = kind;
    }

    @NonNull
    public Kind kind() {
        return this.kind;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ViewAttachEvent)) {
            return false;
        }
        ViewAttachEvent other = (ViewAttachEvent) o;
        if (other.view() == view() && other.kind() == kind()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((view().hashCode() + 629) * 37) + kind().hashCode();
    }

    public String toString() {
        return "ViewAttachEvent{view=" + view() + ", kind=" + kind() + '}';
    }
}
