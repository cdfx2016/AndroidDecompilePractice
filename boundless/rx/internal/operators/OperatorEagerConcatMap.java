package rx.internal.operators;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable;
import rx.Observable.Operator;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.internal.util.atomic.SpscAtomicArrayQueue;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;
import rx.subscriptions.Subscriptions;

public final class OperatorEagerConcatMap<T, R> implements Operator<R, T> {
    final int bufferSize;
    final Func1<? super T, ? extends Observable<? extends R>> mapper;
    private final int maxConcurrent;

    static final class EagerInnerSubscriber<T> extends Subscriber<T> {
        volatile boolean done;
        Throwable error;
        final EagerOuterSubscriber<?, T> parent;
        final Queue<Object> queue;

        public EagerInnerSubscriber(EagerOuterSubscriber<?, T> parent, int bufferSize) {
            Queue<Object> q;
            this.parent = parent;
            if (UnsafeAccess.isUnsafeAvailable()) {
                q = new SpscArrayQueue(bufferSize);
            } else {
                q = new SpscAtomicArrayQueue(bufferSize);
            }
            this.queue = q;
            request((long) bufferSize);
        }

        public void onNext(T t) {
            this.queue.offer(NotificationLite.next(t));
            this.parent.drain();
        }

        public void onError(Throwable e) {
            this.error = e;
            this.done = true;
            this.parent.drain();
        }

        public void onCompleted() {
            this.done = true;
            this.parent.drain();
        }

        void requestMore(long n) {
            request(n);
        }
    }

    static final class EagerOuterProducer extends AtomicLong implements Producer {
        private static final long serialVersionUID = -657299606803478389L;
        final EagerOuterSubscriber<?, ?> parent;

        public EagerOuterProducer(EagerOuterSubscriber<?, ?> parent) {
            this.parent = parent;
        }

        public void request(long n) {
            if (n < 0) {
                throw new IllegalStateException("n >= 0 required but it was " + n);
            } else if (n > 0) {
                BackpressureUtils.getAndAddRequest(this, n);
                this.parent.drain();
            }
        }
    }

    static final class EagerOuterSubscriber<T, R> extends Subscriber<T> {
        final Subscriber<? super R> actual;
        final int bufferSize;
        volatile boolean cancelled;
        volatile boolean done;
        Throwable error;
        final Func1<? super T, ? extends Observable<? extends R>> mapper;
        private EagerOuterProducer sharedProducer;
        final Queue<EagerInnerSubscriber<R>> subscribers = new LinkedList();
        final AtomicInteger wip = new AtomicInteger();

        public EagerOuterSubscriber(Func1<? super T, ? extends Observable<? extends R>> mapper, int bufferSize, int maxConcurrent, Subscriber<? super R> actual) {
            this.mapper = mapper;
            this.bufferSize = bufferSize;
            this.actual = actual;
            request(maxConcurrent == ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED ? Long.MAX_VALUE : (long) maxConcurrent);
        }

        void init() {
            this.sharedProducer = new EagerOuterProducer(this);
            add(Subscriptions.create(new Action0() {
                public void call() {
                    EagerOuterSubscriber.this.cancelled = true;
                    if (EagerOuterSubscriber.this.wip.getAndIncrement() == 0) {
                        EagerOuterSubscriber.this.cleanup();
                    }
                }
            }));
            this.actual.add(this);
            this.actual.setProducer(this.sharedProducer);
        }

        void cleanup() {
            synchronized (this.subscribers) {
                List<Subscription> list = new ArrayList(this.subscribers);
                this.subscribers.clear();
            }
            for (Subscription s : list) {
                s.unsubscribe();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onNext(T r6) {
            /*
            r5 = this;
            r3 = r5.mapper;	 Catch:{ Throwable -> 0x000d }
            r2 = r3.call(r6);	 Catch:{ Throwable -> 0x000d }
            r2 = (rx.Observable) r2;	 Catch:{ Throwable -> 0x000d }
            r3 = r5.cancelled;
            if (r3 == 0) goto L_0x0014;
        L_0x000c:
            return;
        L_0x000d:
            r0 = move-exception;
            r3 = r5.actual;
            rx.exceptions.Exceptions.throwOrReport(r0, r3, r6);
            goto L_0x000c;
        L_0x0014:
            r1 = new rx.internal.operators.OperatorEagerConcatMap$EagerInnerSubscriber;
            r3 = r5.bufferSize;
            r1.<init>(r5, r3);
            r4 = r5.subscribers;
            monitor-enter(r4);
            r3 = r5.cancelled;	 Catch:{ all -> 0x0024 }
            if (r3 == 0) goto L_0x0027;
        L_0x0022:
            monitor-exit(r4);	 Catch:{ all -> 0x0024 }
            goto L_0x000c;
        L_0x0024:
            r3 = move-exception;
            monitor-exit(r4);	 Catch:{ all -> 0x0024 }
            throw r3;
        L_0x0027:
            r3 = r5.subscribers;	 Catch:{ all -> 0x0024 }
            r3.add(r1);	 Catch:{ all -> 0x0024 }
            monitor-exit(r4);	 Catch:{ all -> 0x0024 }
            r3 = r5.cancelled;
            if (r3 != 0) goto L_0x000c;
        L_0x0031:
            r2.unsafeSubscribe(r1);
            r5.drain();
            goto L_0x000c;
            */
            throw new UnsupportedOperationException("Method not decompiled: rx.internal.operators.OperatorEagerConcatMap.EagerOuterSubscriber.onNext(java.lang.Object):void");
        }

        public void onError(Throwable e) {
            this.error = e;
            this.done = true;
            drain();
        }

        public void onCompleted() {
            this.done = true;
            drain();
        }

        void drain() {
            if (this.wip.getAndIncrement() == 0) {
                int missed = 1;
                AtomicLong requested = this.sharedProducer;
                Observer actualSubscriber = this.actual;
                while (!this.cancelled) {
                    EagerInnerSubscriber<R> innerSubscriber;
                    boolean outerDone = this.done;
                    synchronized (this.subscribers) {
                        innerSubscriber = (EagerInnerSubscriber) this.subscribers.peek();
                    }
                    boolean empty = innerSubscriber == null;
                    if (outerDone) {
                        Throwable error = this.error;
                        if (error != null) {
                            cleanup();
                            actualSubscriber.onError(error);
                            return;
                        } else if (empty) {
                            actualSubscriber.onCompleted();
                            return;
                        }
                    }
                    if (!empty) {
                        long requestedAmount = requested.get();
                        long emittedAmount = 0;
                        Queue<Object> innerQueue = innerSubscriber.queue;
                        boolean innerDone = false;
                        while (true) {
                            outerDone = innerSubscriber.done;
                            Object v = innerQueue.peek();
                            empty = v == null;
                            if (outerDone) {
                                Throwable innerError = innerSubscriber.error;
                                if (innerError == null) {
                                    if (empty) {
                                        break;
                                    }
                                }
                                cleanup();
                                actualSubscriber.onError(innerError);
                                return;
                            }
                            if (empty || requestedAmount == emittedAmount) {
                                break;
                            }
                            innerQueue.poll();
                            try {
                                actualSubscriber.onNext(NotificationLite.getValue(v));
                                emittedAmount++;
                            } catch (Throwable ex) {
                                Exceptions.throwOrReport(ex, actualSubscriber, v);
                                return;
                            }
                        }
                        synchronized (this.subscribers) {
                            this.subscribers.poll();
                        }
                        innerSubscriber.unsubscribe();
                        innerDone = true;
                        request(1);
                        if (emittedAmount != 0) {
                            if (requestedAmount != Long.MAX_VALUE) {
                                BackpressureUtils.produced(requested, emittedAmount);
                            }
                            if (!innerDone) {
                                innerSubscriber.requestMore(emittedAmount);
                            }
                        }
                        if (innerDone) {
                            continue;
                        }
                    }
                    missed = this.wip.addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
                cleanup();
            }
        }
    }

    public OperatorEagerConcatMap(Func1<? super T, ? extends Observable<? extends R>> mapper, int bufferSize, int maxConcurrent) {
        this.mapper = mapper;
        this.bufferSize = bufferSize;
        this.maxConcurrent = maxConcurrent;
    }

    public Subscriber<? super T> call(Subscriber<? super R> t) {
        EagerOuterSubscriber<T, R> outer = new EagerOuterSubscriber(this.mapper, this.bufferSize, this.maxConcurrent, t);
        outer.init();
        return outer;
    }
}
