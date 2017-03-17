package com.jakewharton.rxbinding.widget;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class AdapterViewSelectionOnSubscribe implements OnSubscribe<AdapterViewSelectionEvent> {
    final AdapterView<?> view;

    public AdapterViewSelectionOnSubscribe(AdapterView<?> view) {
        this.view = view;
    }

    public void call(final Subscriber<? super AdapterViewSelectionEvent> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(AdapterViewItemSelectionEvent.create(parent, view, position, id));
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(AdapterViewNothingSelectionEvent.create(parent));
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                AdapterViewSelectionOnSubscribe.this.view.setOnItemSelectedListener(null);
            }
        });
        int selectedPosition = this.view.getSelectedItemPosition();
        if (selectedPosition == -1) {
            subscriber.onNext(AdapterViewNothingSelectionEvent.create(this.view));
            return;
        }
        subscriber.onNext(AdapterViewItemSelectionEvent.create(this.view, this.view.getSelectedView(), selectedPosition, this.view.getSelectedItemId()));
    }
}
