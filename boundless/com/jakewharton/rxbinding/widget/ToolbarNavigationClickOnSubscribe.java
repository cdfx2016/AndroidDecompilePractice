package com.jakewharton.rxbinding.widget;

import android.annotation.TargetApi;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toolbar;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

@TargetApi(21)
final class ToolbarNavigationClickOnSubscribe implements OnSubscribe<Void> {
    final Toolbar view;

    public ToolbarNavigationClickOnSubscribe(Toolbar view) {
        this.view = view;
    }

    public void call(final Subscriber<? super Void> subscriber) {
        Preconditions.checkUiThread();
        this.view.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(null);
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                ToolbarNavigationClickOnSubscribe.this.view.setNavigationOnClickListener(null);
            }
        });
    }
}
