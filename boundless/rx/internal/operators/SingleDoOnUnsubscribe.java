package rx.internal.operators;

import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public final class SingleDoOnUnsubscribe<T> implements OnSubscribe<T> {
    final Action0 onUnsubscribe;
    final OnSubscribe<T> source;

    public SingleDoOnUnsubscribe(OnSubscribe<T> source, Action0 onUnsubscribe) {
        this.source = source;
        this.onUnsubscribe = onUnsubscribe;
    }

    public void call(SingleSubscriber<? super T> t) {
        t.add(Subscriptions.create(this.onUnsubscribe));
        this.source.call(t);
    }
}
