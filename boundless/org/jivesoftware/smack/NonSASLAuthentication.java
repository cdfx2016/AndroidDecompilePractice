package org.jivesoftware.smack;

import org.apache.harmony.javax.security.auth.callback.Callback;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.auth.callback.PasswordCallback;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.Authentication;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;

class NonSASLAuthentication implements UserAuthentication {
    private Connection connection;

    public NonSASLAuthentication(Connection connection) {
        this.connection = connection;
    }

    public String authenticate(String str, String str2, String str3) throws XMPPException {
        Packet authentication = new Authentication();
        authentication.setType(Type.GET);
        authentication.setUsername(str);
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(authentication.getPacketID()));
        this.connection.sendPacket(authentication);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        if (iq == null) {
            throw new XMPPException("No response from the server.");
        } else if (iq.getType() == Type.ERROR) {
            throw new XMPPException(iq.getError());
        } else {
            Authentication authentication2 = (Authentication) iq;
            createPacketCollector.cancel();
            Packet authentication3 = new Authentication();
            authentication3.setUsername(str);
            if (authentication2.getDigest() != null) {
                authentication3.setDigest(this.connection.getConnectionID(), str2);
            } else if (authentication2.getPassword() != null) {
                authentication3.setPassword(str2);
            } else {
                throw new XMPPException("Server does not support compatible authentication mechanism.");
            }
            authentication3.setResource(str3);
            PacketCollector createPacketCollector2 = this.connection.createPacketCollector(new PacketIDFilter(authentication3.getPacketID()));
            this.connection.sendPacket(authentication3);
            iq = (IQ) createPacketCollector2.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
            if (iq == null) {
                throw new XMPPException("Authentication failed.");
            } else if (iq.getType() == Type.ERROR) {
                throw new XMPPException(iq.getError());
            } else {
                createPacketCollector2.cancel();
                return iq.getTo();
            }
        }
    }

    public String authenticate(String str, String str2, CallbackHandler callbackHandler) throws XMPPException {
        try {
            callbackHandler.handle(new Callback[]{new PasswordCallback("Password: ", false)});
            return authenticate(str, String.valueOf(new PasswordCallback("Password: ", false).getPassword()), str2);
        } catch (Throwable e) {
            throw new XMPPException("Unable to determine password.", e);
        }
    }

    public String authenticateAnonymously() throws XMPPException {
        Packet authentication = new Authentication();
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(authentication.getPacketID()));
        this.connection.sendPacket(authentication);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        if (iq == null) {
            throw new XMPPException("Anonymous login failed.");
        } else if (iq.getType() == Type.ERROR) {
            throw new XMPPException(iq.getError());
        } else {
            createPacketCollector.cancel();
            return iq.getTo() != null ? iq.getTo() : this.connection.getServiceName() + "/" + ((Authentication) iq).getResource();
        }
    }
}
