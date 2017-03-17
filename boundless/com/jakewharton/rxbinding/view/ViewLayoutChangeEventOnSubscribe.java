package com.jakewharton.rxbinding.view;

import android.view.View;
import android.view.View.OnLayoutChangeListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class ViewLayoutChangeEventOnSubscribe implements OnSubscribe<ViewLayoutChangeEvent> {
    final View view;

    ViewLayoutChangeEventOnSubscribe(View view) {
        this.view = view;
    }

    public void call(final Subscriber<? super ViewLayoutChangeEvent> subscriber) {
        Preconditions.checkUiThread();
        final OnLayoutChangeListener listener = new OnLayoutChangeListener() {
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(ViewLayoutChangeEvent.create(ViewLayoutChangeEventOnSubscribe.this.view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom));
                }
            }
        };
        this.view.addOnLayoutChangeListener(listener);
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                ViewLayoutChangeEventOnSubscribe.this.view.removeOnLayoutChangeListener(listener);
            }
        });
    }
}
