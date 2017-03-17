package rx.internal.operators;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.internal.util.LinkedArrayList;
import rx.subscriptions.SerialSubscription;

public final class CachedObservable<T> extends Observable<T> {
    private final CacheState<T> state;

    static final class CacheState<T> extends LinkedArrayList implements Observer<T> {
        static final ReplayProducer<?>[] EMPTY = new ReplayProducer[0];
        final SerialSubscription connection = new SerialSubscription();
        volatile boolean isConnected;
        volatile ReplayProducer<?>[] producers = EMPTY;
        final Observable<? extends T> source;
        boolean sourceDone;

        public CacheState(Observable<? extends T> source, int capacityHint) {
            super(capacityHint);
            this.source = source;
        }

        public void addProducer(ReplayProducer<T> p) {
            synchronized (this.connection) {
                ReplayProducer<?>[] a = this.producers;
                int n = a.length;
                ReplayProducer<?>[] b = new ReplayProducer[(n + 1)];
                System.arraycopy(a, 0, b, 0, n);
                b[n] = p;
                this.producers = b;
            }
        }

        public void removeProducer(ReplayProducer<T> p) {
            synchronized (this.connection) {
                ReplayProducer<?>[] a = this.producers;
                int n = a.length;
                int j = -1;
                for (int i = 0; i < n; i++) {
                    if (a[i].equals(p)) {
                        j = i;
                        break;
                    }
                }
                if (j < 0) {
                } else if (n == 1) {
                    this.producers = EMPTY;
                } else {
                    ReplayProducer<?>[] b = new ReplayProducer[(n - 1)];
                    System.arraycopy(a, 0, b, 0, j);
                    System.arraycopy(a, j + 1, b, j, (n - j) - 1);
                    this.producers = b;
                }
            }
        }

        public void connect() {
            Subscriber<T> subscriber = new Subscriber<T>() {
                public void onNext(T t) {
                    CacheState.this.onNext(t);
                }

                public void onError(Throwable e) {
                    CacheState.this.onError(e);
                }

                public void onCompleted() {
                    CacheState.this.onCompleted();
                }
            };
            this.connection.set(subscriber);
            this.source.unsafeSubscribe(subscriber);
            this.isConnected = true;
        }

        public void onNext(T t) {
            if (!this.sourceDone) {
                add(NotificationLite.next(t));
                dispatch();
            }
        }

        public void onError(Throwable e) {
            if (!this.sourceDone) {
                this.sourceDone = true;
                add(NotificationLite.error(e));
                this.connection.unsubscribe();
                dispatch();
            }
        }

        public void onCompleted() {
            if (!this.sourceDone) {
                this.sourceDone = true;
                add(NotificationLite.completed());
                this.connection.unsubscribe();
                dispatch();
            }
        }

        void dispatch() {
            for (ReplayProducer<?> rp : this.producers) {
                rp.replay();
            }
        }
    }

    static final class CachedSubscribe<T> extends AtomicBoolean implements OnSubscribe<T> {
        private static final long serialVersionUID = -2817751667698696782L;
        final CacheState<T> state;

        public CachedSubscribe(CacheState<T> state) {
            this.state = state;
        }

        public void call(Subscriber<? super T> t) {
            ReplayProducer<T> rp = new ReplayProducer(t, this.state);
            this.state.addProducer(rp);
            t.add(rp);
            t.setProducer(rp);
            if (!get() && compareAndSet(false, true)) {
                this.state.connect();
            }
        }
    }

    static final class ReplayProducer<T> extends AtomicLong implements Producer, Subscription {
        private static final long serialVersionUID = -2557562030197141021L;
        final Subscriber<? super T> child;
        Object[] currentBuffer;
        int currentIndexInBuffer;
        boolean emitting;
        int index;
        boolean missed;
        final CacheState<T> state;

        public ReplayProducer(Subscriber<? super T> child, CacheState<T> state) {
            this.child = child;
            this.state = state;
        }

        public void request(long n) {
            long r;
            long u;
            do {
                r = get();
                if (r >= 0) {
                    u = r + n;
                    if (u < 0) {
                        u = Long.MAX_VALUE;
                    }
                } else {
                    return;
                }
            } while (!compareAndSet(r, u));
            replay();
        }

        public long produced(long n) {
            return addAndGet(-n);
        }

        public boolean isUnsubscribed() {
            return get() < 0;
        }

        public void unsubscribe() {
            if (get() >= 0 && getAndSet(-1) >= 0) {
                this.state.removeProducer(this);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void replay() {
            /*
            r16 = this;
            monitor-enter(r16);
            r0 = r16;
            r14 = r0.emitting;	 Catch:{ all -> 0x0031 }
            if (r14 == 0) goto L_0x000e;
        L_0x0007:
            r14 = 1;
            r0 = r16;
            r0.missed = r14;	 Catch:{ all -> 0x0031 }
            monitor-exit(r16);	 Catch:{ all -> 0x0031 }
        L_0x000d:
            return;
        L_0x000e:
            r14 = 1;
            r0 = r16;
            r0.emitting = r14;	 Catch:{ all -> 0x0031 }
            monitor-exit(r16);	 Catch:{ all -> 0x0031 }
            r12 = 0;
            r0 = r16;
            r3 = r0.child;	 Catch:{ all -> 0x016a }
        L_0x0019:
            r10 = r16.get();	 Catch:{ all -> 0x016a }
            r14 = 0;
            r14 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1));
            if (r14 >= 0) goto L_0x0034;
        L_0x0023:
            r12 = 1;
            if (r12 != 0) goto L_0x000d;
        L_0x0026:
            monitor-enter(r16);
            r14 = 0;
            r0 = r16;
            r0.emitting = r14;	 Catch:{ all -> 0x002e }
            monitor-exit(r16);	 Catch:{ all -> 0x002e }
            goto L_0x000d;
        L_0x002e:
            r14 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x002e }
            throw r14;
        L_0x0031:
            r14 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x0031 }
            throw r14;
        L_0x0034:
            r0 = r16;
            r14 = r0.state;	 Catch:{ all -> 0x016a }
            r9 = r14.size();	 Catch:{ all -> 0x016a }
            if (r9 == 0) goto L_0x0143;
        L_0x003e:
            r0 = r16;
            r2 = r0.currentBuffer;	 Catch:{ all -> 0x016a }
            if (r2 != 0) goto L_0x0050;
        L_0x0044:
            r0 = r16;
            r14 = r0.state;	 Catch:{ all -> 0x016a }
            r2 = r14.head();	 Catch:{ all -> 0x016a }
            r0 = r16;
            r0.currentBuffer = r2;	 Catch:{ all -> 0x016a }
        L_0x0050:
            r14 = r2.length;	 Catch:{ all -> 0x016a }
            r7 = r14 + -1;
            r0 = r16;
            r5 = r0.index;	 Catch:{ all -> 0x016a }
            r0 = r16;
            r6 = r0.currentIndexInBuffer;	 Catch:{ all -> 0x016a }
            r14 = 0;
            r14 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1));
            if (r14 != 0) goto L_0x009c;
        L_0x0061:
            r8 = r2[r6];	 Catch:{ all -> 0x016a }
            r14 = rx.internal.operators.NotificationLite.isCompleted(r8);	 Catch:{ all -> 0x016a }
            if (r14 == 0) goto L_0x007d;
        L_0x0069:
            r3.onCompleted();	 Catch:{ all -> 0x016a }
            r12 = 1;
            r16.unsubscribe();	 Catch:{ all -> 0x016a }
            if (r12 != 0) goto L_0x000d;
        L_0x0072:
            monitor-enter(r16);
            r14 = 0;
            r0 = r16;
            r0.emitting = r14;	 Catch:{ all -> 0x007a }
            monitor-exit(r16);	 Catch:{ all -> 0x007a }
            goto L_0x000d;
        L_0x007a:
            r14 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x007a }
            throw r14;
        L_0x007d:
            r14 = rx.internal.operators.NotificationLite.isError(r8);	 Catch:{ all -> 0x016a }
            if (r14 == 0) goto L_0x0143;
        L_0x0083:
            r14 = rx.internal.operators.NotificationLite.getError(r8);	 Catch:{ all -> 0x016a }
            r3.onError(r14);	 Catch:{ all -> 0x016a }
            r12 = 1;
            r16.unsubscribe();	 Catch:{ all -> 0x016a }
            if (r12 != 0) goto L_0x000d;
        L_0x0090:
            monitor-enter(r16);
            r14 = 0;
            r0 = r16;
            r0.emitting = r14;	 Catch:{ all -> 0x0099 }
            monitor-exit(r16);	 Catch:{ all -> 0x0099 }
            goto L_0x000d;
        L_0x0099:
            r14 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x0099 }
            throw r14;
        L_0x009c:
            r14 = 0;
            r14 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1));
            if (r14 <= 0) goto L_0x0143;
        L_0x00a2:
            r13 = 0;
        L_0x00a3:
            if (r5 >= r9) goto L_0x011c;
        L_0x00a5:
            r14 = 0;
            r14 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1));
            if (r14 <= 0) goto L_0x011c;
        L_0x00ab:
            r14 = r3.isUnsubscribed();	 Catch:{ all -> 0x016a }
            if (r14 == 0) goto L_0x00c0;
        L_0x00b1:
            r12 = 1;
            if (r12 != 0) goto L_0x000d;
        L_0x00b4:
            monitor-enter(r16);
            r14 = 0;
            r0 = r16;
            r0.emitting = r14;	 Catch:{ all -> 0x00bd }
            monitor-exit(r16);	 Catch:{ all -> 0x00bd }
            goto L_0x000d;
        L_0x00bd:
            r14 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x00bd }
            throw r14;
        L_0x00c0:
            if (r6 != r7) goto L_0x00cb;
        L_0x00c2:
            r14 = r2[r7];	 Catch:{ all -> 0x016a }
            r14 = (java.lang.Object[]) r14;	 Catch:{ all -> 0x016a }
            r0 = r14;
            r0 = (java.lang.Object[]) r0;	 Catch:{ all -> 0x016a }
            r2 = r0;
            r6 = 0;
        L_0x00cb:
            r8 = r2[r6];	 Catch:{ all -> 0x016a }
            r14 = rx.internal.operators.NotificationLite.accept(r3, r8);	 Catch:{ Throwable -> 0x00e5 }
            if (r14 == 0) goto L_0x0112;
        L_0x00d3:
            r12 = 1;
            r16.unsubscribe();	 Catch:{ Throwable -> 0x00e5 }
            if (r12 != 0) goto L_0x000d;
        L_0x00d9:
            monitor-enter(r16);
            r14 = 0;
            r0 = r16;
            r0.emitting = r14;	 Catch:{ all -> 0x00e2 }
            monitor-exit(r16);	 Catch:{ all -> 0x00e2 }
            goto L_0x000d;
        L_0x00e2:
            r14 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x00e2 }
            throw r14;
        L_0x00e5:
            r4 = move-exception;
            rx.exceptions.Exceptions.throwIfFatal(r4);	 Catch:{ all -> 0x016a }
            r12 = 1;
            r16.unsubscribe();	 Catch:{ all -> 0x016a }
            r14 = rx.internal.operators.NotificationLite.isError(r8);	 Catch:{ all -> 0x016a }
            if (r14 != 0) goto L_0x0104;
        L_0x00f3:
            r14 = rx.internal.operators.NotificationLite.isCompleted(r8);	 Catch:{ all -> 0x016a }
            if (r14 != 0) goto L_0x0104;
        L_0x00f9:
            r14 = rx.internal.operators.NotificationLite.getValue(r8);	 Catch:{ all -> 0x016a }
            r14 = rx.exceptions.OnErrorThrowable.addValueAsLastCause(r4, r14);	 Catch:{ all -> 0x016a }
            r3.onError(r14);	 Catch:{ all -> 0x016a }
        L_0x0104:
            if (r12 != 0) goto L_0x000d;
        L_0x0106:
            monitor-enter(r16);
            r14 = 0;
            r0 = r16;
            r0.emitting = r14;	 Catch:{ all -> 0x010f }
            monitor-exit(r16);	 Catch:{ all -> 0x010f }
            goto L_0x000d;
        L_0x010f:
            r14 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x010f }
            throw r14;
        L_0x0112:
            r6 = r6 + 1;
            r5 = r5 + 1;
            r14 = 1;
            r10 = r10 - r14;
            r13 = r13 + 1;
            goto L_0x00a3;
        L_0x011c:
            r14 = r3.isUnsubscribed();	 Catch:{ all -> 0x016a }
            if (r14 == 0) goto L_0x0131;
        L_0x0122:
            r12 = 1;
            if (r12 != 0) goto L_0x000d;
        L_0x0125:
            monitor-enter(r16);
            r14 = 0;
            r0 = r16;
            r0.emitting = r14;	 Catch:{ all -> 0x012e }
            monitor-exit(r16);	 Catch:{ all -> 0x012e }
            goto L_0x000d;
        L_0x012e:
            r14 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x012e }
            throw r14;
        L_0x0131:
            r0 = r16;
            r0.index = r5;	 Catch:{ all -> 0x016a }
            r0 = r16;
            r0.currentIndexInBuffer = r6;	 Catch:{ all -> 0x016a }
            r0 = r16;
            r0.currentBuffer = r2;	 Catch:{ all -> 0x016a }
            r14 = (long) r13;	 Catch:{ all -> 0x016a }
            r0 = r16;
            r0.produced(r14);	 Catch:{ all -> 0x016a }
        L_0x0143:
            monitor-enter(r16);	 Catch:{ all -> 0x016a }
            r0 = r16;
            r14 = r0.missed;	 Catch:{ all -> 0x0167 }
            if (r14 != 0) goto L_0x015f;
        L_0x014a:
            r14 = 0;
            r0 = r16;
            r0.emitting = r14;	 Catch:{ all -> 0x0167 }
            r12 = 1;
            monitor-exit(r16);	 Catch:{ all -> 0x0167 }
            if (r12 != 0) goto L_0x000d;
        L_0x0153:
            monitor-enter(r16);
            r14 = 0;
            r0 = r16;
            r0.emitting = r14;	 Catch:{ all -> 0x015c }
            monitor-exit(r16);	 Catch:{ all -> 0x015c }
            goto L_0x000d;
        L_0x015c:
            r14 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x015c }
            throw r14;
        L_0x015f:
            r14 = 0;
            r0 = r16;
            r0.missed = r14;	 Catch:{ all -> 0x0167 }
            monitor-exit(r16);	 Catch:{ all -> 0x0167 }
            goto L_0x0019;
        L_0x0167:
            r14 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x0167 }
            throw r14;	 Catch:{ all -> 0x016a }
        L_0x016a:
            r14 = move-exception;
            if (r12 != 0) goto L_0x0174;
        L_0x016d:
            monitor-enter(r16);
            r15 = 0;
            r0 = r16;
            r0.emitting = r15;	 Catch:{ all -> 0x0175 }
            monitor-exit(r16);	 Catch:{ all -> 0x0175 }
        L_0x0174:
            throw r14;
        L_0x0175:
            r14 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x0175 }
            throw r14;
            */
            throw new UnsupportedOperationException("Method not decompiled: rx.internal.operators.CachedObservable.ReplayProducer.replay():void");
        }
    }

    public static <T> CachedObservable<T> from(Observable<? extends T> source) {
        return from(source, 16);
    }

    public static <T> CachedObservable<T> from(Observable<? extends T> source, int capacityHint) {
        if (capacityHint < 1) {
            throw new IllegalArgumentException("capacityHint > 0 required");
        }
        CacheState<T> state = new CacheState(source, capacityHint);
        return new CachedObservable(new CachedSubscribe(state), state);
    }

    private CachedObservable(OnSubscribe<T> onSubscribe, CacheState<T> state) {
        super(onSubscribe);
        this.state = state;
    }

    boolean isConnected() {
        return this.state.isConnected;
    }

    boolean hasObservers() {
        return this.state.producers.length != 0;
    }
}
