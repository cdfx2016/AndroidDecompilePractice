package org.jivesoftware.smackx.muc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.packet.Packet;

class ConnectionDetachedPacketCollector {
    private int maxPackets;
    private ArrayBlockingQueue<Packet> resultQueue;

    public ConnectionDetachedPacketCollector() {
        this(SmackConfiguration.getPacketCollectorSize());
    }

    public ConnectionDetachedPacketCollector(int i) {
        this.maxPackets = SmackConfiguration.getPacketCollectorSize();
        this.resultQueue = new ArrayBlockingQueue(i);
    }

    public Packet nextResult() {
        try {
            return (Packet) this.resultQueue.take();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Packet nextResult(long j) {
        try {
            return (Packet) this.resultQueue.poll(j, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Packet pollResult() {
        return (Packet) this.resultQueue.poll();
    }

    protected void processPacket(Packet packet) {
        if (packet != null) {
            while (!this.resultQueue.offer(packet)) {
                this.resultQueue.poll();
            }
        }
    }
}
