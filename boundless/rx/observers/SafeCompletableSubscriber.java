package rx.observers;

import rx.CompletableSubscriber;
import rx.Subscription;
import rx.annotations.Experimental;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.exceptions.OnCompletedFailedException;
import rx.exceptions.OnErrorFailedException;
import rx.plugins.RxJavaHooks;

@Experimental
public final class SafeCompletableSubscriber implements CompletableSubscriber, Subscription {
    final CompletableSubscriber actual;
    boolean done;
    Subscription s;

    public SafeCompletableSubscriber(CompletableSubscriber actual) {
        this.actual = actual;
    }

    public void onCompleted() {
        if (!this.done) {
            this.done = true;
            try {
                this.actual.onCompleted();
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                OnCompletedFailedException onCompletedFailedException = new OnCompletedFailedException(ex);
            }
        }
    }

    public void onError(Throwable e) {
        RxJavaHooks.onError(e);
        if (!this.done) {
            this.done = true;
            try {
                this.actual.onError(e);
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                OnErrorFailedException onErrorFailedException = new OnErrorFailedException(new CompositeException(e, ex));
            }
        }
    }

    public void onSubscribe(Subscription d) {
        this.s = d;
        try {
            this.actual.onSubscribe(this);
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            d.unsubscribe();
            onError(ex);
        }
    }

    public void unsubscribe() {
        this.s.unsubscribe();
    }

    public boolean isUnsubscribed() {
        return this.done || this.s.isUnsubscribed();
    }
}
