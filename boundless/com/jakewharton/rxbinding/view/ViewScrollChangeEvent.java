package com.jakewharton.rxbinding.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;

public final class ViewScrollChangeEvent extends ViewEvent<View> {
    private final int oldScrollX;
    private final int oldScrollY;
    private final int scrollX;
    private final int scrollY;

    @CheckResult
    @NonNull
    public static ViewScrollChangeEvent create(@NonNull View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        return new ViewScrollChangeEvent(view, scrollX, scrollY, oldScrollX, oldScrollY);
    }

    protected ViewScrollChangeEvent(@NonNull View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        super(view);
        this.scrollX = scrollX;
        this.scrollY = scrollY;
        this.oldScrollX = oldScrollX;
        this.oldScrollY = oldScrollY;
    }

    public int scrollX() {
        return this.scrollX;
    }

    public int scrollY() {
        return this.scrollY;
    }

    public int oldScrollX() {
        return this.oldScrollX;
    }

    public int oldScrollY() {
        return this.oldScrollY;
    }

    public int hashCode() {
        return ((((((((view().hashCode() + 629) * 37) + this.scrollX) * 37) + this.scrollY) * 37) + this.oldScrollX) * 37) + this.oldScrollY;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ViewScrollChangeEvent)) {
            return false;
        }
        ViewScrollChangeEvent other = (ViewScrollChangeEvent) o;
        if (other.view() == view() && other.scrollX == this.scrollX && other.scrollY == this.scrollY && other.oldScrollX == this.oldScrollX && other.oldScrollY == this.oldScrollY) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "ViewScrollChangeEvent{scrollX=" + this.scrollX + ", scrollY=" + this.scrollY + ", oldScrollX=" + this.oldScrollX + ", oldScrollY=" + this.oldScrollY + '}';
    }
}
