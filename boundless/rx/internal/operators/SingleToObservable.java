package rx.internal.operators;

import rx.Observable.OnSubscribe;
import rx.Single;
import rx.Subscriber;

public final class SingleToObservable<T> implements OnSubscribe<T> {
    final Single.OnSubscribe<T> source;

    public SingleToObservable(Single.OnSubscribe<T> source) {
        this.source = source;
    }

    public void call(Subscriber<? super T> t) {
        WrapSubscriberIntoSingle<T> parent = new WrapSubscriberIntoSingle(t);
        t.add(parent);
        this.source.call(parent);
    }
}
