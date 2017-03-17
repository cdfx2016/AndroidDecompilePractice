package org.apache.harmony.javax.security.auth.login;

public class CredentialExpiredException extends CredentialException {
    private static final long serialVersionUID = -5344739593859737937L;

    public CredentialExpiredException(String str) {
        super(str);
    }
}
