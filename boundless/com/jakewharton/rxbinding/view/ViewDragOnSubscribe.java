package com.jakewharton.rxbinding.view;

import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.functions.Func1;

final class ViewDragOnSubscribe implements OnSubscribe<DragEvent> {
    final Func1<? super DragEvent, Boolean> handled;
    final View view;

    ViewDragOnSubscribe(View view, Func1<? super DragEvent, Boolean> handled) {
        this.view = view;
        this.handled = handled;
    }

    public void call(final Subscriber<? super DragEvent> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnDragListener(new OnDragListener() {
            public boolean onDrag(View v, DragEvent event) {
                if (!((Boolean) ViewDragOnSubscribe.this.handled.call(event)).booleanValue()) {
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
                ViewDragOnSubscribe.this.view.setOnDragListener(null);
            }
        });
    }
}
