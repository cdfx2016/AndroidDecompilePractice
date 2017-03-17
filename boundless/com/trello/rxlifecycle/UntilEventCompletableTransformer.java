package com.trello.rxlifecycle;

import javax.annotation.Nonnull;
import rx.Completable;
import rx.Completable.Transformer;
import rx.Observable;

final class UntilEventCompletableTransformer<T> implements Transformer {
    final T event;
    final Observable<T> lifecycle;

    public UntilEventCompletableTransformer(@Nonnull Observable<T> lifecycle, @Nonnull T event) {
        this.lifecycle = lifecycle;
        this.event = event;
    }

    public Completable call(Completable source) {
        return Completable.amb(source, TakeUntilGenerator.takeUntilEvent(this.lifecycle, this.event).flatMap(Functions.CANCEL_COMPLETABLE).toCompletable());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UntilEventCompletableTransformer<?> that = (UntilEventCompletableTransformer) o;
        if (this.lifecycle.equals(that.lifecycle)) {
            return this.event.equals(that.event);
        }
        return false;
    }

    public int hashCode() {
        return (this.lifecycle.hashCode() * 31) + this.event.hashCode();
    }
}
