package android.support.v7.app;

import android.app.Notification.BigTextStyle;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;

class NotificationCompatImplJellybean {
    NotificationCompatImplJellybean() {
    }

    public static void addBigTextStyle(NotificationBuilderWithBuilderAccessor b, CharSequence bigText) {
        new BigTextStyle(b.getBuilder()).bigText(bigText);
    }
}
