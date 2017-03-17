package org.jivesoftware.smackx.entitycaps.cache;

import java.io.IOException;
import org.jivesoftware.smackx.packet.DiscoverInfo;

public interface EntityCapsPersistentCache {
    void addDiscoverInfoByNodePersistent(String str, DiscoverInfo discoverInfo);

    void emptyCache();

    void replay() throws IOException;
}
