package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.CheckedTextView;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.functions.Action1;

public final class RxCheckedTextView {
    @CheckResult
    @NonNull
    public static Action1<? super Boolean> check(@NonNull final CheckedTextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Boolean>() {
            public void call(Boolean check) {
                view.setChecked(check.booleanValue());
            }
        };
    }

    private RxCheckedTextView() {
        throw new AssertionError("No instances.");
    }
}
