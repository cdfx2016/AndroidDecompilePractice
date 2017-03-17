package rx.internal.operators;

import java.util.concurrent.TimeUnit;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.functions.Action0;

public final class SingleDelay<T> implements OnSubscribe<T> {
    final long delay;
    final Scheduler scheduler;
    final OnSubscribe<T> source;
    final TimeUnit unit;

    static final class ObserveOnSingleSubscriber<T> extends SingleSubscriber<T> implements Action0 {
        final SingleSubscriber<? super T> actual;
        final long delay;
        Throwable error;
        final TimeUnit unit;
        T value;
        final Worker w;

        public ObserveOnSingleSubscriber(SingleSubscriber<? super T> actual, Worker w, long delay, TimeUnit unit) {
            this.actual = actual;
            this.w = w;
            this.delay = delay;
            this.unit = unit;
        }

        public void onSuccess(T value) {
            this.value = value;
            this.w.schedule(this, this.delay, this.unit);
        }

        public void onError(Throwable error) {
            this.error = error;
            this.w.schedule(this, this.delay, this.unit);
        }

        public void call() {
            try {
                Throwable ex = this.error;
                if (ex != null) {
                    this.error = null;
                    this.actual.onError(ex);
                } else {
                    T v = this.value;
                    this.value = null;
                    this.actual.onSuccess(v);
                }
                this.w.unsubscribe();
            } catch (Throwable th) {
                this.w.unsubscribe();
            }
        }
    }

    public SingleDelay(OnSubscribe<T> source, long delay, TimeUnit unit, Scheduler scheduler) {
        this.source = source;
        this.scheduler = scheduler;
        this.delay = delay;
        this.unit = unit;
    }

    public void call(SingleSubscriber<? super T> t) {
        Worker w = this.scheduler.createWorker();
        ObserveOnSingleSubscriber<T> parent = new ObserveOnSingleSubscriber(t, w, this.delay, this.unit);
        t.add(w);
        t.add(parent);
        this.source.call(parent);
    }
}
