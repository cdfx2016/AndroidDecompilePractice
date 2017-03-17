package org.jivesoftware.smackx;

import android.content.Context;
import org.jivesoftware.smack.PrivacyListManager;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager;
import org.jivesoftware.smackx.commands.AdHocCommandManager;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class InitStaticCode {
    public static void initStaticCode(Context context) {
        ClassLoader classLoader = context.getClassLoader();
        try {
            Class.forName(ServiceDiscoveryManager.class.getName(), true, classLoader);
            Class.forName(PrivacyListManager.class.getName(), true, classLoader);
            Class.forName(XHTMLManager.class.getName(), true, classLoader);
            Class.forName(MultiUserChat.class.getName(), true, classLoader);
            Class.forName(InBandBytestreamManager.class.getName(), true, classLoader);
            Class.forName(Socks5BytestreamManager.class.getName(), true, classLoader);
            Class.forName(FileTransferManager.class.getName(), true, classLoader);
            Class.forName(LastActivityManager.class.getName(), true, classLoader);
            Class.forName(ReconnectionManager.class.getName(), true, classLoader);
            Class.forName(AdHocCommandManager.class.getName(), true, classLoader);
        } catch (Throwable e) {
            throw new IllegalStateException("Could not init static class blocks", e);
        }
    }
}
