package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.TextView;
import com.jakewharton.rxbinding.view.ViewEvent;

public final class TextViewTextChangeEvent extends ViewEvent<TextView> {
    private final int before;
    private final int count;
    private final int start;
    private final CharSequence text;

    @CheckResult
    @NonNull
    public static TextViewTextChangeEvent create(@NonNull TextView view, @NonNull CharSequence text, int start, int before, int count) {
        return new TextViewTextChangeEvent(view, text, start, before, count);
    }

    private TextViewTextChangeEvent(@NonNull TextView view, @NonNull CharSequence text, int start, int before, int count) {
        super(view);
        this.text = text;
        this.start = start;
        this.before = before;
        this.count = count;
    }

    @NonNull
    public CharSequence text() {
        return this.text;
    }

    public int start() {
        return this.start;
    }

    public int before() {
        return this.before;
    }

    public int count() {
        return this.count;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TextViewTextChangeEvent)) {
            return false;
        }
        TextViewTextChangeEvent other = (TextViewTextChangeEvent) o;
        if (other.view() == view() && this.text.equals(other.text) && this.start == other.start && this.before == other.before && this.count == other.count) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((((((TextView) view()).hashCode() + 629) * 37) + this.text.hashCode()) * 37) + this.start) * 37) + this.before) * 37) + this.count;
    }

    public String toString() {
        return "TextViewTextChangeEvent{text=" + this.text + ", start=" + this.start + ", before=" + this.before + ", count=" + this.count + ", view=" + view() + '}';
    }
}
