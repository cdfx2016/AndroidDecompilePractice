package com.jakewharton.rxbinding.view;

import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.functions.Func0;

final class ViewTreeObserverPreDrawOnSubscribe implements OnSubscribe<Void> {
    final Func0<Boolean> proceedDrawingPass;
    final View view;

    ViewTreeObserverPreDrawOnSubscribe(View view, Func0<Boolean> proceedDrawingPass) {
        this.view = view;
        this.proceedDrawingPass = proceedDrawingPass;
    }

    public void call(final Subscriber<? super Void> subscriber) {
        Preconditions.checkUiThread();
        final OnPreDrawListener listener = new OnPreDrawListener() {
            public boolean onPreDraw() {
                if (subscriber.isUnsubscribed()) {
                    return true;
                }
                subscriber.onNext(null);
                return ((Boolean) ViewTreeObserverPreDrawOnSubscribe.this.proceedDrawingPass.call()).booleanValue();
            }
        };
        this.view.getViewTreeObserver().addOnPreDrawListener(listener);
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                ViewTreeObserverPreDrawOnSubscribe.this.view.getViewTreeObserver().removeOnPreDrawListener(listener);
            }
        });
    }
}
