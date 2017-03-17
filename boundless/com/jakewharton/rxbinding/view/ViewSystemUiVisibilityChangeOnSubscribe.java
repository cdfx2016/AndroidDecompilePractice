package com.jakewharton.rxbinding.view;

import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class ViewSystemUiVisibilityChangeOnSubscribe implements OnSubscribe<Integer> {
    final View view;

    ViewSystemUiVisibilityChangeOnSubscribe(View view) {
        this.view = view;
    }

    public void call(final Subscriber<? super Integer> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
            public void onSystemUiVisibilityChange(int visibility) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(Integer.valueOf(visibility));
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                ViewSystemUiVisibilityChangeOnSubscribe.this.view.setOnSystemUiVisibilityChangeListener(null);
            }
        });
    }
}
