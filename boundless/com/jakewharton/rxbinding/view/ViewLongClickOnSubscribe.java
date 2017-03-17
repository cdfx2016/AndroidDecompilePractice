package com.jakewharton.rxbinding.view;

import android.view.View;
import android.view.View.OnLongClickListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.functions.Func0;

final class ViewLongClickOnSubscribe implements OnSubscribe<Void> {
    final Func0<Boolean> handled;
    final View view;

    ViewLongClickOnSubscribe(View view, Func0<Boolean> handled) {
        this.view = view;
        this.handled = handled;
    }

    public void call(final Subscriber<? super Void> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (!((Boolean) ViewLongClickOnSubscribe.this.handled.call()).booleanValue()) {
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
                ViewLongClickOnSubscribe.this.view.setOnLongClickListener(null);
            }
        });
    }
}
