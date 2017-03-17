package org.jivesoftware.smackx;

import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.packet.PEPEvent;
import org.jivesoftware.smackx.packet.PEPItem;
import org.jivesoftware.smackx.packet.PEPPubSub;

public class PEPManager {
    private Connection connection;
    private PacketFilter packetFilter = new PacketExtensionFilter("event", "http://jabber.org/protocol/pubsub#event");
    private PacketListener packetListener;
    private List<PEPListener> pepListeners = new ArrayList();

    public PEPManager(Connection connection) {
        this.connection = connection;
        init();
    }

    private void firePEPListeners(String str, PEPEvent pEPEvent) {
        synchronized (this.pepListeners) {
            PEPListener[] pEPListenerArr = new PEPListener[this.pepListeners.size()];
            this.pepListeners.toArray(pEPListenerArr);
        }
        for (PEPListener eventReceived : pEPListenerArr) {
            eventReceived.eventReceived(str, pEPEvent);
        }
    }

    private void init() {
        this.packetListener = new PacketListener() {
            public void processPacket(Packet packet) {
                Message message = (Message) packet;
                PEPManager.this.firePEPListeners(message.getFrom(), (PEPEvent) message.getExtension("event", "http://jabber.org/protocol/pubsub#event"));
            }
        };
        this.connection.addPacketListener(this.packetListener, this.packetFilter);
    }

    public void addPEPListener(PEPListener pEPListener) {
        synchronized (this.pepListeners) {
            if (!this.pepListeners.contains(pEPListener)) {
                this.pepListeners.add(pEPListener);
            }
        }
    }

    public void destroy() {
        if (this.connection != null) {
            this.connection.removePacketListener(this.packetListener);
        }
    }

    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    public void publish(PEPItem pEPItem) {
        Packet pEPPubSub = new PEPPubSub(pEPItem);
        pEPPubSub.setType(Type.SET);
        this.connection.sendPacket(pEPPubSub);
    }

    public void removePEPListener(PEPListener pEPListener) {
        synchronized (this.pepListeners) {
            this.pepListeners.remove(pEPListener);
        }
    }
}
