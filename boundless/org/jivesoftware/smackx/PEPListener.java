package org.jivesoftware.smackx;

import org.jivesoftware.smackx.packet.PEPEvent;

public interface PEPListener {
    void eventReceived(String str, PEPEvent pEPEvent);
}
