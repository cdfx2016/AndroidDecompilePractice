package org.jivesoftware.smackx.pubsub.util;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormNode;
import org.jivesoftware.smackx.pubsub.PubSubElementType;

public class NodeUtils {
    public static ConfigureForm getFormFromPacket(Packet packet, PubSubElementType pubSubElementType) {
        return new ConfigureForm(((FormNode) packet.getExtension(pubSubElementType.getElementName(), pubSubElementType.getNamespace().getXmlns())).getForm());
    }
}
