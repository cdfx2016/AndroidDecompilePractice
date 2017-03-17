package org.jivesoftware.smackx.bytestreams.ibb;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Close;

class CloseListener implements PacketListener {
    private final PacketFilter closeFilter = new AndFilter(new PacketTypeFilter(Close.class), new IQTypeFilter(Type.SET));
    private final InBandBytestreamManager manager;

    protected CloseListener(InBandBytestreamManager inBandBytestreamManager) {
        this.manager = inBandBytestreamManager;
    }

    protected PacketFilter getFilter() {
        return this.closeFilter;
    }

    public void processPacket(Packet packet) {
        Close close = (Close) packet;
        InBandBytestreamSession inBandBytestreamSession = (InBandBytestreamSession) this.manager.getSessions().get(close.getSessionID());
        if (inBandBytestreamSession == null) {
            this.manager.replyItemNotFoundPacket(close);
            return;
        }
        inBandBytestreamSession.closeByPeer(close);
        this.manager.getSessions().remove(close.getSessionID());
    }
}
