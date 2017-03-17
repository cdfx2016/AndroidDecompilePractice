package com.trello.rxlifecycle.android;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.OutsideLifecycleException;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.internal.Preconditions;
import rx.Observable;
import rx.functions.Func1;

public class RxLifecycleAndroid {
    private static final Func1<ActivityEvent, ActivityEvent> ACTIVITY_LIFECYCLE = new Func1<ActivityEvent, ActivityEvent>() {
        public ActivityEvent call(ActivityEvent lastEvent) {
            switch (lastEvent) {
                case CREATE:
                    return ActivityEvent.DESTROY;
                case START:
                    return ActivityEvent.STOP;
                case RESUME:
                    return ActivityEvent.PAUSE;
                case PAUSE:
                    return ActivityEvent.STOP;
                case STOP:
                    return ActivityEvent.DESTROY;
                case DESTROY:
                    throw new OutsideLifecycleException("Cannot bind to Activity lifecycle when outside of it.");
                default:
                    throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
            }
        }
    };
    private static final Func1<FragmentEvent, FragmentEvent> FRAGMENT_LIFECYCLE = new Func1<FragmentEvent, FragmentEvent>() {
        public FragmentEvent call(FragmentEvent lastEvent) {
            switch (lastEvent) {
                case ATTACH:
                    return FragmentEvent.DETACH;
                case CREATE:
                    return FragmentEvent.DESTROY;
                case CREATE_VIEW:
                    return FragmentEvent.DESTROY_VIEW;
                case START:
                    return FragmentEvent.STOP;
                case RESUME:
                    return FragmentEvent.PAUSE;
                case PAUSE:
                    return FragmentEvent.STOP;
                case STOP:
                    return FragmentEvent.DESTROY_VIEW;
                case DESTROY_VIEW:
                    return FragmentEvent.DESTROY;
                case DESTROY:
                    return FragmentEvent.DETACH;
                case DETACH:
                    throw new OutsideLifecycleException("Cannot bind to Fragment lifecycle when outside of it.");
                default:
                    throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
            }
        }
    };

    private RxLifecycleAndroid() {
        throw new AssertionError("No instances");
    }

    @CheckResult
    @NonNull
    public static <T> LifecycleTransformer<T> bindActivity(@NonNull Observable<ActivityEvent> lifecycle) {
        return RxLifecycle.bind(lifecycle, ACTIVITY_LIFECYCLE);
    }

    @CheckResult
    @NonNull
    public static <T> LifecycleTransformer<T> bindFragment(@NonNull Observable<FragmentEvent> lifecycle) {
        return RxLifecycle.bind(lifecycle, FRAGMENT_LIFECYCLE);
    }

    @CheckResult
    @NonNull
    public static <T> LifecycleTransformer<T> bindView(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return RxLifecycle.bind(RxView.detaches(view));
    }
}
