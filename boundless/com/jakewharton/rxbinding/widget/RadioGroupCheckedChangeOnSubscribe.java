package com.jakewharton.rxbinding.widget;

import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class RadioGroupCheckedChangeOnSubscribe implements OnSubscribe<Integer> {
    final RadioGroup view;

    public RadioGroupCheckedChangeOnSubscribe(RadioGroup view) {
        this.view = view;
    }

    public void call(final Subscriber<? super Integer> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(Integer.valueOf(checkedId));
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                RadioGroupCheckedChangeOnSubscribe.this.view.setOnCheckedChangeListener(null);
            }
        });
        subscriber.onNext(Integer.valueOf(this.view.getCheckedRadioButtonId()));
    }
}
