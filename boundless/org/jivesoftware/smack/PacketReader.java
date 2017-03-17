package org.jivesoftware.smack;

import com.easemob.chat.core.b;
import com.xiaomi.mipush.sdk.MiPushClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.packet.XMPPError.Condition;
import org.jivesoftware.smack.parsing.ParsingExceptionCallback;
import org.jivesoftware.smack.parsing.UnparsablePacket;
import org.jivesoftware.smack.sasl.SASLMechanism.Challenge;
import org.jivesoftware.smack.sasl.SASLMechanism.Success;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.entitycaps.EntityCapsManager;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

class PacketReader {
    private XMPPConnection connection;
    private String connectionID = null;
    volatile boolean done;
    private ExecutorService listenerExecutor;
    private XmlPullParser parser;
    private Thread readerThread;

    private class ListenerNotification implements Runnable {
        private Packet packet;

        public ListenerNotification(Packet packet) {
            this.packet = packet;
        }

        public void run() {
            for (ListenerWrapper notifyListener : PacketReader.this.connection.recvListeners.values()) {
                try {
                    notifyListener.notifyListener(this.packet);
                } catch (Exception e) {
                    System.err.println("Exception in packet listener: " + e);
                    e.printStackTrace();
                }
            }
        }
    }

    protected PacketReader(XMPPConnection xMPPConnection) {
        this.connection = xMPPConnection;
        init();
    }

    private void parseFeatures(XmlPullParser xmlPullParser) throws Exception {
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        while (!z) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("starttls")) {
                    z3 = true;
                } else if (xmlPullParser.getName().equals("mechanisms")) {
                    this.connection.getSASLAuthentication().setAvailableSASLMethods(PacketParserUtils.parseMechanisms(xmlPullParser));
                } else if (xmlPullParser.getName().equals("bind")) {
                    this.connection.getSASLAuthentication().bindingRequired();
                } else if (xmlPullParser.getName().equals("ver")) {
                    this.connection.getConfiguration().setRosterVersioningAvailable(true);
                } else if (xmlPullParser.getName().equals(EntityCapsManager.ELEMENT)) {
                    String attributeValue = xmlPullParser.getAttributeValue(null, "node");
                    String attributeValue2 = xmlPullParser.getAttributeValue(null, "ver");
                    if (!(attributeValue2 == null || attributeValue == null)) {
                        this.connection.setServiceCapsNode(attributeValue + "#" + attributeValue2);
                    }
                } else if (xmlPullParser.getName().equals("session")) {
                    this.connection.getSASLAuthentication().sessionsSupported();
                } else if (xmlPullParser.getName().equals("compression")) {
                    this.connection.setAvailableCompressionMethods(PacketParserUtils.parseCompressionMethods(xmlPullParser));
                } else if (xmlPullParser.getName().equals(MiPushClient.COMMAND_REGISTER)) {
                    this.connection.getAccountManager().setSupportsAccountCreation(true);
                }
            } else if (next == 3) {
                if (xmlPullParser.getName().equals("starttls")) {
                    this.connection.startTLSReceived(z2);
                } else if (xmlPullParser.getName().equals("required") && z3) {
                    z2 = true;
                } else if (xmlPullParser.getName().equals("features")) {
                    z = true;
                }
            }
        }
        if (!this.connection.isSecureConnection() && !z3 && this.connection.getConfiguration().getSecurityMode() == SecurityMode.required) {
            throw new XMPPException("Server does not support security (TLS), but security required by connection configuration.", new XMPPError(Condition.forbidden));
        } else if (!z3 || this.connection.getConfiguration().getSecurityMode() == SecurityMode.disabled) {
            releaseConnectionIDLock();
        }
    }

    private void parsePackets(Thread thread) {
        UnparsablePacket unparsablePacket;
        try {
            int eventType = this.parser.getEventType();
            while (true) {
                int i;
                if (eventType == 2) {
                    int depth = this.parser.getDepth();
                    ParsingExceptionCallback parsingExceptionCallback = this.connection.getParsingExceptionCallback();
                    if (this.parser.getName().equals("message")) {
                        try {
                            processPacket(PacketParserUtils.parseMessage(this.parser));
                        } catch (Exception e) {
                            unparsablePacket = new UnparsablePacket(PacketParserUtils.parseContentDepth(this.parser, depth), e);
                            if (parsingExceptionCallback != null) {
                                parsingExceptionCallback.handleUnparsablePacket(unparsablePacket);
                            }
                            i = eventType;
                        }
                    } else if (this.parser.getName().equals("iq")) {
                        try {
                            processPacket(PacketParserUtils.parseIQ(this.parser, this.connection));
                        } catch (Exception e2) {
                            unparsablePacket = new UnparsablePacket(PacketParserUtils.parseContentDepth(this.parser, depth), e2);
                            if (parsingExceptionCallback != null) {
                                parsingExceptionCallback.handleUnparsablePacket(unparsablePacket);
                            }
                            i = eventType;
                        }
                    } else if (this.parser.getName().equals("presence")) {
                        try {
                            processPacket(PacketParserUtils.parsePresence(this.parser));
                        } catch (Exception e22) {
                            unparsablePacket = new UnparsablePacket(PacketParserUtils.parseContentDepth(this.parser, depth), e22);
                            if (parsingExceptionCallback != null) {
                                parsingExceptionCallback.handleUnparsablePacket(unparsablePacket);
                            }
                            i = eventType;
                        }
                    } else if (this.parser.getName().equals("stream")) {
                        if (b.b.equals(this.parser.getNamespace(null))) {
                            for (i = 0; i < this.parser.getAttributeCount(); i++) {
                                if (this.parser.getAttributeName(i).equals("id")) {
                                    this.connectionID = this.parser.getAttributeValue(i);
                                    if (!"1.0".equals(this.parser.getAttributeValue("", "version"))) {
                                        releaseConnectionIDLock();
                                    }
                                } else if (this.parser.getAttributeName(i).equals("from")) {
                                    this.connection.config.setServiceName(this.parser.getAttributeValue(i));
                                }
                            }
                        }
                    } else if (this.parser.getName().equals("error")) {
                        throw new XMPPException(PacketParserUtils.parseStreamError(this.parser));
                    } else if (this.parser.getName().equals("features")) {
                        parseFeatures(this.parser);
                    } else if (this.parser.getName().equals("proceed")) {
                        this.connection.proceedTLSReceived();
                        resetParser();
                    } else if (this.parser.getName().equals("failure")) {
                        r0 = this.parser.getNamespace(null);
                        if ("urn:ietf:params:xml:ns:xmpp-tls".equals(r0)) {
                            throw new Exception("TLS negotiation has failed");
                        } else if ("http://jabber.org/protocol/compress".equals(r0)) {
                            this.connection.streamCompressionDenied();
                        } else {
                            processPacket(PacketParserUtils.parseSASLFailure(this.parser));
                            this.connection.getSASLAuthentication().authenticationFailed();
                        }
                    } else if (this.parser.getName().equals("challenge")) {
                        r0 = this.parser.nextText();
                        processPacket(new Challenge(r0));
                        this.connection.getSASLAuthentication().challengeReceived(r0);
                    } else if (this.parser.getName().equals("success")) {
                        processPacket(new Success(this.parser.nextText()));
                        this.connection.packetWriter.openStream();
                        resetParser();
                        this.connection.getSASLAuthentication().authenticated();
                    } else if (this.parser.getName().equals("compressed")) {
                        this.connection.startStreamCompression();
                        resetParser();
                    }
                } else if (eventType == 3 && this.parser.getName().equals("stream")) {
                    this.connection.disconnect();
                }
                i = this.parser.next();
                if (!this.done && i != 1 && thread == this.readerThread) {
                    eventType = i;
                } else {
                    return;
                }
            }
        } catch (Exception e222) {
            if (!this.done && !this.connection.isSocketClosed()) {
                this.connection.notifyConnectionError(e222);
            }
        }
    }

    private void processPacket(Packet packet) {
        if (packet != null) {
            for (PacketCollector processPacket : this.connection.getPacketCollectors()) {
                processPacket.processPacket(packet);
            }
            this.listenerExecutor.submit(new ListenerNotification(packet));
        }
    }

    private synchronized void releaseConnectionIDLock() {
        notify();
    }

    private void resetParser() {
        try {
            this.parser = XmlPullParserFactory.newInstance().newPullParser();
            this.parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
            this.parser.setInput(this.connection.reader);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    protected void init() {
        this.done = false;
        this.connectionID = null;
        this.readerThread = new Thread() {
            public void run() {
                PacketReader.this.parsePackets(this);
            }
        };
        this.readerThread.setName("Smack Packet Reader (" + this.connection.connectionCounterValue + ")");
        this.readerThread.setDaemon(true);
        this.listenerExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, "Smack Listener Processor (" + PacketReader.this.connection.connectionCounterValue + ")");
                thread.setDaemon(true);
                return thread;
            }
        });
        resetParser();
    }

    public void shutdown() {
        if (!this.done) {
            for (ConnectionListener connectionClosed : this.connection.getConnectionListeners()) {
                try {
                    connectionClosed.connectionClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        this.done = true;
        this.listenerExecutor.shutdown();
    }

    public synchronized void startup() throws XMPPException {
        this.readerThread.start();
        try {
            wait((long) (SmackConfiguration.getPacketReplyTimeout() * 3));
        } catch (InterruptedException e) {
        }
        if (this.connectionID == null) {
            throw new XMPPException("Connection failed. No response from server.");
        }
        this.connection.connectionID = this.connectionID;
    }
}
