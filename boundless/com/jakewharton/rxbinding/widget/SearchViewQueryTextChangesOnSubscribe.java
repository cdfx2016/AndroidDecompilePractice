package com.jakewharton.rxbinding.widget;

import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class SearchViewQueryTextChangesOnSubscribe implements OnSubscribe<CharSequence> {
    final SearchView view;

    SearchViewQueryTextChangesOnSubscribe(SearchView view) {
        this.view = view;
    }

    public void call(final Subscriber<? super CharSequence> subscriber) {
        Preconditions.checkUiThread();
        this.view.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextChange(String s) {
                if (subscriber.isUnsubscribed()) {
                    return false;
                }
                subscriber.onNext(s);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
        subscriber.add(new MainThreadSubscription() {
            protected void onUnsubscribe() {
                SearchViewQueryTextChangesOnSubscribe.this.view.setOnQueryTextListener(null);
            }
        });
        subscriber.onNext(this.view.getQuery());
    }
}
