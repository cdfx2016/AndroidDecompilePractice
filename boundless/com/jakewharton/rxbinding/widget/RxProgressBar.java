package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.functions.Action1;

public final class RxProgressBar {
    @CheckResult
    @NonNull
    public static Action1<? super Integer> incrementProgressBy(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Integer>() {
            public void call(Integer value) {
                view.incrementProgressBy(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Integer> incrementSecondaryProgressBy(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Integer>() {
            public void call(Integer value) {
                view.incrementSecondaryProgressBy(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Boolean> indeterminate(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Boolean>() {
            public void call(Boolean value) {
                view.setIndeterminate(value.booleanValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Integer> max(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Integer>() {
            public void call(Integer value) {
                view.setMax(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Integer> progress(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Integer>() {
            public void call(Integer value) {
                view.setProgress(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Integer> secondaryProgress(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Integer>() {
            public void call(Integer value) {
                view.setSecondaryProgress(value.intValue());
            }
        };
    }

    private RxProgressBar() {
        throw new AssertionError("No instances.");
    }
}
