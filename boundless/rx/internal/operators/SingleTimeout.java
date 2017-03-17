package rx.internal.operators;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.functions.Action0;
import rx.plugins.RxJavaHooks;

public final class SingleTimeout<T> implements OnSubscribe<T> {
    final OnSubscribe<? extends T> other;
    final Scheduler scheduler;
    final OnSubscribe<T> source;
    final long timeout;
    final TimeUnit unit;

    static final class TimeoutSingleSubscriber<T> extends SingleSubscriber<T> implements Action0 {
        final SingleSubscriber<? super T> actual;
        final AtomicBoolean once = new AtomicBoolean();
        final OnSubscribe<? extends T> other;

        static final class OtherSubscriber<T> extends SingleSubscriber<T> {
            final SingleSubscriber<? super T> actual;

            OtherSubscriber(SingleSubscriber<? super T> actual) {
                this.actual = actual;
            }

            public void onSuccess(T value) {
                this.actual.onSuccess(value);
            }

            public void onError(Throwable error) {
                this.actual.onError(error);
            }
        }

        TimeoutSingleSubscriber(SingleSubscriber<? super T> actual, OnSubscribe<? extends T> other) {
            this.actual = actual;
            this.other = other;
        }

        public void onSuccess(T value) {
            if (this.once.compareAndSet(false, true)) {
                try {
                    this.actual.onSuccess(value);
                } finally {
                    unsubscribe();
                }
            }
        }

        public void onError(Throwable error) {
            if (this.once.compareAndSet(false, true)) {
                try {
                    this.actual.onError(error);
                } finally {
                    unsubscribe();
                }
            } else {
                RxJavaHooks.onError(error);
            }
        }

        public void call() {
            if (this.once.compareAndSet(false, true)) {
                try {
                    OnSubscribe<? extends T> o = this.other;
                    if (o == null) {
                        this.actual.onError(new TimeoutException());
                    } else {
                        OtherSubscriber<T> p = new OtherSubscriber(this.actual);
                        this.actual.add(p);
                        o.call(p);
                    }
                    unsubscribe();
                } catch (Throwable th) {
                    unsubscribe();
                }
            }
        }
    }

    public SingleTimeout(OnSubscribe<T> source, long timeout, TimeUnit unit, Scheduler scheduler, OnSubscribe<? extends T> other) {
        this.source = source;
        this.timeout = timeout;
        this.unit = unit;
        this.scheduler = scheduler;
        this.other = other;
    }

    public void call(SingleSubscriber<? super T> t) {
        TimeoutSingleSubscriber<T> parent = new TimeoutSingleSubscriber(t, this.other);
        Worker w = this.scheduler.createWorker();
        parent.add(w);
        t.add(parent);
        w.schedule(parent, this.timeout, this.unit);
        this.source.call(parent);
    }
}
