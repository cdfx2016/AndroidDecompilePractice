package com.trello.rxlifecycle;

import javax.annotation.Nonnull;
import rx.Completable;
import rx.Completable.Transformer;
import rx.Observable;
import rx.functions.Func1;

final class UntilCorrespondingEventCompletableTransformer<T> implements Transformer {
    final Func1<T, T> correspondingEvents;
    final Observable<T> sharedLifecycle;

    public UntilCorrespondingEventCompletableTransformer(@Nonnull Observable<T> sharedLifecycle, @Nonnull Func1<T, T> correspondingEvents) {
        this.sharedLifecycle = sharedLifecycle;
        this.correspondingEvents = correspondingEvents;
    }

    public Completable call(Completable source) {
        return Completable.amb(source, TakeUntilGenerator.takeUntilCorrespondingEvent(this.sharedLifecycle, this.correspondingEvents).flatMap(Functions.CANCEL_COMPLETABLE).toCompletable());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UntilCorrespondingEventCompletableTransformer<?> that = (UntilCorrespondingEventCompletableTransformer) o;
        if (this.sharedLifecycle.equals(that.sharedLifecycle)) {
            return this.correspondingEvents.equals(that.correspondingEvents);
        }
        return false;
    }

    public int hashCode() {
        return (this.sharedLifecycle.hashCode() * 31) + this.correspondingEvents.hashCode();
    }

    public String toString() {
        return "UntilCorrespondingEventCompletableTransformer{sharedLifecycle=" + this.sharedLifecycle + ", correspondingEvents=" + this.correspondingEvents + '}';
    }
}
