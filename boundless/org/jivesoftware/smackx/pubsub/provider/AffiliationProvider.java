package org.jivesoftware.smackx.pubsub.provider;

import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.Affiliation.Type;

public class AffiliationProvider extends EmbeddedExtensionProvider {
    protected PacketExtension createReturnExtension(String str, String str2, Map<String, String> map, List<? extends PacketExtension> list) {
        return new Affiliation((String) map.get("jid"), (String) map.get("node"), Type.valueOf((String) map.get("affiliation")));
    }
}
