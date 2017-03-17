package org.jivesoftware.smackx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo.Identity;

public class Gateway {
    private Connection connection;
    private String entityJID;
    private Identity identity;
    private DiscoverInfo info;
    private Registration registerInfo;
    private Roster roster;
    private ServiceDiscoveryManager sdManager;

    private class GatewayPresenceListener implements PacketListener {
        private GatewayPresenceListener() {
        }

        public void processPacket(Packet packet) {
            if (packet instanceof Presence) {
                Presence presence = (Presence) packet;
                if (Gateway.this.entityJID.equals(presence.getFrom()) && Gateway.this.roster.contains(presence.getFrom()) && presence.getType().equals(Type.subscribe)) {
                    Packet presence2 = new Presence(Type.subscribed);
                    presence2.setTo(presence.getFrom());
                    presence2.setFrom(StringUtils.parseBareAddress(Gateway.this.connection.getUser()));
                    Gateway.this.connection.sendPacket(presence2);
                }
            }
        }
    }

    Gateway(Connection connection, String str) {
        this.connection = connection;
        this.roster = connection.getRoster();
        this.sdManager = ServiceDiscoveryManager.getInstanceFor(connection);
        this.entityJID = str;
    }

    Gateway(Connection connection, String str, DiscoverInfo discoverInfo, Identity identity) {
        this(connection, str);
        this.info = discoverInfo;
        this.identity = identity;
    }

    private void discoverInfo() throws XMPPException {
        this.info = this.sdManager.discoverInfo(this.entityJID);
        Iterator identities = this.info.getIdentities();
        while (identities.hasNext()) {
            Identity identity = (Identity) identities.next();
            if (identity.getCategory().equalsIgnoreCase("gateway")) {
                this.identity = identity;
                return;
            }
        }
    }

    private Identity getIdentity() throws XMPPException {
        if (this.identity == null) {
            discoverInfo();
        }
        return this.identity;
    }

    private Registration getRegisterInfo() {
        if (this.registerInfo == null) {
            refreshRegisterInfo();
        }
        return this.registerInfo;
    }

    private void refreshRegisterInfo() {
        Packet registration = new Registration();
        registration.setFrom(this.connection.getUser());
        registration.setType(IQ.Type.GET);
        registration.setTo(this.entityJID);
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(registration.getPacketID()));
        this.connection.sendPacket(registration);
        registration = createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if ((registration instanceof Registration) && registration.getError() == null) {
            this.registerInfo = (Registration) registration;
        }
    }

    public boolean canRegister() throws XMPPException {
        if (this.info == null) {
            discoverInfo();
        }
        return this.info.containsFeature("jabber:iq:register");
    }

    public String getField(String str) {
        return getRegisterInfo().getField(str);
    }

    public List<String> getFieldNames() {
        return getRegisterInfo().getFieldNames();
    }

    public String getInstructions() {
        return getRegisterInfo().getInstructions();
    }

    public String getName() throws XMPPException {
        if (this.identity == null) {
            discoverInfo();
        }
        return this.identity.getName();
    }

    public String getPassword() {
        return getField("password");
    }

    public List<String> getRequiredFields() {
        return getRegisterInfo().getRequiredFields();
    }

    public String getType() throws XMPPException {
        if (this.identity == null) {
            discoverInfo();
        }
        return this.identity.getType();
    }

    public String getUsername() {
        return getField("username");
    }

    public boolean isRegistered() throws XMPPException {
        return getRegisterInfo().isRegistered();
    }

    public void login() {
        login(new Presence(Type.available));
    }

    public void login(Presence presence) {
        presence.setType(Type.available);
        presence.setTo(this.entityJID);
        presence.setFrom(this.connection.getUser());
        this.connection.sendPacket(presence);
    }

    public void logout() {
        Packet presence = new Presence(Type.unavailable);
        presence.setTo(this.entityJID);
        presence.setFrom(this.connection.getUser());
        this.connection.sendPacket(presence);
    }

    public void register(String str, String str2) throws XMPPException {
        register(str, str2, new HashMap());
    }

    public void register(String str, String str2, Map<String, String> map) throws XMPPException {
        if (getRegisterInfo().isRegistered()) {
            throw new IllegalStateException("You are already registered with this gateway");
        }
        Packet registration = new Registration();
        registration.setFrom(this.connection.getUser());
        registration.setTo(this.entityJID);
        registration.setType(IQ.Type.SET);
        registration.setUsername(str);
        registration.setPassword(str2);
        for (String str3 : map.keySet()) {
            registration.addAttribute(str3, (String) map.get(str3));
        }
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(registration.getPacketID()));
        this.connection.sendPacket(registration);
        Packet nextResult = createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (nextResult == null || !(nextResult instanceof IQ)) {
            throw new XMPPException("Packet reply timeout");
        }
        IQ iq = (IQ) nextResult;
        if (iq.getError() != null) {
            throw new XMPPException(iq.getError());
        } else if (iq.getType() == IQ.Type.ERROR) {
            throw new XMPPException(iq.getError());
        } else {
            this.connection.addPacketListener(new GatewayPresenceListener(), new PacketTypeFilter(Presence.class));
            this.roster.createEntry(this.entityJID, getIdentity().getName(), new String[0]);
        }
    }

    public void unregister() throws XMPPException {
        Packet registration = new Registration();
        registration.setFrom(this.connection.getUser());
        registration.setTo(this.entityJID);
        registration.setType(IQ.Type.SET);
        registration.setRemove(true);
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(registration.getPacketID()));
        this.connection.sendPacket(registration);
        registration = createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (registration == null || !(registration instanceof IQ)) {
            throw new XMPPException("Packet reply timeout");
        }
        IQ iq = (IQ) registration;
        if (iq.getError() != null) {
            throw new XMPPException(iq.getError());
        } else if (iq.getType() == IQ.Type.ERROR) {
            throw new XMPPException(iq.getError());
        } else {
            this.roster.removeEntry(this.roster.getEntry(this.entityJID));
        }
    }
}
