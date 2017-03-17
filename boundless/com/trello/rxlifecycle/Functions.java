package com.trello.rxlifecycle;

import java.util.concurrent.CancellationException;
import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Func1;

final class Functions {
    static final Func1<Object, Observable<Object>> CANCEL_COMPLETABLE = new Func1<Object, Observable<Object>>() {
        public Observable<Object> call(Object ignore) {
            return Observable.error(new CancellationException());
        }
    };
    static final Func1<Throwable, Boolean> RESUME_FUNCTION = new Func1<Throwable, Boolean>() {
        public Boolean call(Throwable throwable) {
            if (throwable instanceof OutsideLifecycleException) {
                return Boolean.valueOf(true);
            }
            Exceptions.propagate(throwable);
            return Boolean.valueOf(false);
        }
    };
    static final Func1<Boolean, Boolean> SHOULD_COMPLETE = new Func1<Boolean, Boolean>() {
        public Boolean call(Boolean shouldComplete) {
            return shouldComplete;
        }
    };

    private Functions() {
        throw new AssertionError("No instances!");
    }
}
