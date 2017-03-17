package com.easemob.chat;

import java.util.List;

public interface EMContactListener {
    void onContactAdded(List<String> list);

    void onContactAgreed(String str);

    void onContactDeleted(List<String> list);

    void onContactInvited(String str, String str2);

    void onContactRefused(String str);
}
