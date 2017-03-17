package org.apache.harmony.javax.security.sasl;

public class AuthenticationException extends SaslException {
    private static final long serialVersionUID = -3579708765071815007L;

    public AuthenticationException(String str) {
        super(str);
    }

    public AuthenticationException(String str, Throwable th) {
        super(str, th);
    }
}
