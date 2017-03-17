package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.widget.TextView;
import com.jakewharton.rxbinding.view.ViewEvent;

public final class TextViewEditorActionEvent extends ViewEvent<TextView> {
    private final int actionId;
    private final KeyEvent keyEvent;

    @CheckResult
    @NonNull
    public static TextViewEditorActionEvent create(@NonNull TextView view, int actionId, @NonNull KeyEvent keyEvent) {
        return new TextViewEditorActionEvent(view, actionId, keyEvent);
    }

    private TextViewEditorActionEvent(@NonNull TextView view, int actionId, @NonNull KeyEvent keyEvent) {
        super(view);
        this.actionId = actionId;
        this.keyEvent = keyEvent;
    }

    public int actionId() {
        return this.actionId;
    }

    @NonNull
    public KeyEvent keyEvent() {
        return this.keyEvent;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TextViewEditorActionEvent)) {
            return false;
        }
        TextViewEditorActionEvent other = (TextViewEditorActionEvent) o;
        if (other.view() == view() && other.actionId == this.actionId && other.keyEvent.equals(this.keyEvent)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((TextView) view()).hashCode() + 629) * 37) + this.actionId) * 37) + this.keyEvent.hashCode();
    }

    public String toString() {
        return "TextViewEditorActionEvent{view=" + view() + ", actionId=" + this.actionId + ", keyEvent=" + this.keyEvent + '}';
    }
}
