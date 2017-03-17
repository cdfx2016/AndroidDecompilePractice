package org.jivesoftware.smack;

import cn.finalteam.toolsfinal.io.IOUtils;
import java.net.URI;
import java.net.URISyntaxException;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.proxy.ProxyInfo.ProxyType;

public class BOSHConfiguration extends ConnectionConfiguration {
    private String file;
    private boolean ssl;

    public BOSHConfiguration(String str) {
        super(str, 7070);
        setSASLAuthenticationEnabled(true);
        this.ssl = false;
        this.file = "/http-bind/";
    }

    public BOSHConfiguration(String str, int i) {
        super(str, i);
        setSASLAuthenticationEnabled(true);
        this.ssl = false;
        this.file = "/http-bind/";
    }

    public BOSHConfiguration(boolean z, String str, int i, String str2, String str3) {
        super(str, i, str3);
        setSASLAuthenticationEnabled(true);
        this.ssl = z;
        if (str2 == null) {
            str2 = "/";
        }
        this.file = str2;
    }

    public BOSHConfiguration(boolean z, String str, int i, String str2, ProxyInfo proxyInfo, String str3) {
        super(str, i, str3, proxyInfo);
        setSASLAuthenticationEnabled(true);
        this.ssl = z;
        if (str2 == null) {
            str2 = "/";
        }
        this.file = str2;
    }

    public String getProxyAddress() {
        return this.proxy != null ? this.proxy.getProxyAddress() : null;
    }

    public ProxyInfo getProxyInfo() {
        return this.proxy;
    }

    public int getProxyPort() {
        return this.proxy != null ? this.proxy.getProxyPort() : 8080;
    }

    public URI getURI() throws URISyntaxException {
        if (this.file.charAt(0) != IOUtils.DIR_SEPARATOR_UNIX) {
            this.file = IOUtils.DIR_SEPARATOR_UNIX + this.file;
        }
        return new URI((this.ssl ? "https://" : "http://") + getHost() + ":" + getPort() + this.file);
    }

    public boolean isProxyEnabled() {
        return (this.proxy == null || this.proxy.getProxyType() == ProxyType.NONE) ? false : true;
    }

    public boolean isUsingSSL() {
        return this.ssl;
    }
}
