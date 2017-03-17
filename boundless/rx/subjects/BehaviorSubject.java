package rx.subjects;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import rx.Observable.OnSubscribe;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.internal.operators.NotificationLite;

public final class BehaviorSubject<T> extends Subject<T, T> {
    private static final Object[] EMPTY_ARRAY = new Object[0];
    private final SubjectSubscriptionManager<T> state;

    public static <T> BehaviorSubject<T> create() {
        return create(null, false);
    }

    public static <T> BehaviorSubject<T> create(T defaultValue) {
        return create(defaultValue, true);
    }

    private static <T> BehaviorSubject<T> create(T defaultValue, boolean hasDefault) {
        final SubjectSubscriptionManager<T> state = new SubjectSubscriptionManager();
        if (hasDefault) {
            state.setLatest(NotificationLite.next(defaultValue));
        }
        state.onAdded = new Action1<SubjectObserver<T>>() {
            public void call(SubjectObserver<T> o) {
                o.emitFirst(state.getLatest());
            }
        };
        state.onTerminated = state.onAdded;
        return new BehaviorSubject(state, state);
    }

    protected BehaviorSubject(OnSubscribe<T> onSubscribe, SubjectSubscriptionManager<T> state) {
        super(onSubscribe);
        this.state = state;
    }

    public void onCompleted() {
        if (this.state.getLatest() == null || this.state.active) {
            Object n = NotificationLite.completed();
            for (SubjectObserver<T> bo : this.state.terminate(n)) {
                bo.emitNext(n);
            }
        }
    }

    public void onError(Throwable e) {
        if (this.state.getLatest() == null || this.state.active) {
            Object n = NotificationLite.error(e);
            List<Throwable> errors = null;
            for (SubjectObserver<T> bo : this.state.terminate(n)) {
                try {
                    bo.emitNext(n);
                } catch (Throwable e2) {
                    if (errors == null) {
                        errors = new ArrayList();
                    }
                    errors.add(e2);
                }
            }
            Exceptions.throwIfAny(errors);
        }
    }

    public void onNext(T v) {
        if (this.state.getLatest() == null || this.state.active) {
            Object n = NotificationLite.next(v);
            for (SubjectObserver<T> bo : this.state.next(n)) {
                bo.emitNext(n);
            }
        }
    }

    int subscriberCount() {
        return this.state.observers().length;
    }

    public boolean hasObservers() {
        return this.state.observers().length > 0;
    }

    public boolean hasValue() {
        return NotificationLite.isNext(this.state.getLatest());
    }

    public boolean hasThrowable() {
        return NotificationLite.isError(this.state.getLatest());
    }

    public boolean hasCompleted() {
        return NotificationLite.isCompleted(this.state.getLatest());
    }

    public T getValue() {
        Object o = this.state.getLatest();
        if (NotificationLite.isNext(o)) {
            return NotificationLite.getValue(o);
        }
        return null;
    }

    public Throwable getThrowable() {
        Object o = this.state.getLatest();
        if (NotificationLite.isError(o)) {
            return NotificationLite.getError(o);
        }
        return null;
    }

    public T[] getValues(T[] a) {
        Object o = this.state.getLatest();
        if (NotificationLite.isNext(o)) {
            if (a.length == 0) {
                a = (Object[]) ((Object[]) Array.newInstance(a.getClass().getComponentType(), 1));
            }
            a[0] = NotificationLite.getValue(o);
            if (a.length > 1) {
                a[1] = null;
            }
        } else if (a.length > 0) {
            a[0] = null;
        }
        return a;
    }

    public Object[] getValues() {
        T[] r = getValues(EMPTY_ARRAY);
        if (r == EMPTY_ARRAY) {
            return new Object[0];
        }
        return r;
    }
}
