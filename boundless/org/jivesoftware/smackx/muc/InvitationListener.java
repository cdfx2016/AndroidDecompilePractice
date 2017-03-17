package org.jivesoftware.smackx.muc;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;

public interface InvitationListener {
    void invitationReceived(Connection connection, String str, String str2, String str3, String str4, Message message);
}
