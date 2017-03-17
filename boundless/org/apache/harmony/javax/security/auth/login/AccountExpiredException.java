package org.apache.harmony.javax.security.auth.login;

public class AccountExpiredException extends AccountException {
    private static final long serialVersionUID = -6064064890162661560L;

    public AccountExpiredException(String str) {
        super(str);
    }
}
