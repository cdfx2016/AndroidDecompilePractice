package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.TextView;
import com.jakewharton.rxbinding.internal.Functions;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public final class RxTextView {
    @CheckResult
    @NonNull
    public static Observable<Integer> editorActions(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return editorActions(view, Functions.FUNC1_ALWAYS_TRUE);
    }

    @CheckResult
    @NonNull
    public static Observable<Integer> editorActions(@NonNull TextView view, @NonNull Func1<? super Integer, Boolean> handled) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return Observable.create(new TextViewEditorActionOnSubscribe(view, handled));
    }

    @CheckResult
    @NonNull
    public static Observable<TextViewEditorActionEvent> editorActionEvents(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return editorActionEvents(view, Functions.FUNC1_ALWAYS_TRUE);
    }

    @CheckResult
    @NonNull
    public static Observable<TextViewEditorActionEvent> editorActionEvents(@NonNull TextView view, @NonNull Func1<? super TextViewEditorActionEvent, Boolean> handled) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return Observable.create(new TextViewEditorActionEventOnSubscribe(view, handled));
    }

    @CheckResult
    @NonNull
    public static Observable<CharSequence> textChanges(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new TextViewTextOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<TextViewTextChangeEvent> textChangeEvents(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new TextViewTextChangeEventOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<TextViewBeforeTextChangeEvent> beforeTextChangeEvents(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new TextViewBeforeTextChangeEventOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<TextViewAfterTextChangeEvent> afterTextChangeEvents(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new TextViewAfterTextChangeEventOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Action1<? super CharSequence> text(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<CharSequence>() {
            public void call(CharSequence text) {
                view.setText(text);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Integer> textRes(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Integer>() {
            public void call(Integer textRes) {
                view.setText(textRes.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super CharSequence> error(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<CharSequence>() {
            public void call(CharSequence text) {
                view.setError(text);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Integer> errorRes(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Integer>() {
            public void call(Integer textRes) {
                view.setError(view.getContext().getResources().getText(textRes.intValue()));
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super CharSequence> hint(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<CharSequence>() {
            public void call(CharSequence hint) {
                view.setHint(hint);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Integer> hintRes(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Integer>() {
            public void call(Integer hintRes) {
                view.setHint(hintRes.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Integer> color(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Integer>() {
            public void call(Integer color) {
                view.setTextColor(color.intValue());
            }
        };
    }

    private RxTextView() {
        throw new AssertionError("No instances.");
    }
}
