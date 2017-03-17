package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.CompoundButton;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable;
import rx.functions.Action1;

public final class RxCompoundButton {
    @CheckResult
    @NonNull
    public static Observable<Boolean> checkedChanges(@NonNull CompoundButton view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new CompoundButtonCheckedChangeOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Action1<? super Boolean> checked(@NonNull final CompoundButton view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Boolean>() {
            public void call(Boolean value) {
                view.setChecked(value.booleanValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Object> toggle(@NonNull final CompoundButton view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Object>() {
            public void call(Object value) {
                view.toggle();
            }
        };
    }

    private RxCompoundButton() {
        throw new AssertionError("No instances.");
    }
}
