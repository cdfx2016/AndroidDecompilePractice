package com.jakewharton.rxbinding.view;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.functions.Func1;

final class ViewTouchOnSubscribe implements OnSubscribe<MotionEvent> {
    final Func1<? super MotionEvent, Boolean> handled;
    final View view;

    ViewTouchOnSubscribe(View view, Func1<? super MotionEvent, Boolean> handled) {
        this.view = view;
        this.handled = handled;
    }

    public void call(final Subscriber<? super MotionEvent> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, @NonNull MotionEvent event) {
                if (!((Boolean) ViewTouchOnSubscribe.this.handled.call(event)).booleanValue()) {
                    return false;
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(event);
                }
                return true;
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                ViewTouchOnSubscribe.this.view.setOnTouchListener(null);
            }
        });
    }
}
