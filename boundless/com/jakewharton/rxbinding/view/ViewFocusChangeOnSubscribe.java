package com.jakewharton.rxbinding.view;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class ViewFocusChangeOnSubscribe implements OnSubscribe<Boolean> {
    final View view;

    ViewFocusChangeOnSubscribe(View view) {
        this.view = view;
    }

    public void call(final Subscriber<? super Boolean> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(Boolean.valueOf(hasFocus));
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                ViewFocusChangeOnSubscribe.this.view.setOnFocusChangeListener(null);
            }
        });
        subscriber.onNext(Boolean.valueOf(this.view.hasFocus()));
    }
}
