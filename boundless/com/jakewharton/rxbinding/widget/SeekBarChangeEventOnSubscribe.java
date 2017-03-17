package com.jakewharton.rxbinding.widget;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class SeekBarChangeEventOnSubscribe implements OnSubscribe<SeekBarChangeEvent> {
    final SeekBar view;

    public SeekBarChangeEventOnSubscribe(SeekBar view) {
        this.view = view;
    }

    public void call(final Subscriber<? super SeekBarChangeEvent> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(SeekBarProgressChangeEvent.create(seekBar, progress, fromUser));
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(SeekBarStartChangeEvent.create(seekBar));
                }
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(SeekBarStopChangeEvent.create(seekBar));
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                SeekBarChangeEventOnSubscribe.this.view.setOnSeekBarChangeListener(null);
            }
        });
        subscriber.onNext(SeekBarProgressChangeEvent.create(this.view, this.view.getProgress(), false));
    }
}
