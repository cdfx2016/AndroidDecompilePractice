package com.jakewharton.rxbinding.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.MenuItem;

public final class MenuItemActionViewEvent extends MenuItemEvent<MenuItem> {
    private final Kind kind;

    public enum Kind {
        EXPAND,
        COLLAPSE
    }

    @CheckResult
    @NonNull
    public static MenuItemActionViewEvent create(@NonNull MenuItem menuItem, @NonNull Kind kind) {
        return new MenuItemActionViewEvent(menuItem, kind);
    }

    private MenuItemActionViewEvent(@NonNull MenuItem menuItem, @NonNull Kind kind) {
        super(menuItem);
        this.kind = kind;
    }

    @NonNull
    public Kind kind() {
        return this.kind;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MenuItemActionViewEvent that = (MenuItemActionViewEvent) o;
        if (!menuItem().equals(that.menuItem())) {
            return false;
        }
        if (this.kind != that.kind) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (menuItem().hashCode() * 31) + this.kind.hashCode();
    }

    public String toString() {
        return "MenuItemActionViewEvent{menuItem=" + menuItem() + ", kind=" + this.kind + '}';
    }
}
