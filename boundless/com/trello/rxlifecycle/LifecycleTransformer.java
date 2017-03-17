package com.trello.rxlifecycle;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import rx.Completable;
import rx.Observable.Transformer;
import rx.Single;

public interface LifecycleTransformer<T> extends Transformer<T, T> {
    @CheckReturnValue
    @Nonnull
    Completable.Transformer forCompletable();

    @CheckReturnValue
    @Nonnull
    <U> Single.Transformer<U, U> forSingle();
}
