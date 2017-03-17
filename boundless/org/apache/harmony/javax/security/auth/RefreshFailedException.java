package org.apache.harmony.javax.security.auth;

public class RefreshFailedException extends Exception {
    private static final long serialVersionUID = 5058444488565265840L;

    public RefreshFailedException(String str) {
        super(str);
    }
}
