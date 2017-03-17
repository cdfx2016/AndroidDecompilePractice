package org.jivesoftware.smack;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.jivesoftware.smack.packet.Packet;

class PacketWriter {
    private XMPPConnection connection;
    volatile boolean done;
    private Thread keepAliveThread;
    private final BlockingQueue<Packet> queue = new ArrayBlockingQueue(500, true);
    private Writer writer;
    private Thread writerThread;

    protected PacketWriter(XMPPConnection xMPPConnection) {
        this.connection = xMPPConnection;
        init();
    }

    private Packet nextPacket() {
        Packet packet = null;
        while (!this.done) {
            packet = (Packet) this.queue.poll();
            if (packet != null) {
                break;
            }
            try {
                synchronized (this.queue) {
                    this.queue.wait();
                }
            } catch (InterruptedException e) {
            }
        }
        return packet;
    }

    private void writePackets(Thread thread) {
        try {
            openStream();
            while (!this.done && this.writerThread == thread) {
                Packet nextPacket = nextPacket();
                if (nextPacket != null) {
                    this.writer.write(nextPacket.toXML());
                    if (this.queue.isEmpty()) {
                        this.writer.flush();
                    }
                }
            }
            while (!this.queue.isEmpty()) {
                try {
                    this.writer.write(((Packet) this.queue.remove()).toXML());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.writer.flush();
            this.queue.clear();
            this.writer.write("</stream:stream>");
            this.writer.flush();
            try {
                this.writer.close();
            } catch (Exception e2) {
            }
        } catch (Exception e3) {
            try {
                this.writer.close();
            } catch (Exception e4) {
            }
        } catch (Exception e5) {
            if (!this.done && !this.connection.isSocketClosed()) {
                this.done = true;
                if (this.connection.packetReader != null) {
                    this.connection.notifyConnectionError(e5);
                }
            }
        } catch (Throwable th) {
            try {
                this.writer.close();
            } catch (Exception e6) {
            }
        }
    }

    protected void init() {
        this.writer = this.connection.writer;
        this.done = false;
        this.writerThread = new Thread() {
            public void run() {
                PacketWriter.this.writePackets(this);
            }
        };
        this.writerThread.setName("Smack Packet Writer (" + this.connection.connectionCounterValue + ")");
        this.writerThread.setDaemon(true);
    }

    void openStream() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<stream:stream");
        stringBuilder.append(" to=\"").append(this.connection.getServiceName()).append("\"");
        stringBuilder.append(" xmlns=\"jabber:client\"");
        stringBuilder.append(" xmlns:stream=\"http://etherx.jabber.org/streams\"");
        stringBuilder.append(" version=\"1.0\">");
        this.writer.write(stringBuilder.toString());
        this.writer.flush();
    }

    public void sendPacket(Packet packet) {
        if (!this.done) {
            this.connection.firePacketInterceptors(packet);
            try {
                this.queue.put(packet);
                synchronized (this.queue) {
                    this.queue.notifyAll();
                }
                this.connection.firePacketSendingListeners(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void setWriter(Writer writer) {
        this.writer = writer;
    }

    public void shutdown() {
        this.done = true;
        synchronized (this.queue) {
            this.queue.notifyAll();
        }
        if (this.keepAliveThread != null) {
            this.keepAliveThread.interrupt();
        }
    }

    public void startup() {
        this.writerThread.start();
    }
}
