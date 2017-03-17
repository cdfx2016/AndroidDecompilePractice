package com.trello.rxlifecycle;

import javax.annotation.Nonnull;
import rx.Observable;
import rx.Single;
import rx.Single.Transformer;

final class UntilLifecycleSingleTransformer<T, R> implements Transformer<T, T> {
    final Observable<R> lifecycle;

    public UntilLifecycleSingleTransformer(@Nonnull Observable<R> lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Single<T> call(Single<T> source) {
        return source.takeUntil(this.lifecycle);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.lifecycle.equals(((UntilLifecycleSingleTransformer) o).lifecycle);
    }

    public int hashCode() {
        return this.lifecycle.hashCode();
    }

    public String toString() {
        return "UntilLifecycleSingleTransformer{lifecycle=" + this.lifecycle + '}';
    }
}
