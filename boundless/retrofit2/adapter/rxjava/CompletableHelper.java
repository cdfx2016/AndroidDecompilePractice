package retrofit2.adapter.rxjava;

import java.lang.reflect.Type;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import rx.Completable;
import rx.Completable.CompletableOnSubscribe;
import rx.Completable.CompletableSubscriber;
import rx.Scheduler;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

final class CompletableHelper {

    static class CompletableCallAdapter implements CallAdapter<Completable> {
        private final Scheduler scheduler;

        CompletableCallAdapter(Scheduler scheduler) {
            this.scheduler = scheduler;
        }

        public Type responseType() {
            return Void.class;
        }

        public Completable adapt(Call call) {
            Completable completable = Completable.create(new CompletableCallOnSubscribe(call));
            if (this.scheduler != null) {
                return completable.subscribeOn(this.scheduler);
            }
            return completable;
        }
    }

    private static final class CompletableCallOnSubscribe implements CompletableOnSubscribe {
        private final Call originalCall;

        CompletableCallOnSubscribe(Call originalCall) {
            this.originalCall = originalCall;
        }

        public void call(CompletableSubscriber subscriber) {
            final Call call = this.originalCall.clone();
            Subscription subscription = Subscriptions.create(new Action0() {
                public void call() {
                    call.cancel();
                }
            });
            subscriber.onSubscribe(subscription);
            try {
                Response response = call.execute();
                if (!subscription.isUnsubscribed()) {
                    if (response.isSuccessful()) {
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new HttpException(response));
                    }
                }
            } catch (Throwable t) {
                Exceptions.throwIfFatal(t);
                if (!subscription.isUnsubscribed()) {
                    subscriber.onError(t);
                }
            }
        }
    }

    CompletableHelper() {
    }

    static CallAdapter<Completable> createCallAdapter(Scheduler scheduler) {
        return new CompletableCallAdapter(scheduler);
    }
}
