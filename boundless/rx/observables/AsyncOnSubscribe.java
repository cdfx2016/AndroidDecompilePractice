package rx.observables;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.annotations.Experimental;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Action3;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.internal.operators.BufferUntilSubscriber;
import rx.observers.SerializedObserver;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.CompositeSubscription;

@Experimental
public abstract class AsyncOnSubscribe<S, T> implements OnSubscribe<T> {

    static final class AsyncOnSubscribeImpl<S, T> extends AsyncOnSubscribe<S, T> {
        private final Func0<? extends S> generator;
        private final Func3<? super S, Long, ? super Observer<Observable<? extends T>>, ? extends S> next;
        private final Action1<? super S> onUnsubscribe;

        public /* bridge */ /* synthetic */ void call(Object x0) {
            super.call((Subscriber) x0);
        }

        AsyncOnSubscribeImpl(Func0<? extends S> generator, Func3<? super S, Long, ? super Observer<Observable<? extends T>>, ? extends S> next, Action1<? super S> onUnsubscribe) {
            this.generator = generator;
            this.next = next;
            this.onUnsubscribe = onUnsubscribe;
        }

        public AsyncOnSubscribeImpl(Func0<? extends S> generator, Func3<? super S, Long, ? super Observer<Observable<? extends T>>, ? extends S> next) {
            this(generator, next, null);
        }

        public AsyncOnSubscribeImpl(Func3<S, Long, Observer<Observable<? extends T>>, S> next, Action1<? super S> onUnsubscribe) {
            this(null, next, onUnsubscribe);
        }

        public AsyncOnSubscribeImpl(Func3<S, Long, Observer<Observable<? extends T>>, S> nextFunc) {
            this(null, nextFunc, null);
        }

        protected S generateState() {
            return this.generator == null ? null : this.generator.call();
        }

        protected S next(S state, long requested, Observer<Observable<? extends T>> observer) {
            return this.next.call(state, Long.valueOf(requested), observer);
        }

        protected void onUnsubscribe(S state) {
            if (this.onUnsubscribe != null) {
                this.onUnsubscribe.call(state);
            }
        }
    }

    static final class AsyncOuterManager<S, T> implements Producer, Subscription, Observer<Observable<? extends T>> {
        Producer concatProducer;
        boolean emitting;
        long expectedDelivery;
        private boolean hasTerminated;
        final AtomicBoolean isUnsubscribed;
        private final UnicastSubject<Observable<T>> merger;
        private boolean onNextCalled;
        private final AsyncOnSubscribe<S, T> parent;
        List<Long> requests;
        private final SerializedObserver<Observable<? extends T>> serializedSubscriber;
        private S state;
        final CompositeSubscription subscriptions = new CompositeSubscription();

        public AsyncOuterManager(AsyncOnSubscribe<S, T> parent, S initialState, UnicastSubject<Observable<T>> merger) {
            this.parent = parent;
            this.serializedSubscriber = new SerializedObserver(this);
            this.state = initialState;
            this.merger = merger;
            this.isUnsubscribed = new AtomicBoolean();
        }

        public void unsubscribe() {
            if (this.isUnsubscribed.compareAndSet(false, true)) {
                synchronized (this) {
                    if (this.emitting) {
                        this.requests = new ArrayList();
                        this.requests.add(Long.valueOf(0));
                        return;
                    }
                    this.emitting = true;
                    cleanup();
                }
            }
        }

        void setConcatProducer(Producer p) {
            if (this.concatProducer != null) {
                throw new IllegalStateException("setConcatProducer may be called at most once!");
            }
            this.concatProducer = p;
        }

        public boolean isUnsubscribed() {
            return this.isUnsubscribed.get();
        }

        public void nextIteration(long requestCount) {
            this.state = this.parent.next(this.state, requestCount, this.serializedSubscriber);
        }

        void cleanup() {
            this.subscriptions.unsubscribe();
            try {
                this.parent.onUnsubscribe(this.state);
            } catch (Throwable ex) {
                handleThrownError(ex);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void request(long r10) {
            /*
            r9 = this;
            r6 = 0;
            r3 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1));
            if (r3 != 0) goto L_0x0007;
        L_0x0006:
            return;
        L_0x0007:
            r3 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1));
            if (r3 >= 0) goto L_0x0024;
        L_0x000b:
            r3 = new java.lang.IllegalStateException;
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r7 = "Request can't be negative! ";
            r6 = r6.append(r7);
            r6 = r6.append(r10);
            r6 = r6.toString();
            r3.<init>(r6);
            throw r3;
        L_0x0024:
            r2 = 0;
            monitor-enter(r9);
            r3 = r9.emitting;	 Catch:{ all -> 0x005c }
            if (r3 == 0) goto L_0x0058;
        L_0x002a:
            r1 = r9.requests;	 Catch:{ all -> 0x005c }
            if (r1 != 0) goto L_0x0035;
        L_0x002e:
            r1 = new java.util.ArrayList;	 Catch:{ all -> 0x005c }
            r1.<init>();	 Catch:{ all -> 0x005c }
            r9.requests = r1;	 Catch:{ all -> 0x005c }
        L_0x0035:
            r3 = java.lang.Long.valueOf(r10);	 Catch:{ all -> 0x005c }
            r1.add(r3);	 Catch:{ all -> 0x005c }
            r2 = 1;
        L_0x003d:
            monitor-exit(r9);	 Catch:{ all -> 0x005c }
            r3 = r9.concatProducer;
            r3.request(r10);
            if (r2 != 0) goto L_0x0006;
        L_0x0045:
            r3 = r9.tryEmit(r10);
            if (r3 != 0) goto L_0x0006;
        L_0x004b:
            monitor-enter(r9);
            r1 = r9.requests;	 Catch:{ all -> 0x0055 }
            if (r1 != 0) goto L_0x005f;
        L_0x0050:
            r3 = 0;
            r9.emitting = r3;	 Catch:{ all -> 0x0055 }
            monitor-exit(r9);	 Catch:{ all -> 0x0055 }
            goto L_0x0006;
        L_0x0055:
            r3 = move-exception;
            monitor-exit(r9);	 Catch:{ all -> 0x0055 }
            throw r3;
        L_0x0058:
            r3 = 1;
            r9.emitting = r3;	 Catch:{ all -> 0x005c }
            goto L_0x003d;
        L_0x005c:
            r3 = move-exception;
            monitor-exit(r9);	 Catch:{ all -> 0x005c }
            throw r3;
        L_0x005f:
            r3 = 0;
            r9.requests = r3;	 Catch:{ all -> 0x0055 }
            monitor-exit(r9);	 Catch:{ all -> 0x0055 }
            r0 = r1.iterator();
        L_0x0067:
            r3 = r0.hasNext();
            if (r3 == 0) goto L_0x004b;
        L_0x006d:
            r3 = r0.next();
            r3 = (java.lang.Long) r3;
            r4 = r3.longValue();
            r3 = r9.tryEmit(r4);
            if (r3 == 0) goto L_0x0067;
        L_0x007d:
            goto L_0x0006;
            */
            throw new UnsupportedOperationException("Method not decompiled: rx.observables.AsyncOnSubscribe.AsyncOuterManager.request(long):void");
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void requestRemaining(long r10) {
            /*
            r9 = this;
            r6 = 0;
            r4 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1));
            if (r4 != 0) goto L_0x0007;
        L_0x0006:
            return;
        L_0x0007:
            r4 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1));
            if (r4 >= 0) goto L_0x0024;
        L_0x000b:
            r4 = new java.lang.IllegalStateException;
            r5 = new java.lang.StringBuilder;
            r5.<init>();
            r6 = "Request can't be negative! ";
            r5 = r5.append(r6);
            r5 = r5.append(r10);
            r5 = r5.toString();
            r4.<init>(r5);
            throw r4;
        L_0x0024:
            monitor-enter(r9);
            r4 = r9.emitting;	 Catch:{ all -> 0x003d }
            if (r4 == 0) goto L_0x0040;
        L_0x0029:
            r1 = r9.requests;	 Catch:{ all -> 0x003d }
            if (r1 != 0) goto L_0x0034;
        L_0x002d:
            r1 = new java.util.ArrayList;	 Catch:{ all -> 0x003d }
            r1.<init>();	 Catch:{ all -> 0x003d }
            r9.requests = r1;	 Catch:{ all -> 0x003d }
        L_0x0034:
            r4 = java.lang.Long.valueOf(r10);	 Catch:{ all -> 0x003d }
            r1.add(r4);	 Catch:{ all -> 0x003d }
            monitor-exit(r9);	 Catch:{ all -> 0x003d }
            goto L_0x0006;
        L_0x003d:
            r4 = move-exception;
            monitor-exit(r9);	 Catch:{ all -> 0x003d }
            throw r4;
        L_0x0040:
            r4 = 1;
            r9.emitting = r4;	 Catch:{ all -> 0x003d }
            monitor-exit(r9);	 Catch:{ all -> 0x003d }
            r4 = r9.tryEmit(r10);
            if (r4 != 0) goto L_0x0006;
        L_0x004a:
            monitor-enter(r9);
            r1 = r9.requests;	 Catch:{ all -> 0x0054 }
            if (r1 != 0) goto L_0x0057;
        L_0x004f:
            r4 = 0;
            r9.emitting = r4;	 Catch:{ all -> 0x0054 }
            monitor-exit(r9);	 Catch:{ all -> 0x0054 }
            goto L_0x0006;
        L_0x0054:
            r4 = move-exception;
            monitor-exit(r9);	 Catch:{ all -> 0x0054 }
            throw r4;
        L_0x0057:
            r4 = 0;
            r9.requests = r4;	 Catch:{ all -> 0x0054 }
            monitor-exit(r9);	 Catch:{ all -> 0x0054 }
            r0 = r1.iterator();
        L_0x005f:
            r4 = r0.hasNext();
            if (r4 == 0) goto L_0x004a;
        L_0x0065:
            r4 = r0.next();
            r4 = (java.lang.Long) r4;
            r2 = r4.longValue();
            r4 = r9.tryEmit(r2);
            if (r4 == 0) goto L_0x005f;
        L_0x0075:
            goto L_0x0006;
            */
            throw new UnsupportedOperationException("Method not decompiled: rx.observables.AsyncOnSubscribe.AsyncOuterManager.requestRemaining(long):void");
        }

        boolean tryEmit(long n) {
            if (isUnsubscribed()) {
                cleanup();
                return true;
            }
            try {
                this.onNextCalled = false;
                this.expectedDelivery = n;
                nextIteration(n);
                if (this.hasTerminated || isUnsubscribed()) {
                    cleanup();
                    return true;
                } else if (this.onNextCalled) {
                    return false;
                } else {
                    handleThrownError(new IllegalStateException("No events emitted!"));
                    return true;
                }
            } catch (Throwable ex) {
                handleThrownError(ex);
                return true;
            }
        }

        private void handleThrownError(Throwable ex) {
            if (this.hasTerminated) {
                RxJavaHooks.onError(ex);
                return;
            }
            this.hasTerminated = true;
            this.merger.onError(ex);
            cleanup();
        }

        public void onCompleted() {
            if (this.hasTerminated) {
                throw new IllegalStateException("Terminal event already emitted.");
            }
            this.hasTerminated = true;
            this.merger.onCompleted();
        }

        public void onError(Throwable e) {
            if (this.hasTerminated) {
                throw new IllegalStateException("Terminal event already emitted.");
            }
            this.hasTerminated = true;
            this.merger.onError(e);
        }

        public void onNext(Observable<? extends T> t) {
            if (this.onNextCalled) {
                throw new IllegalStateException("onNext called multiple times!");
            }
            this.onNextCalled = true;
            if (!this.hasTerminated) {
                subscribeBufferToObservable(t);
            }
        }

        private void subscribeBufferToObservable(Observable<? extends T> t) {
            final BufferUntilSubscriber<T> buffer = BufferUntilSubscriber.create();
            final long expected = this.expectedDelivery;
            final Subscriber s = new Subscriber<T>() {
                long remaining = expected;

                public void onNext(T t) {
                    this.remaining--;
                    buffer.onNext(t);
                }

                public void onError(Throwable e) {
                    buffer.onError(e);
                }

                public void onCompleted() {
                    buffer.onCompleted();
                    long r = this.remaining;
                    if (r > 0) {
                        AsyncOuterManager.this.requestRemaining(r);
                    }
                }
            };
            this.subscriptions.add(s);
            t.doOnTerminate(new Action0() {
                public void call() {
                    AsyncOuterManager.this.subscriptions.remove(s);
                }
            }).subscribe(s);
            this.merger.onNext(buffer);
        }
    }

    static final class UnicastSubject<T> extends Observable<T> implements Observer<T> {
        private final State<T> state;

        static final class State<T> implements OnSubscribe<T> {
            Subscriber<? super T> subscriber;

            State() {
            }

            public void call(Subscriber<? super T> s) {
                synchronized (this) {
                    if (this.subscriber == null) {
                        this.subscriber = s;
                        return;
                    }
                    s.onError(new IllegalStateException("There can be only one subscriber"));
                }
            }
        }

        public static <T> UnicastSubject<T> create() {
            return new UnicastSubject(new State());
        }

        protected UnicastSubject(State<T> state) {
            super(state);
            this.state = state;
        }

        public void onCompleted() {
            this.state.subscriber.onCompleted();
        }

        public void onError(Throwable e) {
            this.state.subscriber.onError(e);
        }

        public void onNext(T t) {
            this.state.subscriber.onNext(t);
        }
    }

    protected abstract S generateState();

    protected abstract S next(S s, long j, Observer<Observable<? extends T>> observer);

    protected void onUnsubscribe(S s) {
    }

    @Experimental
    public static <S, T> AsyncOnSubscribe<S, T> createSingleState(Func0<? extends S> generator, final Action3<? super S, Long, ? super Observer<Observable<? extends T>>> next) {
        return new AsyncOnSubscribeImpl((Func0) generator, new Func3<S, Long, Observer<Observable<? extends T>>, S>() {
            public S call(S state, Long requested, Observer<Observable<? extends T>> subscriber) {
                next.call(state, requested, subscriber);
                return state;
            }
        });
    }

    @Experimental
    public static <S, T> AsyncOnSubscribe<S, T> createSingleState(Func0<? extends S> generator, final Action3<? super S, Long, ? super Observer<Observable<? extends T>>> next, Action1<? super S> onUnsubscribe) {
        return new AsyncOnSubscribeImpl(generator, new Func3<S, Long, Observer<Observable<? extends T>>, S>() {
            public S call(S state, Long requested, Observer<Observable<? extends T>> subscriber) {
                next.call(state, requested, subscriber);
                return state;
            }
        }, onUnsubscribe);
    }

    @Experimental
    public static <S, T> AsyncOnSubscribe<S, T> createStateful(Func0<? extends S> generator, Func3<? super S, Long, ? super Observer<Observable<? extends T>>, ? extends S> next, Action1<? super S> onUnsubscribe) {
        return new AsyncOnSubscribeImpl(generator, next, onUnsubscribe);
    }

    @Experimental
    public static <S, T> AsyncOnSubscribe<S, T> createStateful(Func0<? extends S> generator, Func3<? super S, Long, ? super Observer<Observable<? extends T>>, ? extends S> next) {
        return new AsyncOnSubscribeImpl((Func0) generator, (Func3) next);
    }

    @Experimental
    public static <T> AsyncOnSubscribe<Void, T> createStateless(final Action2<Long, ? super Observer<Observable<? extends T>>> next) {
        return new AsyncOnSubscribeImpl(new Func3<Void, Long, Observer<Observable<? extends T>>, Void>() {
            public Void call(Void state, Long requested, Observer<Observable<? extends T>> subscriber) {
                next.call(requested, subscriber);
                return state;
            }
        });
    }

    @Experimental
    public static <T> AsyncOnSubscribe<Void, T> createStateless(final Action2<Long, ? super Observer<Observable<? extends T>>> next, final Action0 onUnsubscribe) {
        return new AsyncOnSubscribeImpl(new Func3<Void, Long, Observer<Observable<? extends T>>, Void>() {
            public Void call(Void state, Long requested, Observer<Observable<? extends T>> subscriber) {
                next.call(requested, subscriber);
                return null;
            }
        }, new Action1<Void>() {
            public void call(Void t) {
                onUnsubscribe.call();
            }
        });
    }

    public final void call(final Subscriber<? super T> actualSubscriber) {
        try {
            S state = generateState();
            UnicastSubject<Observable<T>> subject = UnicastSubject.create();
            final AsyncOuterManager<S, T> outerProducer = new AsyncOuterManager(this, state, subject);
            Subscriber<T> concatSubscriber = new Subscriber<T>() {
                public void onNext(T t) {
                    actualSubscriber.onNext(t);
                }

                public void onError(Throwable e) {
                    actualSubscriber.onError(e);
                }

                public void onCompleted() {
                    actualSubscriber.onCompleted();
                }

                public void setProducer(Producer p) {
                    outerProducer.setConcatProducer(p);
                }
            };
            subject.onBackpressureBuffer().concatMap(new Func1<Observable<T>, Observable<T>>() {
                public Observable<T> call(Observable<T> v) {
                    return v.onBackpressureBuffer();
                }
            }).unsafeSubscribe(concatSubscriber);
            actualSubscriber.add(concatSubscriber);
            actualSubscriber.add(outerProducer);
            actualSubscriber.setProducer(outerProducer);
        } catch (Throwable ex) {
            actualSubscriber.onError(ex);
        }
    }
}
