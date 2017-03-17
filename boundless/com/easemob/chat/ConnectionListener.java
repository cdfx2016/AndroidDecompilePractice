package com.easemob.chat;

public interface ConnectionListener {
    void onConnected();

    void onConnecting(String str);

    void onDisConnected(String str);

    void onReConnected();

    void onReConnecting();
}
