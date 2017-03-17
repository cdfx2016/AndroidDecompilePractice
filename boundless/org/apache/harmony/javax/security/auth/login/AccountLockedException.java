package org.apache.harmony.javax.security.auth.login;

public class AccountLockedException extends AccountException {
    private static final long serialVersionUID = 8280345554014066334L;

    public AccountLockedException(String str) {
        super(str);
    }
}
