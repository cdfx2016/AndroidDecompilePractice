package org.jivesoftware.smackx;

import com.google.android.exoplayer2.ExoPlayerFactory;
import java.util.Hashtable;
import java.util.Map;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.packet.DefaultPrivateData;
import org.jivesoftware.smackx.packet.PrivateData;
import org.jivesoftware.smackx.provider.PrivateDataProvider;
import org.xmlpull.v1.XmlPullParser;

public class PrivateDataManager {
    private static Map<String, PrivateDataProvider> privateDataProviders = new Hashtable();
    private Connection connection;
    private String user;

    public static class PrivateDataIQProvider implements IQProvider {
        public IQ parseIQ(XmlPullParser xmlPullParser) throws Exception {
            PrivateData privateData = null;
            Object obj = null;
            while (obj == null) {
                Object obj2;
                PrivateData privateData2;
                Object obj3;
                int next = xmlPullParser.next();
                if (next == 2) {
                    String name = xmlPullParser.getName();
                    String namespace = xmlPullParser.getNamespace();
                    PrivateDataProvider privateDataProvider = PrivateDataManager.getPrivateDataProvider(name, namespace);
                    if (privateDataProvider != null) {
                        privateData = privateDataProvider.parsePrivateData(xmlPullParser);
                    } else {
                        privateData = new DefaultPrivateData(name, namespace);
                        Object obj4 = null;
                        while (obj4 == null) {
                            int next2 = xmlPullParser.next();
                            if (next2 == 2) {
                                String name2 = xmlPullParser.getName();
                                if (xmlPullParser.isEmptyElementTag()) {
                                    privateData.setValue(name2, "");
                                } else if (xmlPullParser.next() == 4) {
                                    privateData.setValue(name2, xmlPullParser.getText());
                                }
                            } else if (next2 == 3 && xmlPullParser.getName().equals(name)) {
                                obj4 = 1;
                            }
                        }
                    }
                    obj2 = obj;
                    privateData2 = privateData;
                    obj3 = obj2;
                } else if (next == 3 && xmlPullParser.getName().equals("query")) {
                    privateData2 = privateData;
                    int i = 1;
                } else {
                    obj2 = obj;
                    privateData2 = privateData;
                    obj3 = obj2;
                }
                obj2 = obj3;
                privateData = privateData2;
                obj = obj2;
            }
            return new PrivateDataResult(privateData);
        }
    }

    private static class PrivateDataResult extends IQ {
        private PrivateData privateData;

        PrivateDataResult(PrivateData privateData) {
            this.privateData = privateData;
        }

        public String getChildElementXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<query xmlns=\"jabber:iq:private\">");
            if (this.privateData != null) {
                this.privateData.toXML();
            }
            stringBuilder.append("</query>");
            return stringBuilder.toString();
        }

        public PrivateData getPrivateData() {
            return this.privateData;
        }
    }

    public PrivateDataManager(Connection connection) {
        if (connection.isAuthenticated()) {
            this.connection = connection;
            return;
        }
        throw new IllegalStateException("Must be logged in to XMPP server.");
    }

    public PrivateDataManager(Connection connection, String str) {
        if (connection.isAuthenticated()) {
            this.connection = connection;
            this.user = str;
            return;
        }
        throw new IllegalStateException("Must be logged in to XMPP server.");
    }

    public static void addPrivateDataProvider(String str, String str2, PrivateDataProvider privateDataProvider) {
        privateDataProviders.put(getProviderKey(str, str2), privateDataProvider);
    }

    public static PrivateDataProvider getPrivateDataProvider(String str, String str2) {
        return (PrivateDataProvider) privateDataProviders.get(getProviderKey(str, str2));
    }

    private static String getProviderKey(String str, String str2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(str).append("/><").append(str2).append("/>");
        return stringBuilder.toString();
    }

    public static void removePrivateDataProvider(String str, String str2) {
        privateDataProviders.remove(getProviderKey(str, str2));
    }

    public PrivateData getPrivateData(final String str, final String str2) throws XMPPException {
        Packet anonymousClass1 = new IQ() {
            public String getChildElementXML() {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("<query xmlns=\"jabber:iq:private\">");
                stringBuilder.append("<").append(str).append(" xmlns=\"").append(str2).append("\"/>");
                stringBuilder.append("</query>");
                return stringBuilder.toString();
            }
        };
        anonymousClass1.setType(Type.GET);
        if (this.user != null) {
            anonymousClass1.setTo(this.user);
        }
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(anonymousClass1.getPacketID()));
        this.connection.sendPacket(anonymousClass1);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from the server.");
        } else if (iq.getType() != Type.ERROR) {
            return ((PrivateDataResult) iq).getPrivateData();
        } else {
            throw new XMPPException(iq.getError());
        }
    }

    public void setPrivateData(final PrivateData privateData) throws XMPPException {
        Packet anonymousClass2 = new IQ() {
            public String getChildElementXML() {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("<query xmlns=\"jabber:iq:private\">");
                stringBuilder.append(privateData.toXML());
                stringBuilder.append("</query>");
                return stringBuilder.toString();
            }
        };
        anonymousClass2.setType(Type.SET);
        if (this.user != null) {
            anonymousClass2.setTo(this.user);
        }
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(anonymousClass2.getPacketID()));
        this.connection.sendPacket(anonymousClass2);
        IQ iq = (IQ) createPacketCollector.nextResult(ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from the server.");
        } else if (iq.getType() == Type.ERROR) {
            throw new XMPPException(iq.getError());
        }
    }
}
