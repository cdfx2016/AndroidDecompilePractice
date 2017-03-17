package com.jakewharton.rxbinding.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class TextViewAfterTextChangeEventOnSubscribe implements OnSubscribe<TextViewAfterTextChangeEvent> {
    final TextView view;

    TextViewAfterTextChangeEventOnSubscribe(TextView view) {
        this.view = view;
    }

    public void call(final Subscriber<? super TextViewAfterTextChangeEvent> subscriber) {
        Preconditions.checkUiThread();
        final TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(TextViewAfterTextChangeEvent.create(TextViewAfterTextChangeEventOnSubscribe.this.view, s));
                }
            }
        };
        this.view.addTextChangedListener(watcher);
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                TextViewAfterTextChangeEventOnSubscribe.this.view.removeTextChangedListener(watcher);
            }
        });
        subscriber.onNext(TextViewAfterTextChangeEvent.create(this.view, this.view.getEditableText()));
    }
}
