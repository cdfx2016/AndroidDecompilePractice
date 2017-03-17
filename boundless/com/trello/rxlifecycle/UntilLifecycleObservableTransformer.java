package com.trello.rxlifecycle;

import javax.annotation.Nonnull;
import rx.Completable;
import rx.Observable;
import rx.Single.Transformer;

final class UntilLifecycleObservableTransformer<T, R> implements LifecycleTransformer<T> {
    final Observable<R> lifecycle;

    public UntilLifecycleObservableTransformer(@Nonnull Observable<R> lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Observable<T> call(Observable<T> source) {
        return source.takeUntil(this.lifecycle);
    }

    @Nonnull
    public Transformer<T, T> forSingle() {
        return new UntilLifecycleSingleTransformer(this.lifecycle);
    }

    @Nonnull
    public Completable.Transformer forCompletable() {
        return new UntilLifecycleCompletableTransformer(this.lifecycle);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.lifecycle.equals(((UntilLifecycleObservableTransformer) o).lifecycle);
    }

    public int hashCode() {
        return this.lifecycle.hashCode();
    }

    public String toString() {
        return "UntilLifecycleObservableTransformer{lifecycle=" + this.lifecycle + '}';
    }
}
