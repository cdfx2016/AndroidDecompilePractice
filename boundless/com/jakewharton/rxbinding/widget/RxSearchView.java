package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.SearchView;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable;
import rx.functions.Action1;

public final class RxSearchView {
    @CheckResult
    @NonNull
    public static Observable<SearchViewQueryTextEvent> queryTextChangeEvents(@NonNull SearchView view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new SearchViewQueryTextChangeEventsOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<CharSequence> queryTextChanges(@NonNull SearchView view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new SearchViewQueryTextChangesOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Action1<? super CharSequence> query(@NonNull final SearchView view, final boolean submit) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<CharSequence>() {
            public void call(CharSequence text) {
                view.setQuery(text, submit);
            }
        };
    }

    private RxSearchView() {
        throw new AssertionError("No instances.");
    }
}
