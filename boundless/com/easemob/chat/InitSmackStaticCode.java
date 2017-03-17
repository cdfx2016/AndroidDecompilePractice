package com.easemob.chat;

import android.content.Context;
import org.jivesoftware.smack.PrivacyListManager;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smackx.LastActivityManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.XHTMLManager;
import org.jivesoftware.smackx.entitycaps.packet.CapsExtension;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class InitSmackStaticCode {
    public static void initStaticCode(Context context) {
        ClassLoader classLoader = context.getClassLoader();
        try {
            Class.forName(ServiceDiscoveryManager.class.getName(), true, classLoader);
            Class.forName(PrivacyListManager.class.getName(), true, classLoader);
            Class.forName(XHTMLManager.class.getName(), true, classLoader);
            Class.forName(MultiUserChat.class.getName(), true, classLoader);
            Class.forName(LastActivityManager.class.getName(), true, classLoader);
            Class.forName(ReconnectionManager.class.getName(), true, classLoader);
            Class.forName(CapsExtension.class.getName(), true, classLoader);
        } catch (Throwable e) {
            throw new IllegalStateException("Could not init static class blocks", e);
        }
    }
}
