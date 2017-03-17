package com.jakewharton.rxbinding.view;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class ViewGroupHierarchyChangeEventOnSubscribe implements OnSubscribe<ViewGroupHierarchyChangeEvent> {
    final ViewGroup viewGroup;

    ViewGroupHierarchyChangeEventOnSubscribe(ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
    }

    public void call(final Subscriber<? super ViewGroupHierarchyChangeEvent> subscriber) {
        Preconditions.checkUiThread();
        this.viewGroup.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            public void onChildViewAdded(View parent, View child) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(ViewGroupHierarchyChildViewAddEvent.create((ViewGroup) parent, child));
                }
            }

            public void onChildViewRemoved(View parent, View child) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(ViewGroupHierarchyChildViewRemoveEvent.create((ViewGroup) parent, child));
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                ViewGroupHierarchyChangeEventOnSubscribe.this.viewGroup.setOnHierarchyChangeListener(null);
            }
        });
    }
}
