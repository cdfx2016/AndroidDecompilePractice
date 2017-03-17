package org.jivesoftware.smack.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.SocketFactory;

class DirectSocketFactory extends SocketFactory {
    private static int roundrobin = 0;

    public Socket createSocket(String str, int i) throws IOException, UnknownHostException {
        Socket socket = new Socket(Proxy.NO_PROXY);
        InetAddress[] allByName = InetAddress.getAllByName(str);
        int i2 = roundrobin;
        roundrobin = i2 + 1;
        socket.connect(new InetSocketAddress(allByName[i2 % allByName.length], i));
        return socket;
    }

    public Socket createSocket(String str, int i, InetAddress inetAddress, int i2) throws IOException, UnknownHostException {
        return new Socket(str, i, inetAddress, i2);
    }

    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        Socket socket = new Socket(Proxy.NO_PROXY);
        socket.connect(new InetSocketAddress(inetAddress, i));
        return socket;
    }

    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress2, int i2) throws IOException {
        return new Socket(inetAddress, i, inetAddress2, i2);
    }
}
