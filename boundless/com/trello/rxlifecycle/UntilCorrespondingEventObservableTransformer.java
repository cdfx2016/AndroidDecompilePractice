package com.trello.rxlifecycle;

import javax.annotation.Nonnull;
import rx.Completable;
import rx.Observable;
import rx.Single.Transformer;
import rx.functions.Func1;

final class UntilCorrespondingEventObservableTransformer<T, R> implements LifecycleTransformer<T> {
    final Func1<R, R> correspondingEvents;
    final Observable<R> sharedLifecycle;

    public UntilCorrespondingEventObservableTransformer(@Nonnull Observable<R> sharedLifecycle, @Nonnull Func1<R, R> correspondingEvents) {
        this.sharedLifecycle = sharedLifecycle;
        this.correspondingEvents = correspondingEvents;
    }

    public Observable<T> call(Observable<T> source) {
        return source.takeUntil(TakeUntilGenerator.takeUntilCorrespondingEvent(this.sharedLifecycle, this.correspondingEvents));
    }

    @Nonnull
    public Transformer<T, T> forSingle() {
        return new UntilCorrespondingEventSingleTransformer(this.sharedLifecycle, this.correspondingEvents);
    }

    @Nonnull
    public Completable.Transformer forCompletable() {
        return new UntilCorrespondingEventCompletableTransformer(this.sharedLifecycle, this.correspondingEvents);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UntilCorrespondingEventObservableTransformer<?, ?> that = (UntilCorrespondingEventObservableTransformer) o;
        if (this.sharedLifecycle.equals(that.sharedLifecycle)) {
            return this.correspondingEvents.equals(that.correspondingEvents);
        }
        return false;
    }

    public int hashCode() {
        return (this.sharedLifecycle.hashCode() * 31) + this.correspondingEvents.hashCode();
    }

    public String toString() {
        return "UntilCorrespondingEventObservableTransformer{sharedLifecycle=" + this.sharedLifecycle + ", correspondingEvents=" + this.correspondingEvents + '}';
    }
}
