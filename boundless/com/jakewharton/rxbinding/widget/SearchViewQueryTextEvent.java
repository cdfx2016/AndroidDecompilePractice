package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.SearchView;
import com.jakewharton.rxbinding.view.ViewEvent;

public final class SearchViewQueryTextEvent extends ViewEvent<SearchView> {
    private final CharSequence queryText;
    private final boolean submitted;

    @CheckResult
    @NonNull
    public static SearchViewQueryTextEvent create(@NonNull SearchView view, @NonNull CharSequence queryText, boolean submitted) {
        return new SearchViewQueryTextEvent(view, queryText, submitted);
    }

    private SearchViewQueryTextEvent(@NonNull SearchView view, @NonNull CharSequence queryText, boolean submitted) {
        super(view);
        this.queryText = queryText;
        this.submitted = submitted;
    }

    @NonNull
    public CharSequence queryText() {
        return this.queryText;
    }

    public boolean isSubmitted() {
        return this.submitted;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SearchViewQueryTextEvent)) {
            return false;
        }
        SearchViewQueryTextEvent other = (SearchViewQueryTextEvent) o;
        if (other.view() == view() && other.queryText.equals(this.queryText) && other.submitted == this.submitted) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((SearchView) view()).hashCode() + 629) * 37) + this.queryText.hashCode()) * 37) + (this.submitted ? 1 : 0);
    }

    public String toString() {
        return "SearchViewQueryTextEvent{view=" + view() + ", queryText=" + this.queryText + ", submitted=" + this.submitted + '}';
    }
}
