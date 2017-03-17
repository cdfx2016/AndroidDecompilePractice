package com.jakewharton.rxbinding.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable;

public final class RxViewGroup {
    @CheckResult
    @NonNull
    public static Observable<ViewGroupHierarchyChangeEvent> changeEvents(@NonNull ViewGroup viewGroup) {
        Preconditions.checkNotNull(viewGroup, "viewGroup == null");
        return Observable.create(new ViewGroupHierarchyChangeEventOnSubscribe(viewGroup));
    }

    private RxViewGroup() {
        throw new AssertionError("No instances.");
    }
}
