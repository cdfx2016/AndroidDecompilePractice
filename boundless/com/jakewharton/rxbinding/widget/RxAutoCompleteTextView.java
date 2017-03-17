package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.AutoCompleteTextView;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable;
import rx.functions.Action1;

public final class RxAutoCompleteTextView {
    @CheckResult
    @NonNull
    public static Observable<AdapterViewItemClickEvent> itemClickEvents(@NonNull AutoCompleteTextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new AutoCompleteTextViewItemClickEventOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Action1<? super CharSequence> completionHint(@NonNull final AutoCompleteTextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<CharSequence>() {
            public void call(CharSequence completionHint) {
                view.setCompletionHint(completionHint);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Integer> threshold(@NonNull final AutoCompleteTextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Integer>() {
            public void call(Integer threshold) {
                view.setThreshold(threshold.intValue());
            }
        };
    }

    private RxAutoCompleteTextView() {
        throw new AssertionError("No instances.");
    }
}
