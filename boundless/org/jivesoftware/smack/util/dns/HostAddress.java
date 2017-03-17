package org.jivesoftware.smack.util.dns;

import cn.finalteam.toolsfinal.io.FilenameUtils;

public class HostAddress {
    private Exception exception;
    private String fqdn;
    private int port;

    public HostAddress(String str) {
        if (str == null) {
            throw new IllegalArgumentException("FQDN is null");
        }
        if (str.charAt(str.length() - 1) == FilenameUtils.EXTENSION_SEPARATOR) {
            this.fqdn = str.substring(0, str.length() - 1);
        } else {
            this.fqdn = str;
        }
        this.port = 5222;
    }

    public HostAddress(String str, int i) {
        this(str);
        if (i < 0 || i > 65535) {
            throw new IllegalArgumentException("DNS SRV records weight must be a 16-bit unsiged integer (i.e. between 0-65535. Port was: " + i);
        }
        this.port = i;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HostAddress)) {
            return false;
        }
        HostAddress hostAddress = (HostAddress) obj;
        return !this.fqdn.equals(hostAddress.fqdn) ? false : this.port == hostAddress.port;
    }

    public String getErrorMessage() {
        return toString() + " Exception: " + (this.exception == null ? "No error logged" : this.exception.getMessage());
    }

    public String getFQDN() {
        return this.fqdn;
    }

    public int getPort() {
        return this.port;
    }

    public int hashCode() {
        return ((this.fqdn.hashCode() + 37) * 37) + this.port;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String toString() {
        return this.fqdn + ":" + this.port;
    }
}
