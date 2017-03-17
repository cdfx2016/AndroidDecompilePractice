package rx.internal.operators;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.exceptions.Exceptions;
import rx.subscriptions.Subscriptions;

public final class SingleFromFuture<T> implements OnSubscribe<T> {
    final Future<? extends T> future;
    final long timeout;
    final TimeUnit unit;

    public SingleFromFuture(Future<? extends T> future, long timeout, TimeUnit unit) {
        this.future = future;
        this.timeout = timeout;
        this.unit = unit;
    }

    public void call(SingleSubscriber<? super T> t) {
        Future f = this.future;
        t.add(Subscriptions.from(f));
        try {
            T v;
            if (this.timeout == 0) {
                v = f.get();
            } else {
                v = f.get(this.timeout, this.unit);
            }
            t.onSuccess(v);
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            t.onError(ex);
        }
    }
}
