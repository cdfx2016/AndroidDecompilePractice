package com.jakewharton.rxbinding.widget;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class AbsListViewScrollEventOnSubscribe implements OnSubscribe<AbsListViewScrollEvent> {
    final AbsListView view;

    AbsListViewScrollEventOnSubscribe(AbsListView view) {
        this.view = view;
    }

    public void call(final Subscriber<? super AbsListViewScrollEvent> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnScrollListener(new OnScrollListener() {
            int currentScrollState = 0;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(AbsListViewScrollEvent.create(view, scrollState, view.getFirstVisiblePosition(), view.getChildCount(), view.getCount()));
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(AbsListViewScrollEvent.create(view, this.currentScrollState, firstVisibleItem, visibleItemCount, totalItemCount));
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                AbsListViewScrollEventOnSubscribe.this.view.setOnScrollListener(null);
            }
        });
    }
}
