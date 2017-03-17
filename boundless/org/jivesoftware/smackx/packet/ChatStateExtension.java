package org.jivesoftware.smackx.packet;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smackx.ChatState;
import org.xmlpull.v1.XmlPullParser;

public class ChatStateExtension implements PacketExtension {
    private ChatState state;

    public static class Provider implements PacketExtensionProvider {
        public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
            ChatState valueOf;
            try {
                valueOf = ChatState.valueOf(xmlPullParser.getName());
            } catch (Exception e) {
                valueOf = ChatState.active;
            }
            return new ChatStateExtension(valueOf);
        }
    }

    public ChatStateExtension(ChatState chatState) {
        this.state = chatState;
    }

    public String getElementName() {
        return this.state.name();
    }

    public String getNamespace() {
        return "http://jabber.org/protocol/chatstates";
    }

    public String toXML() {
        return "<" + getElementName() + " xmlns=\"" + getNamespace() + "\" />";
    }
}
