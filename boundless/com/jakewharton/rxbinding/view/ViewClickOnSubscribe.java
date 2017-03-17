package com.jakewharton.rxbinding.view;

import android.view.View;
import android.view.View.OnClickListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class ViewClickOnSubscribe implements OnSubscribe<Void> {
    final View view;

    ViewClickOnSubscribe(View view) {
        this.view = view;
    }

    public void call(final Subscriber<? super Void> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(null);
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                ViewClickOnSubscribe.this.view.setOnClickListener(null);
            }
        });
    }
}
