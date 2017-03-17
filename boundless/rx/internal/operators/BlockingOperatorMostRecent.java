package rx.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.Exceptions;

public final class BlockingOperatorMostRecent {

    static final class MostRecentObserver<T> extends Subscriber<T> {
        volatile Object value;

        MostRecentObserver(T value) {
            this.value = NotificationLite.next(value);
        }

        public void onCompleted() {
            this.value = NotificationLite.completed();
        }

        public void onError(Throwable e) {
            this.value = NotificationLite.error(e);
        }

        public void onNext(T args) {
            this.value = NotificationLite.next(args);
        }

        public Iterator<T> getIterable() {
            return new Iterator<T>() {
                private Object buf;

                public boolean hasNext() {
                    this.buf = MostRecentObserver.this.value;
                    return !NotificationLite.isCompleted(this.buf);
                }

                public T next() {
                    try {
                        if (this.buf == null) {
                            Object obj = MostRecentObserver.this.value;
                        }
                        if (NotificationLite.isCompleted(this.buf)) {
                            throw new NoSuchElementException();
                        } else if (NotificationLite.isError(this.buf)) {
                            throw Exceptions.propagate(NotificationLite.getError(this.buf));
                        } else {
                            T value = NotificationLite.getValue(this.buf);
                            this.buf = null;
                            return value;
                        }
                    } finally {
                        this.buf = null;
                    }
                }

                public void remove() {
                    throw new UnsupportedOperationException("Read only iterator");
                }
            };
        }
    }

    private BlockingOperatorMostRecent() {
        throw new IllegalStateException("No instances!");
    }

    public static <T> Iterable<T> mostRecent(final Observable<? extends T> source, final T initialValue) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                Subscriber mostRecentObserver = new MostRecentObserver(initialValue);
                source.subscribe(mostRecentObserver);
                return mostRecentObserver.getIterable();
            }
        };
    }
}
