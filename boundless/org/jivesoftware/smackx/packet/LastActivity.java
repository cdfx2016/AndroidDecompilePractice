package org.jivesoftware.smackx.packet;

import java.io.IOException;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class LastActivity extends IQ {
    public static final String NAMESPACE = "jabber:iq:last";
    public long lastActivity = -1;
    public String message;

    public static class Provider implements IQProvider {
        public IQ parseIQ(XmlPullParser xmlPullParser) throws XMPPException, XmlPullParserException {
            if (xmlPullParser.getEventType() != 2) {
                throw new XMPPException("Parser not in proper position, or bad XML.");
            }
            IQ lastActivity = new LastActivity();
            String attributeValue = xmlPullParser.getAttributeValue("", "seconds");
            String str = null;
            try {
                str = xmlPullParser.nextText();
            } catch (IOException e) {
            }
            if (attributeValue != null) {
                try {
                    lastActivity.setLastActivity(Long.parseLong(attributeValue));
                } catch (NumberFormatException e2) {
                }
            }
            if (str != null) {
                lastActivity.setMessage(str);
            }
            return lastActivity;
        }
    }

    public LastActivity() {
        setType(Type.GET);
    }

    public static LastActivity getLastActivity(Connection connection, String str) throws XMPPException {
        Packet lastActivity = new LastActivity();
        lastActivity.setTo(StringUtils.parseBareAddress(str));
        PacketCollector createPacketCollector = connection.createPacketCollector(new PacketIDFilter(lastActivity.getPacketID()));
        connection.sendPacket(lastActivity);
        LastActivity lastActivity2 = (LastActivity) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (lastActivity2 == null) {
            throw new XMPPException("No response from server on status set.");
        } else if (lastActivity2.getError() == null) {
            return lastActivity2;
        } else {
            throw new XMPPException(lastActivity2.getError());
        }
    }

    private void setMessage(String str) {
        this.message = str;
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<query xmlns=\"jabber:iq:last\"");
        if (this.lastActivity != -1) {
            stringBuilder.append(" seconds=\"").append(this.lastActivity).append("\"");
        }
        stringBuilder.append("></query>");
        return stringBuilder.toString();
    }

    public long getIdleTime() {
        return this.lastActivity;
    }

    public String getStatusMessage() {
        return this.message;
    }

    public void setLastActivity(long j) {
        this.lastActivity = j;
    }
}
