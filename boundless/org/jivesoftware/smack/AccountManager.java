package org.jivesoftware.smack;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;

public class AccountManager {
    private boolean accountCreationSupported = false;
    private Connection connection;
    private Registration info = null;

    public AccountManager(Connection connection) {
        this.connection = connection;
    }

    private synchronized void getRegistrationInfo() throws XMPPException {
        Packet registration = new Registration();
        registration.setTo(this.connection.getServiceName());
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new AndFilter(new PacketIDFilter(registration.getPacketID()), new PacketTypeFilter(IQ.class)));
        this.connection.sendPacket(registration);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getType() == Type.ERROR) {
            throw new XMPPException(iq.getError());
        } else {
            this.info = (Registration) iq;
        }
    }

    public void changePassword(String str) throws XMPPException {
        Packet registration = new Registration();
        registration.setType(Type.SET);
        registration.setTo(this.connection.getServiceName());
        Map hashMap = new HashMap();
        hashMap.put("username", StringUtils.parseName(this.connection.getUser()));
        hashMap.put("password", str);
        registration.setAttributes(hashMap);
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new AndFilter(new PacketIDFilter(registration.getPacketID()), new PacketTypeFilter(IQ.class)));
        this.connection.sendPacket(registration);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getType() == Type.ERROR) {
            throw new XMPPException(iq.getError());
        }
    }

    public void createAccount(String str, String str2) throws XMPPException {
        if (supportsAccountCreation()) {
            Map hashMap = new HashMap();
            for (String put : getAccountAttributes()) {
                hashMap.put(put, "");
            }
            createAccount(str, str2, hashMap);
            return;
        }
        throw new XMPPException("Server does not support account creation.");
    }

    public void createAccount(String str, String str2, Map<String, String> map) throws XMPPException {
        if (supportsAccountCreation()) {
            Packet registration = new Registration();
            registration.setType(Type.SET);
            registration.setTo(this.connection.getServiceName());
            map.put("username", str);
            map.put("password", str2);
            registration.setAttributes(map);
            PacketCollector createPacketCollector = this.connection.createPacketCollector(new AndFilter(new PacketIDFilter(registration.getPacketID()), new PacketTypeFilter(IQ.class)));
            this.connection.sendPacket(registration);
            IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
            createPacketCollector.cancel();
            if (iq == null) {
                throw new XMPPException("No response from server.");
            } else if (iq.getType() == Type.ERROR) {
                throw new XMPPException(iq.getError());
            } else {
                return;
            }
        }
        throw new XMPPException("Server does not support account creation.");
    }

    public void deleteAccount() throws XMPPException {
        if (this.connection.isAuthenticated()) {
            Packet registration = new Registration();
            registration.setType(Type.SET);
            registration.setTo(this.connection.getServiceName());
            Map hashMap = new HashMap();
            hashMap.put(Item.REMOVE_ACTION, "");
            registration.setAttributes(hashMap);
            PacketCollector createPacketCollector = this.connection.createPacketCollector(new AndFilter(new PacketIDFilter(registration.getPacketID()), new PacketTypeFilter(IQ.class)));
            this.connection.sendPacket(registration);
            IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
            createPacketCollector.cancel();
            if (iq == null) {
                throw new XMPPException("No response from server.");
            } else if (iq.getType() == Type.ERROR) {
                throw new XMPPException(iq.getError());
            } else {
                return;
            }
        }
        throw new IllegalStateException("Must be logged in to delete a account.");
    }

    public String getAccountAttribute(String str) {
        try {
            if (this.info == null) {
                getRegistrationInfo();
            }
            return (String) this.info.getAttributes().get(str);
        } catch (XMPPException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Collection<String> getAccountAttributes() {
        try {
            if (this.info == null) {
                getRegistrationInfo();
            }
            Map attributes = this.info.getAttributes();
            if (attributes != null) {
                return Collections.unmodifiableSet(attributes.keySet());
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return Collections.emptySet();
    }

    public String getAccountInstructions() {
        try {
            if (this.info == null) {
                getRegistrationInfo();
            }
            return this.info.getInstructions();
        } catch (XMPPException e) {
            return null;
        }
    }

    void setSupportsAccountCreation(boolean z) {
        this.accountCreationSupported = z;
    }

    public boolean supportsAccountCreation() {
        boolean z = true;
        if (this.accountCreationSupported) {
            return true;
        }
        try {
            if (this.info == null) {
                getRegistrationInfo();
                if (this.info.getType() == Type.ERROR) {
                    z = false;
                }
                this.accountCreationSupported = z;
            }
            return this.accountCreationSupported;
        } catch (XMPPException e) {
            return false;
        }
    }
}
