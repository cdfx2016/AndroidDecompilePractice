package rx.internal.operators;

import java.util.concurrent.Callable;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.exceptions.Exceptions;

public final class SingleFromCallable<T> implements OnSubscribe<T> {
    final Callable<? extends T> callable;

    public SingleFromCallable(Callable<? extends T> callable) {
        this.callable = callable;
    }

    public void call(SingleSubscriber<? super T> t) {
        try {
            t.onSuccess(this.callable.call());
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            t.onError(ex);
        }
    }
}
