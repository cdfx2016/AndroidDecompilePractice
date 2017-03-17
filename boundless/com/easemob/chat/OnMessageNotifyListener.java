package com.easemob.chat;

public interface OnMessageNotifyListener {
    String onLatestMessageNotify(EMMessage eMMessage, int i, int i2);

    String onNewMessageNotify(EMMessage eMMessage);
}
