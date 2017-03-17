package com.jakewharton.rxbinding.view;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import com.jakewharton.rxbinding.view.ViewAttachEvent.Kind;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class ViewAttachEventOnSubscribe implements OnSubscribe<ViewAttachEvent> {
    final View view;

    ViewAttachEventOnSubscribe(View view) {
        this.view = view;
    }

    public void call(final Subscriber<? super ViewAttachEvent> subscriber) {
        Preconditions.checkUiThread();
        final OnAttachStateChangeListener listener = new OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(@NonNull View v) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(ViewAttachEvent.create(ViewAttachEventOnSubscribe.this.view, Kind.ATTACH));
                }
            }

            public void onViewDetachedFromWindow(@NonNull View v) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(ViewAttachEvent.create(ViewAttachEventOnSubscribe.this.view, Kind.DETACH));
                }
            }
        };
        this.view.addOnAttachStateChangeListener(listener);
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                ViewAttachEventOnSubscribe.this.view.removeOnAttachStateChangeListener(listener);
            }
        });
    }
}
