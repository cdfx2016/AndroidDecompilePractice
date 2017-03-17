package com.trello.rxlifecycle;

import javax.annotation.Nonnull;
import rx.Completable;
import rx.Observable;
import rx.Single.Transformer;

final class UntilEventObservableTransformer<T, R> implements LifecycleTransformer<T> {
    final R event;
    final Observable<R> lifecycle;

    public UntilEventObservableTransformer(@Nonnull Observable<R> lifecycle, @Nonnull R event) {
        this.lifecycle = lifecycle;
        this.event = event;
    }

    public Observable<T> call(Observable<T> source) {
        return source.takeUntil(TakeUntilGenerator.takeUntilEvent(this.lifecycle, this.event));
    }

    @Nonnull
    public Transformer<T, T> forSingle() {
        return new UntilEventSingleTransformer(this.lifecycle, this.event);
    }

    @Nonnull
    public Completable.Transformer forCompletable() {
        return new UntilEventCompletableTransformer(this.lifecycle, this.event);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UntilEventObservableTransformer<?, ?> that = (UntilEventObservableTransformer) o;
        if (this.lifecycle.equals(that.lifecycle)) {
            return this.event.equals(that.event);
        }
        return false;
    }

    public int hashCode() {
        return (this.lifecycle.hashCode() * 31) + this.event.hashCode();
    }

    public String toString() {
        return "UntilEventObservableTransformer{lifecycle=" + this.lifecycle + ", event=" + this.event + '}';
    }
}
