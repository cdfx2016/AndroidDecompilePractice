package com.jakewharton.rxbinding.widget;

import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class RatingBarRatingChangeOnSubscribe implements OnSubscribe<Float> {
    final RatingBar view;

    public RatingBarRatingChangeOnSubscribe(RatingBar view) {
        this.view = view;
    }

    public void call(final Subscriber<? super Float> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(Float.valueOf(rating));
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                RatingBarRatingChangeOnSubscribe.this.view.setOnRatingBarChangeListener(null);
            }
        });
        subscriber.onNext(Float.valueOf(this.view.getRating()));
    }
}
