package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.widget.TextView;
import com.jakewharton.rxbinding.view.ViewEvent;

public final class TextViewAfterTextChangeEvent extends ViewEvent<TextView> {
    private final Editable editable;

    @CheckResult
    @NonNull
    public static TextViewAfterTextChangeEvent create(@NonNull TextView view, @NonNull Editable editable) {
        return new TextViewAfterTextChangeEvent(view, editable);
    }

    private TextViewAfterTextChangeEvent(@NonNull TextView view, @NonNull Editable editable) {
        super(view);
        this.editable = editable;
    }

    @NonNull
    public Editable editable() {
        return this.editable;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TextViewAfterTextChangeEvent)) {
            return false;
        }
        TextViewAfterTextChangeEvent other = (TextViewAfterTextChangeEvent) o;
        if (other.view() == view() && this.editable.equals(other.editable)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((TextView) view()).hashCode() + 629) * 37) + this.editable.hashCode();
    }

    public String toString() {
        return "TextViewAfterTextChangeEvent{editable=" + this.editable + ", view=" + view() + '}';
    }
}
