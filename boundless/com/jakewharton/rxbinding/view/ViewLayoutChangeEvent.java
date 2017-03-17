package com.jakewharton.rxbinding.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;

public final class ViewLayoutChangeEvent extends ViewEvent<View> {
    private final int bottom;
    private final int left;
    private final int oldBottom;
    private final int oldLeft;
    private final int oldRight;
    private final int oldTop;
    private final int right;
    private final int top;

    @CheckResult
    @NonNull
    public static ViewLayoutChangeEvent create(@NonNull View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        return new ViewLayoutChangeEvent(view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom);
    }

    private ViewLayoutChangeEvent(@NonNull View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        super(view);
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.oldLeft = oldLeft;
        this.oldTop = oldTop;
        this.oldRight = oldRight;
        this.oldBottom = oldBottom;
    }

    public int left() {
        return this.left;
    }

    public int top() {
        return this.top;
    }

    public int right() {
        return this.right;
    }

    public int bottom() {
        return this.bottom;
    }

    public int oldLeft() {
        return this.oldLeft;
    }

    public int oldTop() {
        return this.oldTop;
    }

    public int oldRight() {
        return this.oldRight;
    }

    public int oldBottom() {
        return this.oldBottom;
    }

    public int hashCode() {
        return ((((((((((((((((view().hashCode() + 629) * 37) + this.left) * 37) + this.top) * 37) + this.right) * 37) + this.bottom) * 37) + this.oldLeft) * 37) + this.oldTop) * 37) + this.oldRight) * 37) + this.oldBottom;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ViewLayoutChangeEvent)) {
            return false;
        }
        ViewLayoutChangeEvent other = (ViewLayoutChangeEvent) o;
        if (other.view() == view() && other.left == this.left && other.top == this.top && other.right == this.right && other.bottom == this.bottom && other.oldLeft == this.oldLeft && other.oldTop == this.oldTop && other.oldRight == this.oldRight && other.oldBottom == this.oldBottom) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "ViewLayoutChangeEvent{left=" + this.left + ", top=" + this.top + ", right=" + this.right + ", bottom=" + this.bottom + ", oldLeft=" + this.oldLeft + ", oldTop=" + this.oldTop + ", oldRight=" + this.oldRight + ", oldBottom=" + this.oldBottom + '}';
    }
}
