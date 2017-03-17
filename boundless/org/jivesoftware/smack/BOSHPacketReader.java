package org.jivesoftware.smack;

import com.kenai.jbosh.AbstractBody;
import com.kenai.jbosh.BOSHClientResponseListener;
import com.kenai.jbosh.BOSHMessageEvent;
import com.kenai.jbosh.BodyQName;
import com.kenai.jbosh.ComposableBody;
import com.xiaomi.mipush.sdk.MiPushClient;
import java.io.StringReader;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.sasl.SASLMechanism.Challenge;
import org.jivesoftware.smack.sasl.SASLMechanism.Success;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class BOSHPacketReader implements BOSHClientResponseListener {
    private BOSHConnection connection;

    public BOSHPacketReader(BOSHConnection bOSHConnection) {
        this.connection = bOSHConnection;
    }

    private void parseFeatures(XmlPullParser xmlPullParser) throws Exception {
        boolean z = false;
        while (!z) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("mechanisms")) {
                    this.connection.getSASLAuthentication().setAvailableSASLMethods(PacketParserUtils.parseMechanisms(xmlPullParser));
                } else if (xmlPullParser.getName().equals("bind")) {
                    this.connection.getSASLAuthentication().bindingRequired();
                } else if (xmlPullParser.getName().equals("session")) {
                    this.connection.getSASLAuthentication().sessionsSupported();
                } else if (xmlPullParser.getName().equals(MiPushClient.COMMAND_REGISTER)) {
                    this.connection.getAccountManager().setSupportsAccountCreation(true);
                }
            } else if (next == 3 && xmlPullParser.getName().equals("features")) {
                z = true;
            }
        }
    }

    public void responseReceived(BOSHMessageEvent bOSHMessageEvent) {
        AbstractBody body = bOSHMessageEvent.getBody();
        if (body != null) {
            try {
                if (this.connection.sessionID == null) {
                    this.connection.sessionID = body.getAttribute(BodyQName.create(BOSHConnection.BOSH_URI, "sid"));
                }
                if (this.connection.authID == null) {
                    this.connection.authID = body.getAttribute(BodyQName.create(BOSHConnection.BOSH_URI, "authid"));
                }
                XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
                newPullParser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
                newPullParser.setInput(new StringReader(body.toXML()));
                newPullParser.getEventType();
                int next;
                do {
                    next = newPullParser.next();
                    if (next == 2 && !newPullParser.getName().equals(TtmlNode.TAG_BODY)) {
                        if (newPullParser.getName().equals("message")) {
                            this.connection.processPacket(PacketParserUtils.parseMessage(newPullParser));
                            continue;
                        } else if (newPullParser.getName().equals("iq")) {
                            this.connection.processPacket(PacketParserUtils.parseIQ(newPullParser, this.connection));
                            continue;
                        } else if (newPullParser.getName().equals("presence")) {
                            this.connection.processPacket(PacketParserUtils.parsePresence(newPullParser));
                            continue;
                        } else if (newPullParser.getName().equals("challenge")) {
                            String nextText = newPullParser.nextText();
                            this.connection.getSASLAuthentication().challengeReceived(nextText);
                            this.connection.processPacket(new Challenge(nextText));
                            continue;
                        } else if (newPullParser.getName().equals("success")) {
                            this.connection.send(ComposableBody.builder().setNamespaceDefinition("xmpp", BOSHConnection.XMPP_BOSH_NS).setAttribute(BodyQName.createWithPrefix(BOSHConnection.XMPP_BOSH_NS, "restart", "xmpp"), "true").setAttribute(BodyQName.create(BOSHConnection.BOSH_URI, "to"), this.connection.getServiceName()).build());
                            this.connection.getSASLAuthentication().authenticated();
                            this.connection.processPacket(new Success(newPullParser.nextText()));
                            continue;
                        } else if (newPullParser.getName().equals("features")) {
                            parseFeatures(newPullParser);
                            continue;
                        } else if (newPullParser.getName().equals("failure")) {
                            if ("urn:ietf:params:xml:ns:xmpp-sasl".equals(newPullParser.getNamespace(null))) {
                                Packet parseSASLFailure = PacketParserUtils.parseSASLFailure(newPullParser);
                                this.connection.getSASLAuthentication().authenticationFailed();
                                this.connection.processPacket(parseSASLFailure);
                                continue;
                            } else {
                                continue;
                            }
                        } else if (newPullParser.getName().equals("error")) {
                            throw new XMPPException(PacketParserUtils.parseStreamError(newPullParser));
                        }
                    }
                } while (next != 1);
            } catch (Exception e) {
                if (this.connection.isConnected()) {
                    this.connection.notifyConnectionError(e);
                }
            }
        }
    }
}
