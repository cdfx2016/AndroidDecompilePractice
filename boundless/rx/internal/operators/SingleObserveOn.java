package rx.internal.operators;

import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.functions.Action0;

public final class SingleObserveOn<T> implements OnSubscribe<T> {
    final Scheduler scheduler;
    final OnSubscribe<T> source;

    static final class ObserveOnSingleSubscriber<T> extends SingleSubscriber<T> implements Action0 {
        final SingleSubscriber<? super T> actual;
        Throwable error;
        T value;
        final Worker w;

        public ObserveOnSingleSubscriber(SingleSubscriber<? super T> actual, Worker w) {
            this.actual = actual;
            this.w = w;
        }

        public void onSuccess(T value) {
            this.value = value;
            this.w.schedule(this);
        }

        public void onError(Throwable error) {
            this.error = error;
            this.w.schedule(this);
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

    public SingleObserveOn(OnSubscribe<T> source, Scheduler scheduler) {
        this.source = source;
        this.scheduler = scheduler;
    }

    public void call(SingleSubscriber<? super T> t) {
        Worker w = this.scheduler.createWorker();
        ObserveOnSingleSubscriber<T> parent = new ObserveOnSingleSubscriber(t, w);
        t.add(w);
        t.add(parent);
        this.source.call(parent);
    }
}
