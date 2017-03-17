package rx.internal.operators;

import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import rx.Observable;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.plugins.RxJavaHooks;

public final class SingleTakeUntilObservable<T, U> implements OnSubscribe<T> {
    final Observable<? extends U> other;
    final OnSubscribe<T> source;

    static final class TakeUntilSourceSubscriber<T, U> extends SingleSubscriber<T> {
        final SingleSubscriber<? super T> actual;
        final AtomicBoolean once = new AtomicBoolean();
        final Subscriber<U> other = new OtherSubscriber();

        final class OtherSubscriber extends Subscriber<U> {
            OtherSubscriber() {
            }

            public void onNext(U u) {
                onCompleted();
            }

            public void onError(Throwable error) {
                TakeUntilSourceSubscriber.this.onError(error);
            }

            public void onCompleted() {
                onError(new CancellationException("Stream was canceled before emitting a terminal event."));
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

    public SingleTakeUntilObservable(OnSubscribe<T> source, Observable<? extends U> other) {
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
