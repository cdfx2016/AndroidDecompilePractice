package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.TextView;
import com.jakewharton.rxbinding.view.ViewEvent;

public final class TextViewBeforeTextChangeEvent extends ViewEvent<TextView> {
    private final int after;
    private final int count;
    private final int start;
    private final CharSequence text;

    @CheckResult
    @NonNull
    public static TextViewBeforeTextChangeEvent create(@NonNull TextView view, @NonNull CharSequence text, int start, int count, int after) {
        return new TextViewBeforeTextChangeEvent(view, text, start, count, after);
    }

    private TextViewBeforeTextChangeEvent(@NonNull TextView view, @NonNull CharSequence text, int start, int count, int after) {
        super(view);
        this.text = text;
        this.start = start;
        this.count = count;
        this.after = after;
    }

    @NonNull
    public CharSequence text() {
        return this.text;
    }

    public int start() {
        return this.start;
    }

    public int count() {
        return this.count;
    }

    public int after() {
        return this.after;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TextViewAfterTextChangeEvent)) {
            return false;
        }
        TextViewBeforeTextChangeEvent other = (TextViewBeforeTextChangeEvent) o;
        if (other.view() == view() && this.text.equals(other.text) && this.start == other.start && this.count == other.count && this.after == other.after) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((((((TextView) view()).hashCode() + 629) * 37) + this.text.hashCode()) * 37) + this.start) * 37) + this.count) * 37) + this.after;
    }

    public String toString() {
        return "TextViewBeforeTextChangeEvent{text=" + this.text + ", start=" + this.start + ", count=" + this.count + ", after=" + this.after + ", view=" + view() + '}';
    }
}
