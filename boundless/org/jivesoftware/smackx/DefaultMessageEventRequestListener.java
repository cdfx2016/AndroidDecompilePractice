package org.jivesoftware.smackx;

public class DefaultMessageEventRequestListener implements MessageEventRequestListener {
    public void composingNotificationRequested(String str, String str2, MessageEventManager messageEventManager) {
    }

    public void deliveredNotificationRequested(String str, String str2, MessageEventManager messageEventManager) {
        messageEventManager.sendDeliveredNotification(str, str2);
    }

    public void displayedNotificationRequested(String str, String str2, MessageEventManager messageEventManager) {
    }

    public void offlineNotificationRequested(String str, String str2, MessageEventManager messageEventManager) {
    }
}
