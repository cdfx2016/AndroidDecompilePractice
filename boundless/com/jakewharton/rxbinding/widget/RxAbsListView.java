package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.AbsListView;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable;

public final class RxAbsListView {
    @CheckResult
    @NonNull
    public static Observable<AbsListViewScrollEvent> scrollEvents(@NonNull AbsListView absListView) {
        Preconditions.checkNotNull(absListView, "absListView == null");
        return Observable.create(new AbsListViewScrollEventOnSubscribe(absListView));
    }

    private RxAbsListView() {
        throw new AssertionError("No instances.");
    }
}
