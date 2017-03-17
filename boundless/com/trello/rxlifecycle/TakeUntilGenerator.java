package com.trello.rxlifecycle;

import javax.annotation.Nonnull;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

final class TakeUntilGenerator {
    @Nonnull
    static <T> Observable<T> takeUntilEvent(@Nonnull Observable<T> lifecycle, @Nonnull final T event) {
        return lifecycle.takeFirst(new Func1<T, Boolean>() {
            public Boolean call(T lifecycleEvent) {
                return Boolean.valueOf(lifecycleEvent.equals(event));
            }
        });
    }

    @Nonnull
    static <T> Observable<Boolean> takeUntilCorrespondingEvent(@Nonnull Observable<T> lifecycle, @Nonnull Func1<T, T> correspondingEvents) {
        return Observable.combineLatest(lifecycle.take(1).map(correspondingEvents), lifecycle.skip(1), new Func2<T, T, Boolean>() {
            public Boolean call(T bindUntilEvent, T lifecycleEvent) {
                return Boolean.valueOf(lifecycleEvent.equals(bindUntilEvent));
            }
        }).onErrorReturn(Functions.RESUME_FUNCTION).takeFirst(Functions.SHOULD_COMPLETE);
    }

    private TakeUntilGenerator() {
        throw new AssertionError("No instances!");
    }
}
