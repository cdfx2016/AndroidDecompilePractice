package com.trello.rxlifecycle;

import javax.annotation.Nonnull;
import rx.Observable;
import rx.Single;
import rx.Single.Transformer;

final class UntilEventSingleTransformer<T, R> implements Transformer<T, T> {
    final R event;
    final Observable<R> lifecycle;

    public UntilEventSingleTransformer(@Nonnull Observable<R> lifecycle, @Nonnull R event) {
        this.lifecycle = lifecycle;
        this.event = event;
    }

    public Single<T> call(Single<T> source) {
        return source.takeUntil(TakeUntilGenerator.takeUntilEvent(this.lifecycle, this.event));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UntilEventSingleTransformer<?, ?> that = (UntilEventSingleTransformer) o;
        if (this.lifecycle.equals(that.lifecycle)) {
            return this.event.equals(that.event);
        }
        return false;
    }

    public int hashCode() {
        return (this.lifecycle.hashCode() * 31) + this.event.hashCode();
    }

    public String toString() {
        return "UntilEventSingleTransformer{lifecycle=" + this.lifecycle + ", event=" + this.event + '}';
    }
}
