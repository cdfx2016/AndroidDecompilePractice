package com.jakewharton.rxbinding.widget;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class AutoCompleteTextViewItemClickEventOnSubscribe implements OnSubscribe<AdapterViewItemClickEvent> {
    final AutoCompleteTextView view;

    public AutoCompleteTextViewItemClickEventOnSubscribe(AutoCompleteTextView view) {
        this.view = view;
    }

    public void call(final Subscriber<? super AdapterViewItemClickEvent> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(AdapterViewItemClickEvent.create(parent, view, position, id));
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                AutoCompleteTextViewItemClickEventOnSubscribe.this.view.setOnItemClickListener(null);
            }
        });
    }
}
