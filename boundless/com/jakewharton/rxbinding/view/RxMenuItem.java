package com.jakewharton.rxbinding.view;

import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import com.jakewharton.rxbinding.internal.Functions;
import com.jakewharton.rxbinding.internal.Preconditions;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public final class RxMenuItem {
    @CheckResult
    @NonNull
    public static Observable<Void> clicks(@NonNull MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return Observable.create(new MenuItemClickOnSubscribe(menuItem, Functions.FUNC1_ALWAYS_TRUE));
    }

    @CheckResult
    @NonNull
    public static Observable<Void> clicks(@NonNull MenuItem menuItem, @NonNull Func1<? super MenuItem, Boolean> handled) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return Observable.create(new MenuItemClickOnSubscribe(menuItem, handled));
    }

    @CheckResult
    @NonNull
    public static Observable<MenuItemActionViewEvent> actionViewEvents(@NonNull MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return Observable.create(new MenuItemActionViewEventOnSubscribe(menuItem, Functions.FUNC1_ALWAYS_TRUE));
    }

    @CheckResult
    @NonNull
    public static Observable<MenuItemActionViewEvent> actionViewEvents(@NonNull MenuItem menuItem, @NonNull Func1<? super MenuItemActionViewEvent, Boolean> handled) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return Observable.create(new MenuItemActionViewEventOnSubscribe(menuItem, handled));
    }

    @CheckResult
    @NonNull
    public static Action1<? super Boolean> checked(@NonNull final MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new Action1<Boolean>() {
            public void call(Boolean value) {
                menuItem.setChecked(value.booleanValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Boolean> enabled(@NonNull final MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new Action1<Boolean>() {
            public void call(Boolean value) {
                menuItem.setEnabled(value.booleanValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Drawable> icon(@NonNull final MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new Action1<Drawable>() {
            public void call(Drawable value) {
                menuItem.setIcon(value);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Integer> iconRes(@NonNull final MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new Action1<Integer>() {
            public void call(Integer value) {
                menuItem.setIcon(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super CharSequence> title(@NonNull final MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new Action1<CharSequence>() {
            public void call(CharSequence value) {
                menuItem.setTitle(value);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Integer> titleRes(@NonNull final MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new Action1<Integer>() {
            public void call(Integer value) {
                menuItem.setTitle(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Action1<? super Boolean> visible(@NonNull final MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new Action1<Boolean>() {
            public void call(Boolean value) {
                menuItem.setVisible(value.booleanValue());
            }
        };
    }

    private RxMenuItem() {
        throw new AssertionError("No instances.");
    }
}
