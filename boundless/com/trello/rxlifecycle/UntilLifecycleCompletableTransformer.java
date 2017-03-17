package com.trello.rxlifecycle;

import javax.annotation.Nonnull;
import rx.Completable;
import rx.Completable.Transformer;
import rx.Observable;

final class UntilLifecycleCompletableTransformer<T> implements Transformer {
    final Observable<T> lifecycle;

    public UntilLifecycleCompletableTransformer(@Nonnull Observable<T> lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Completable call(Completable source) {
        return Completable.amb(source, this.lifecycle.flatMap(Functions.CANCEL_COMPLETABLE).toCompletable());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.lifecycle.equals(((UntilLifecycleCompletableTransformer) o).lifecycle);
    }

    public int hashCode() {
        return this.lifecycle.hashCode();
    }

    public String toString() {
        return "UntilLifecycleCompletableTransformer{lifecycle=" + this.lifecycle + '}';
    }
}
