package org.jivesoftware.smackx.bytestreams.ibb.packet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

public class Data extends IQ {
    private final DataPacketExtension dataPacketExtension;

    public Data(DataPacketExtension dataPacketExtension) {
        if (dataPacketExtension == null) {
            throw new IllegalArgumentException("Data must not be null");
        }
        this.dataPacketExtension = dataPacketExtension;
        addExtension(dataPacketExtension);
        setType(Type.SET);
    }

    public String getChildElementXML() {
        return this.dataPacketExtension.toXML();
    }

    public DataPacketExtension getDataPacketExtension() {
        return this.dataPacketExtension;
    }
}
