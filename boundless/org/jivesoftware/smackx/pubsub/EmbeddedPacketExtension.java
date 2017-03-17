package org.jivesoftware.smackx.pubsub;

import java.util.List;
import org.jivesoftware.smack.packet.PacketExtension;

public interface EmbeddedPacketExtension extends PacketExtension {
    List<PacketExtension> getExtensions();
}
