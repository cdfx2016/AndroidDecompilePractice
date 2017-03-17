package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.RatingBar;
import com.jakewharton.rxbinding.view.ViewEvent;

public final class RatingBarChangeEvent extends ViewEvent<RatingBar> {
    private final boolean fromUser;
    private final float rating;

    @CheckResult
    @NonNull
    public static RatingBarChangeEvent create(@NonNull RatingBar view, float rating, boolean fromUser) {
        return new RatingBarChangeEvent(view, rating, fromUser);
    }

    private RatingBarChangeEvent(@NonNull RatingBar view, float rating, boolean fromUser) {
        super(view);
        this.rating = rating;
        this.fromUser = fromUser;
    }

    public float rating() {
        return this.rating;
    }

    public boolean fromUser() {
        return this.fromUser;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RatingBarChangeEvent)) {
            return false;
        }
        RatingBarChangeEvent other = (RatingBarChangeEvent) o;
        if (other.view() == view() && other.rating == this.rating && other.fromUser == this.fromUser) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((RatingBar) view()).hashCode() + 629) * 37) + Float.floatToIntBits(this.rating)) * 37) + (this.fromUser ? 1 : 0);
    }

    public String toString() {
        return "RatingBarChangeEvent{view=" + view() + ", rating=" + this.rating + ", fromUser=" + this.fromUser + '}';
    }
}
