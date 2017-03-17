package com.jakewharton.rxbinding.widget;

import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class PopupMenuItemClickOnSubscribe implements OnSubscribe<MenuItem> {
    final PopupMenu view;

    public PopupMenuItemClickOnSubscribe(PopupMenu view) {
        this.view = view;
    }

    public void call(final Subscriber<? super MenuItem> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(item);
                }
                return true;
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                PopupMenuItemClickOnSubscribe.this.view.setOnMenuItemClickListener(null);
            }
        });
    }
}
