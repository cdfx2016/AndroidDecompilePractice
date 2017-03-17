package org.jivesoftware.smackx.bytestreams.socks5.packet;

import com.easemob.util.HanziToPinyin.Token;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.PacketExtension;

public class Bytestream extends IQ {
    private Mode mode = Mode.tcp;
    private String sessionID;
    private final List<StreamHost> streamHosts = new ArrayList();
    private Activate toActivate;
    private StreamHostUsed usedHost;

    public static class Activate implements PacketExtension {
        public static String ELEMENTNAME = "activate";
        public String NAMESPACE = "";
        private final String target;

        public Activate(String str) {
            this.target = str;
        }

        public String getElementName() {
            return ELEMENTNAME;
        }

        public String getNamespace() {
            return this.NAMESPACE;
        }

        public String getTarget() {
            return this.target;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<").append(getElementName()).append(">");
            stringBuilder.append(getTarget());
            stringBuilder.append("</").append(getElementName()).append(">");
            return stringBuilder.toString();
        }
    }

    public enum Mode {
        tcp,
        udp;

        public static Mode fromName(String str) {
            try {
                return valueOf(str);
            } catch (Exception e) {
                return tcp;
            }
        }
    }

    public static class StreamHost implements PacketExtension {
        public static String ELEMENTNAME = "streamhost";
        public static String NAMESPACE = "";
        private final String JID;
        private final String addy;
        private int port = 0;

        public StreamHost(String str, String str2) {
            this.JID = str;
            this.addy = str2;
        }

        public String getAddress() {
            return this.addy;
        }

        public String getElementName() {
            return ELEMENTNAME;
        }

        public String getJID() {
            return this.JID;
        }

        public String getNamespace() {
            return NAMESPACE;
        }

        public int getPort() {
            return this.port;
        }

        public void setPort(int i) {
            this.port = i;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<").append(getElementName()).append(Token.SEPARATOR);
            stringBuilder.append("jid=\"").append(getJID()).append("\" ");
            stringBuilder.append("host=\"").append(getAddress()).append("\" ");
            if (getPort() != 0) {
                stringBuilder.append("port=\"").append(getPort()).append("\"");
            } else {
                stringBuilder.append("zeroconf=\"_jabber.bytestreams\"");
            }
            stringBuilder.append("/>");
            return stringBuilder.toString();
        }
    }

    public static class StreamHostUsed implements PacketExtension {
        public static String ELEMENTNAME = "streamhost-used";
        private final String JID;
        public String NAMESPACE = "";

        public StreamHostUsed(String str) {
            this.JID = str;
        }

        public String getElementName() {
            return ELEMENTNAME;
        }

        public String getJID() {
            return this.JID;
        }

        public String getNamespace() {
            return this.NAMESPACE;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<").append(getElementName()).append(Token.SEPARATOR);
            stringBuilder.append("jid=\"").append(getJID()).append("\" ");
            stringBuilder.append("/>");
            return stringBuilder.toString();
        }
    }

    public Bytestream(String str) {
        setSessionID(str);
    }

    public StreamHost addStreamHost(String str, String str2) {
        return addStreamHost(str, str2, 0);
    }

    public StreamHost addStreamHost(String str, String str2, int i) {
        StreamHost streamHost = new StreamHost(str, str2);
        streamHost.setPort(i);
        addStreamHost(streamHost);
        return streamHost;
    }

    public void addStreamHost(StreamHost streamHost) {
        this.streamHosts.add(streamHost);
    }

    public int countStreamHosts() {
        return this.streamHosts.size();
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<query xmlns=\"http://jabber.org/protocol/bytestreams\"");
        if (getType().equals(Type.SET)) {
            if (getSessionID() != null) {
                stringBuilder.append(" sid=\"").append(getSessionID()).append("\"");
            }
            if (getMode() != null) {
                stringBuilder.append(" mode = \"").append(getMode()).append("\"");
            }
            stringBuilder.append(">");
            if (getToActivate() == null) {
                for (StreamHost toXML : getStreamHosts()) {
                    stringBuilder.append(toXML.toXML());
                }
            } else {
                stringBuilder.append(getToActivate().toXML());
            }
        } else if (!getType().equals(Type.RESULT)) {
            return getType().equals(Type.GET) ? stringBuilder.append("/>").toString() : null;
        } else {
            stringBuilder.append(">");
            if (getUsedHost() != null) {
                stringBuilder.append(getUsedHost().toXML());
            } else if (countStreamHosts() > 0) {
                for (StreamHost toXML2 : this.streamHosts) {
                    stringBuilder.append(toXML2.toXML());
                }
            }
        }
        stringBuilder.append("</query>");
        return stringBuilder.toString();
    }

    public Mode getMode() {
        return this.mode;
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public StreamHost getStreamHost(String str) {
        if (str == null) {
            return null;
        }
        for (StreamHost streamHost : this.streamHosts) {
            if (streamHost.getJID().equals(str)) {
                return streamHost;
            }
        }
        return null;
    }

    public Collection<StreamHost> getStreamHosts() {
        return Collections.unmodifiableCollection(this.streamHosts);
    }

    public Activate getToActivate() {
        return this.toActivate;
    }

    public StreamHostUsed getUsedHost() {
        return this.usedHost;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setSessionID(String str) {
        this.sessionID = str;
    }

    public void setToActivate(String str) {
        this.toActivate = new Activate(str);
    }

    public void setUsedHost(String str) {
        this.usedHost = new StreamHostUsed(str);
    }
}
