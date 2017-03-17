package org.jivesoftware.smackx;

import java.util.Iterator;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.packet.XHTMLExtension;

public class XHTMLManager {
    private static final String namespace = "http://jabber.org/protocol/xhtml-im";

    static {
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(Connection connection) {
                XHTMLManager.setServiceEnabled(connection, true);
            }
        });
    }

    public static void addBody(Message message, String str) {
        XHTMLExtension xHTMLExtension = (XHTMLExtension) message.getExtension("html", namespace);
        if (xHTMLExtension == null) {
            xHTMLExtension = new XHTMLExtension();
            message.addExtension(xHTMLExtension);
        }
        xHTMLExtension.addBody(str);
    }

    public static Iterator<String> getBodies(Message message) {
        XHTMLExtension xHTMLExtension = (XHTMLExtension) message.getExtension("html", namespace);
        return xHTMLExtension != null ? xHTMLExtension.getBodies() : null;
    }

    public static boolean isServiceEnabled(Connection connection) {
        return ServiceDiscoveryManager.getInstanceFor(connection).includesFeature(namespace);
    }

    public static boolean isServiceEnabled(Connection connection, String str) {
        try {
            return ServiceDiscoveryManager.getInstanceFor(connection).discoverInfo(str).containsFeature(namespace);
        } catch (XMPPException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isXHTMLMessage(Message message) {
        return message.getExtension("html", namespace) != null;
    }

    public static synchronized void setServiceEnabled(Connection connection, boolean z) {
        synchronized (XHTMLManager.class) {
            if (isServiceEnabled(connection) != z) {
                if (z) {
                    ServiceDiscoveryManager.getInstanceFor(connection).addFeature(namespace);
                } else {
                    ServiceDiscoveryManager.getInstanceFor(connection).removeFeature(namespace);
                }
            }
        }
    }
}
