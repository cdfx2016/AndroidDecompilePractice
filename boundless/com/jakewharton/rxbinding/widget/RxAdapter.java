package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.Adapter;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable;

public final class RxAdapter {
    @CheckResult
    @NonNull
    public static <T extends Adapter> Observable<T> dataChanges(@NonNull T adapter) {
        Preconditions.checkNotNull(adapter, "adapter == null");
        return Observable.create(new AdapterDataChangeOnSubscribe(adapter));
    }

    private RxAdapter() {
        throw new AssertionError("No instances.");
    }
}
