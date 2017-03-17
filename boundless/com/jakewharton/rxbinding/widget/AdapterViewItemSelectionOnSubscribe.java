package com.jakewharton.rxbinding.widget;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class AdapterViewItemSelectionOnSubscribe implements OnSubscribe<Integer> {
    final AdapterView<?> view;

    public AdapterViewItemSelectionOnSubscribe(AdapterView<?> view) {
        this.view = view;
    }

    public void call(final Subscriber<? super Integer> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(Integer.valueOf(position));
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(Integer.valueOf(-1));
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                AdapterViewItemSelectionOnSubscribe.this.view.setOnItemSelectedListener(null);
            }
        });
        subscriber.onNext(Integer.valueOf(this.view.getSelectedItemPosition()));
    }
}
