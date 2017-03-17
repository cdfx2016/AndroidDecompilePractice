package com.jakewharton.rxbinding.view;

import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.functions.Func1;

final class MenuItemClickOnSubscribe implements OnSubscribe<Void> {
    final Func1<? super MenuItem, Boolean> handled;
    final MenuItem menuItem;

    MenuItemClickOnSubscribe(MenuItem menuItem, Func1<? super MenuItem, Boolean> handled) {
        this.menuItem = menuItem;
        this.handled = handled;
    }

    public void call(final Subscriber<? super Void> subscriber) {
        Preconditions.checkUiThread();
        this.menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (!((Boolean) MenuItemClickOnSubscribe.this.handled.call(MenuItemClickOnSubscribe.this.menuItem)).booleanValue()) {
                    return false;
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(null);
                }
                return true;
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                MenuItemClickOnSubscribe.this.menuItem.setOnMenuItemClickListener(null);
            }
        });
    }
}
