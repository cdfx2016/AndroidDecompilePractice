package com.jakewharton.rxbinding.widget;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.functions.Func1;

final class TextViewEditorActionOnSubscribe implements OnSubscribe<Integer> {
    final Func1<? super Integer, Boolean> handled;
    final TextView view;

    TextViewEditorActionOnSubscribe(TextView view, Func1<? super Integer, Boolean> handled) {
        this.view = view;
        this.handled = handled;
    }

    public void call(final Subscriber<? super Integer> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!((Boolean) TextViewEditorActionOnSubscribe.this.handled.call(Integer.valueOf(actionId))).booleanValue()) {
                    return false;
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(Integer.valueOf(actionId));
                }
                return true;
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                TextViewEditorActionOnSubscribe.this.view.setOnEditorActionListener(null);
            }
        });
    }
}
