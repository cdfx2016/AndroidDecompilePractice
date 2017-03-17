package com.easemob.chat;

public class EMChatOptions {
    private boolean acceptInvitationAlways = true;
    private boolean noticedBySound = true;
    private boolean noticedByVibrate = true;
    private boolean notificationEnable = true;
    OnMessageNotifyListener onMessageNotifyListener;
    OnNotificationClickListener onNotificationClickListener;
    private boolean requireAck = true;
    private boolean requireServerAck = true;
    private boolean useEncryption = false;
    private boolean useRoster = true;
    private boolean useSpeaker = true;

    public boolean getAcceptInvitationAlways() {
        return this.acceptInvitationAlways;
    }

    public boolean getNoticedBySound() {
        return this.noticedBySound;
    }

    public boolean getNoticedByVibrate() {
        return this.noticedByVibrate;
    }

    public boolean getNotificationEnable() {
        return this.notificationEnable;
    }

    public boolean getRequireAck() {
        return this.requireAck;
    }

    public boolean getRequireServerAck() {
        return this.requireServerAck;
    }

    public boolean getUseEncryption() {
        return this.useEncryption;
    }

    public boolean getUseRoster() {
        return this.useRoster;
    }

    public boolean getUseSpeaker() {
        return this.useSpeaker;
    }

    public void setAcceptInvitationAlways(boolean z) {
        this.acceptInvitationAlways = z;
    }

    public void setNoticeBySound(boolean z) {
        this.noticedBySound = z;
    }

    public void setNoticedByVibrate(boolean z) {
        this.noticedByVibrate = z;
    }

    public void setNotificationEnable(boolean z) {
        this.notificationEnable = z;
    }

    public void setNotifyText(OnMessageNotifyListener onMessageNotifyListener) {
        this.onMessageNotifyListener = onMessageNotifyListener;
    }

    public void setOnNotificationClickListener(OnNotificationClickListener onNotificationClickListener) {
        this.onNotificationClickListener = onNotificationClickListener;
    }

    public void setRequireAck(boolean z) {
        this.requireAck = z;
    }

    public void setRequireServerAck(boolean z) {
        this.requireServerAck = z;
    }

    public void setUseEncryption(boolean z) {
        this.useEncryption = z;
    }

    public void setUseRoster(boolean z) {
        this.useRoster = z;
    }

    public void setUseSpeaker(boolean z) {
        this.useSpeaker = z;
    }
}
