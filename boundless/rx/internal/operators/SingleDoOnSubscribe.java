package rx.internal.operators;

import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.exceptions.Exceptions;
import rx.functions.Action0;

public final class SingleDoOnSubscribe<T> implements OnSubscribe<T> {
    final Action0 onSubscribe;
    final OnSubscribe<T> source;

    public SingleDoOnSubscribe(OnSubscribe<T> source, Action0 onSubscribe) {
        this.source = source;
        this.onSubscribe = onSubscribe;
    }

    public void call(SingleSubscriber<? super T> t) {
        try {
            this.onSubscribe.call();
            this.source.call(t);
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            t.onError(ex);
        }
    }
}
