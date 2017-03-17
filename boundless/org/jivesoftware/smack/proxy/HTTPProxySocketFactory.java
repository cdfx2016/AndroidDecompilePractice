package org.jivesoftware.smack.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.SocketFactory;
import org.jivesoftware.smack.proxy.ProxyInfo.ProxyType;
import org.jivesoftware.smack.util.StringUtils;

class HTTPProxySocketFactory extends SocketFactory {
    private static final Pattern RESPONSE_PATTERN = Pattern.compile("HTTP/\\S+\\s(\\d+)\\s(.*)\\s*");
    private ProxyInfo proxy;

    public HTTPProxySocketFactory(ProxyInfo proxyInfo) {
        this.proxy = proxyInfo;
    }

    private Socket httpProxifiedSocket(String str, int i) throws IOException {
        String proxyAddress = this.proxy.getProxyAddress();
        Socket socket = new Socket(proxyAddress, this.proxy.getProxyPort());
        String str2 = "CONNECT " + str + ":" + i;
        String proxyUsername = this.proxy.getProxyUsername();
        if (proxyUsername == null) {
            proxyUsername = "";
        } else {
            proxyUsername = "\r\nProxy-Authorization: Basic " + StringUtils.encodeBase64(proxyUsername + ":" + this.proxy.getProxyPassword());
        }
        socket.getOutputStream().write((str2 + " HTTP/1.1\r\nHost: " + str2 + proxyUsername + "\r\n\r\n").getBytes("UTF-8"));
        InputStream inputStream = socket.getInputStream();
        StringBuilder stringBuilder = new StringBuilder(100);
        int i2 = 0;
        do {
            char read = (char) inputStream.read();
            stringBuilder.append(read);
            if (stringBuilder.length() > 1024) {
                throw new ProxyException(ProxyType.HTTP, "Recieved header of >1024 characters from " + proxyAddress + ", cancelling connection");
            } else if (read == '￿') {
                throw new ProxyException(ProxyType.HTTP);
            } else if ((i2 == 0 || i2 == 2) && read == '\r') {
                i2++;
                continue;
            } else if ((i2 == 1 || i2 == 3) && read == '\n') {
                i2++;
                continue;
            } else {
                i2 = 0;
                continue;
            }
        } while (i2 != 4);
        if (i2 != 4) {
            throw new ProxyException(ProxyType.HTTP, "Never received blank line from " + proxyAddress + ", cancelling connection");
        }
        Object readLine = new BufferedReader(new StringReader(stringBuilder.toString())).readLine();
        if (readLine == null) {
            throw new ProxyException(ProxyType.HTTP, "Empty proxy response from " + proxyAddress + ", cancelling");
        }
        Matcher matcher = RESPONSE_PATTERN.matcher(readLine);
        if (!matcher.matches()) {
            throw new ProxyException(ProxyType.HTTP, "Unexpected proxy response from " + proxyAddress + ": " + readLine);
        } else if (Integer.parseInt(matcher.group(1)) == 200) {
            return socket;
        } else {
            throw new ProxyException(ProxyType.HTTP);
        }
    }

    public Socket createSocket(String str, int i) throws IOException, UnknownHostException {
        return httpProxifiedSocket(str, i);
    }

    public Socket createSocket(String str, int i, InetAddress inetAddress, int i2) throws IOException, UnknownHostException {
        return httpProxifiedSocket(str, i);
    }

    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        return httpProxifiedSocket(inetAddress.getHostAddress(), i);
    }

    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress2, int i2) throws IOException {
        return httpProxifiedSocket(inetAddress.getHostAddress(), i);
    }
}