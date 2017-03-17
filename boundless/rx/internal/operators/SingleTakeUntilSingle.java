package rx.internal.operators;

import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.plugins.RxJavaHooks;

public final class SingleTakeUntilSingle<T, U> implements OnSubscribe<T> {
    final Single<? extends U> other;
    final OnSubscribe<T> source;

    static final class TakeUntilSourceSubscriber<T, U> extends SingleSubscriber<T> {
        final SingleSubscriber<? super T> actual;
        final AtomicBoolean once = new AtomicBoolean();
        final SingleSubscriber<U> other = new OtherSubscriber();

        final class OtherSubscriber extends SingleSubscriber<U> {
            OtherSubscriber() {
            }

            public void onSuccess(U u) {
                onError(new CancellationException("Stream was canceled before emitting a terminal event."));
            }

            public void onError(Throwable error) {
                TakeUntilSourceSubscriber.this.onError(error);
            }
        }

        TakeUntilSourceSubscriber(SingleSubscriber<? super T> actual) {
            this.actual = actual;
            add(this.other);
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
    }

    public SingleTakeUntilSingle(OnSubscribe<T> source, Single<? extends U> other) {
        this.source = source;
        this.other = other;
    }

    public void call(SingleSubscriber<? super T> t) {
        TakeUntilSourceSubscriber<T, U> parent = new TakeUntilSourceSubscriber(t);
        t.add(parent);
        this.other.subscribe(parent.other);
        this.source.call(parent);
    }
}
