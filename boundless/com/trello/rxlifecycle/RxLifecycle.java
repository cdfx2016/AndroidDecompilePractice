package com.trello.rxlifecycle;

import com.trello.rxlifecycle.internal.Preconditions;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import rx.Observable;
import rx.functions.Func1;

public class RxLifecycle {
    private RxLifecycle() {
        throw new AssertionError("No instances");
    }

    @CheckReturnValue
    @Nonnull
    public static <T, R> LifecycleTransformer<T> bindUntilEvent(@Nonnull Observable<R> lifecycle, @Nonnull R event) {
        Preconditions.checkNotNull(lifecycle, "lifecycle == null");
        Preconditions.checkNotNull(event, "event == null");
        return new UntilEventObservableTransformer(lifecycle, event);
    }

    @CheckReturnValue
    @Nonnull
    public static <T, R> LifecycleTransformer<T> bind(@Nonnull Observable<R> lifecycle) {
        Preconditions.checkNotNull(lifecycle, "lifecycle == null");
        return new UntilLifecycleObservableTransformer(lifecycle);
    }

    @CheckReturnValue
    @Nonnull
    public static <T, R> LifecycleTransformer<T> bind(@Nonnull Observable<R> lifecycle, @Nonnull Func1<R, R> correspondingEvents) {
        Preconditions.checkNotNull(lifecycle, "lifecycle == null");
        Preconditions.checkNotNull(correspondingEvents, "correspondingEvents == null");
        return new UntilCorrespondingEventObservableTransformer(lifecycle.share(), correspondingEvents);
    }
}
