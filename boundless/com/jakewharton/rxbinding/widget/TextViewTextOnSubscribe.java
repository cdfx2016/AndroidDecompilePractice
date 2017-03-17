package com.jakewharton.rxbinding.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class TextViewTextOnSubscribe implements OnSubscribe<CharSequence> {
    final TextView view;

    TextViewTextOnSubscribe(TextView view) {
        this.view = view;
    }

    public void call(final Subscriber<? super CharSequence> subscriber) {
        Preconditions.checkUiThread();
        final TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(s);
                }
            }

            public void afterTextChanged(Editable s) {
            }
        };
        this.view.addTextChangedListener(watcher);
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                TextViewTextOnSubscribe.this.view.removeTextChangedListener(watcher);
            }
        });
        subscriber.onNext(this.view.getText());
    }
}
