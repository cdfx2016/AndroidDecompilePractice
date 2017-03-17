package com.jakewharton.rxbinding.widget;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class CompoundButtonCheckedChangeOnSubscribe implements OnSubscribe<Boolean> {
    final CompoundButton view;

    public CompoundButtonCheckedChangeOnSubscribe(CompoundButton view) {
        this.view = view;
    }

    public void call(final Subscriber<? super Boolean> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(Boolean.valueOf(isChecked));
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                CompoundButtonCheckedChangeOnSubscribe.this.view.setOnCheckedChangeListener(null);
            }
        });
        subscriber.onNext(Boolean.valueOf(this.view.isChecked()));
    }
}
