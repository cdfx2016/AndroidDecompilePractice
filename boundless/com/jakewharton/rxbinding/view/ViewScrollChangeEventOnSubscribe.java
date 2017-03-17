package com.jakewharton.rxbinding.view;

import android.annotation.TargetApi;
import android.view.View;
import android.view.View.OnScrollChangeListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

@TargetApi(23)
final class ViewScrollChangeEventOnSubscribe implements OnSubscribe<ViewScrollChangeEvent> {
    final View view;

    ViewScrollChangeEventOnSubscribe(View view) {
        this.view = view;
    }

    public void call(final Subscriber<? super ViewScrollChangeEvent> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnScrollChangeListener(new OnScrollChangeListener() {
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(ViewScrollChangeEvent.create(ViewScrollChangeEventOnSubscribe.this.view, scrollX, scrollY, oldScrollX, oldScrollY));
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                ViewScrollChangeEventOnSubscribe.this.view.setOnScrollChangeListener(null);
            }
        });
    }
}
