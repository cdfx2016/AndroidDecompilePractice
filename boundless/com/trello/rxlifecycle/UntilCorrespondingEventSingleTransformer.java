package com.trello.rxlifecycle;

import javax.annotation.Nonnull;
import rx.Observable;
import rx.Single;
import rx.Single.Transformer;
import rx.functions.Func1;

final class UntilCorrespondingEventSingleTransformer<T, R> implements Transformer<T, T> {
    final Func1<R, R> correspondingEvents;
    final Observable<R> sharedLifecycle;

    public UntilCorrespondingEventSingleTransformer(@Nonnull Observable<R> sharedLifecycle, @Nonnull Func1<R, R> correspondingEvents) {
        this.sharedLifecycle = sharedLifecycle;
        this.correspondingEvents = correspondingEvents;
    }

    public Single<T> call(Single<T> source) {
        return source.takeUntil(TakeUntilGenerator.takeUntilCorrespondingEvent(this.sharedLifecycle, this.correspondingEvents));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UntilCorrespondingEventSingleTransformer<?, ?> that = (UntilCorrespondingEventSingleTransformer) o;
        if (this.sharedLifecycle.equals(that.sharedLifecycle)) {
            return this.correspondingEvents.equals(that.correspondingEvents);
        }
        return false;
    }

    public int hashCode() {
        return (this.sharedLifecycle.hashCode() * 31) + this.correspondingEvents.hashCode();
    }

    public String toString() {
        return "UntilCorrespondingEventSingleTransformer{sharedLifecycle=" + this.sharedLifecycle + ", correspondingEvents=" + this.correspondingEvents + '}';
    }
}
