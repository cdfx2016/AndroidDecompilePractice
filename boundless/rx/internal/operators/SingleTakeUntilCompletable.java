package rx.internal.operators;

import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import rx.Completable;
import rx.CompletableSubscriber;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.plugins.RxJavaHooks;

public final class SingleTakeUntilCompletable<T> implements OnSubscribe<T> {
    final Completable other;
    final OnSubscribe<T> source;

    static final class TakeUntilSourceSubscriber<T> extends SingleSubscriber<T> implements CompletableSubscriber {
        final SingleSubscriber<? super T> actual;
        final AtomicBoolean once = new AtomicBoolean();

        TakeUntilSourceSubscriber(SingleSubscriber<? super T> actual) {
            this.actual = actual;
        }

        public void onSubscribe(Subscription d) {
            add(d);
        }

        public void onSuccess(T value) {
            if (this.once.compareAndSet(false, true)) {
                unsubscribe();
                this.actual.onSuccess(value);
            }
        }

        public void onError(Throwable error) {
            if (this.once.compareAndSet(false, true)) {
                unsubscribe();
                this.actual.onError(error);
                return;
            }
            RxJavaHooks.onError(error);
        }

        public void onCompleted() {
            onError(new CancellationException("Stream was canceled before emitting a terminal event."));
        }
    }

    public SingleTakeUntilCompletable(OnSubscribe<T> source, Completable other) {
        this.source = source;
        this.other = other;
    }

    public void call(SingleSubscriber<? super T> t) {
        CompletableSubscriber parent = new TakeUntilSourceSubscriber(t);
        t.add(parent);
        this.other.subscribe(parent);
        this.source.call(parent);
    }
}
