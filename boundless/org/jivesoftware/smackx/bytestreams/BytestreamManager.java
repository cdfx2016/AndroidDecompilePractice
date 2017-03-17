package org.jivesoftware.smackx.bytestreams;

import java.io.IOException;
import org.jivesoftware.smack.XMPPException;

public interface BytestreamManager {
    void addIncomingBytestreamListener(BytestreamListener bytestreamListener);

    void addIncomingBytestreamListener(BytestreamListener bytestreamListener, String str);

    BytestreamSession establishSession(String str) throws XMPPException, IOException, InterruptedException;

    BytestreamSession establishSession(String str, String str2) throws XMPPException, IOException, InterruptedException;

    void removeIncomingBytestreamListener(String str);

    void removeIncomingBytestreamListener(BytestreamListener bytestreamListener);
}
