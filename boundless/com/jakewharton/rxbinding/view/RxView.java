package com.jakewharton.rxbinding.view;

import android.annotation.TargetApi;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import com.jakewharton.rxbinding.internal.Functions;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

public final class RxView {
    @CheckResult
    @NonNull
    public static Observable<Void> attaches(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new ViewAttachesOnSubscribe(view, true));
    }

    @CheckResult
    @NonNull
    public static Observable<ViewAttachEvent> attachEvents(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new ViewAttachEventOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<Void> detaches(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new ViewAttachesOnSubscribe(view, false));
    }

    @CheckResult
    @NonNull
    public static Observable<Void> clicks(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new ViewClickOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<DragEvent> drags(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new ViewDragOnSubscribe(view, Functions.FUNC1_ALWAYS_TRUE));
    }

    @CheckResult
    @NonNull
    public static Observable<DragEvent> drags(@NonNull View view, @NonNull Func1<? super DragEvent, Boolean> handled) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return Observable.create(new ViewDragOnSubscribe(view, handled));
    }

    @CheckResult
    @NonNull
    public static Observable<Void> draws(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new ViewTreeObserverDrawOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<Boolean> focusChanges(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new ViewFocusChangeOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<Void> globalLayouts(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new ViewTreeObserverGlobalLayoutOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<MotionEvent> hovers(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return hovers(view, Functions.FUNC1_ALWAYS_TRUE);
    }

    @CheckResult
    @NonNull
    public static Observable<MotionEvent> hovers(@NonNull View view, @NonNull Func1<? super MotionEvent, Boolean> handled) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return Observable.create(new ViewHoverOnSubscribe(view, handled));
    }

    @CheckResult
    @NonNull
    public static Observable<Void> layoutChanges(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new ViewLayoutChangeOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<ViewLayoutChangeEvent> layoutChangeEvents(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new ViewLayoutChangeEventOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<Void> longClicks(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new ViewLongClickOnSubscribe(view, Functions.FUNC0_ALWAYS_TRUE));
    }

    @CheckResult
    @NonNull
    public static Observable<Void> longClicks(@NonNull View view, @NonNull Func0<Boolean> handled) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return Observable.create(new ViewLongClickOnSubscribe(view, handled));
    }

    @CheckResult
    @NonNull
    public static Observable<Void> preDraws(@NonNull View view, @NonNull Func0<Boolean> proceedDrawingPass) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(proceedDrawingPass, "proceedDrawingPass == null");
        return Observable.create(new ViewTreeObserverPreDrawOnSubscribe(view, proceedDrawingPass));
    }

    @CheckResult
    @NonNull
    @TargetApi(23)
    public static Observable<ViewScrollChangeEvent> scrollChangeEvents(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new ViewScrollChangeEventOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<Integer> systemUiVisibilityChanges(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new ViewSystemUiVisibilityChangeOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<MotionEvent> touches(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return touches(view, Functions.FUNC1_ALWAYS_TRUE);
    }

    @CheckResult
    @NonNull
    public static Observable<MotionEvent> touches(@NonNull View view, @NonNull Func1<? super MotionEvent, Boolean> handled) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return Observable.create(new ViewTouchOnSubscribe(view, handled));
    }

    @CheckResult
    @NonNull
    public static Action1<? super Boolean> activated(@NonNull final View view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Boolean>() {
            public void call(Boolean value) {
                view.setActivated(value.booleanValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Boolean> clickable(@NonNull final View view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Boolean>() {
            public void call(Boolean value) {
                view.setClickable(value.booleanValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Boolean> enabled(@NonNull final View view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Boolean>() {
            public void call(Boolean value) {
                view.setEnabled(value.booleanValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Boolean> pressed(@NonNull final View view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Boolean>() {
            public void call(Boolean value) {
                view.setPressed(value.booleanValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Boolean> selected(@NonNull final View view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Boolean>() {
            public void call(Boolean value) {
                view.setSelected(value.booleanValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Boolean> visibility(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return visibility(view, 8);
    }

    @CheckResult
    @NonNull
    public static Action1<? super Boolean> visibility(@NonNull final View view, final int visibilityWhenFalse) {
        boolean z;
        boolean z2 = false;
        Preconditions.checkNotNull(view, "view == null");
        if (visibilityWhenFalse != 0) {
            z = true;
        } else {
            z = false;
        }
        Preconditions.checkArgument(z, "Setting visibility to VISIBLE when false would have no effect.");
        if (visibilityWhenFalse == 4 || visibilityWhenFalse == 8) {
            z2 = true;
        }
        Preconditions.checkArgument(z2, "Must set visibility to INVISIBLE or GONE when false.");
        return new Action1<Boolean>() {
            public void call(Boolean value) {
                view.setVisibility(value.booleanValue() ? 0 : visibilityWhenFalse);
            }
        };
    }

    private RxView() {
        throw new AssertionError("No instances.");
    }
}
