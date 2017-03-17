package org.jivesoftware.smackx.bytestreams;

import org.jivesoftware.smack.XMPPException;

public interface BytestreamRequest {
    BytestreamSession accept() throws XMPPException, InterruptedException;

    String getFrom();

    String getSessionID();

    void reject();
}
