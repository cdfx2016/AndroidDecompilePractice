package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.PopupMenu;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable;

public final class RxPopupMenu {
    @CheckResult
    @NonNull
    public static Observable<MenuItem> itemClicks(@NonNull PopupMenu view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new PopupMenuItemClickOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<Void> dismisses(@NonNull PopupMenu view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new PopupMenuDismissOnSubscribe(view));
    }

    private RxPopupMenu() {
        throw new AssertionError("No instances.");
    }
}
