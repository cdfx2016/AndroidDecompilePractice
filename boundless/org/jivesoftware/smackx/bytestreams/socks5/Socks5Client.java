package org.jivesoftware.smackx.bytestreams.socks5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHost;

class Socks5Client {
    protected String digest;
    protected StreamHost streamHost;

    public Socks5Client(StreamHost streamHost, String str) {
        this.streamHost = streamHost;
        this.digest = str;
    }

    private byte[] createSocks5ConnectRequest() {
        Object bytes = this.digest.getBytes();
        Object obj = new byte[(bytes.length + 7)];
        obj[0] = 5;
        obj[1] = 1;
        obj[2] = null;
        obj[3] = 3;
        obj[4] = (byte) bytes.length;
        System.arraycopy(bytes, 0, obj, 5, bytes.length);
        obj[obj.length - 2] = null;
        obj[obj.length - 1] = null;
        return obj;
    }

    protected boolean establish(Socket socket) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataOutputStream.write(new byte[]{(byte) 5, (byte) 1, (byte) 0});
        dataOutputStream.flush();
        byte[] bArr = new byte[2];
        dataInputStream.readFully(bArr);
        if (bArr[0] != (byte) 5 || bArr[1] != (byte) 0) {
            return false;
        }
        bArr = createSocks5ConnectRequest();
        dataOutputStream.write(bArr);
        dataOutputStream.flush();
        try {
            byte[] receiveSocks5Message = Socks5Utils.receiveSocks5Message(dataInputStream);
            bArr[1] = (byte) 0;
            return Arrays.equals(bArr, receiveSocks5Message);
        } catch (XMPPException e) {
            return false;
        }
    }

    public Socket getSocket(int i) throws IOException, XMPPException, InterruptedException, TimeoutException {
        Object futureTask = new FutureTask(new Callable<Socket>() {
            public Socket call() throws Exception {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(Socks5Client.this.streamHost.getAddress(), Socks5Client.this.streamHost.getPort()));
                if (Socks5Client.this.establish(socket)) {
                    return socket;
                }
                socket.close();
                throw new XMPPException("establishing connection to SOCKS5 proxy failed");
            }
        });
        new Thread(futureTask).start();
        try {
            return (Socket) futureTask.get((long) i, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                if (cause instanceof IOException) {
                    throw ((IOException) cause);
                } else if (cause instanceof XMPPException) {
                    throw ((XMPPException) cause);
                }
            }
            throw new IOException("Error while connection to SOCKS5 proxy");
        }
    }
}
