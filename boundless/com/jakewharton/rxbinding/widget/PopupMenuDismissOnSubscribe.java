package com.jakewharton.rxbinding.widget;

import android.widget.PopupMenu;
import android.widget.PopupMenu.OnDismissListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class PopupMenuDismissOnSubscribe implements OnSubscribe<Void> {
    final PopupMenu view;

    public PopupMenuDismissOnSubscribe(PopupMenu view) {
        this.view = view;
    }

    public void call(final Subscriber<? super Void> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(PopupMenu menu) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(null);
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                PopupMenuDismissOnSubscribe.this.view.setOnDismissListener(null);
            }
        });
    }
}
