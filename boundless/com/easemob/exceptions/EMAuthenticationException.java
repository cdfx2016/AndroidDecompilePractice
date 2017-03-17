package com.easemob.exceptions;

public class EMAuthenticationException extends EaseMobException {
    private static final long serialVersionUID = 1;

    public EMAuthenticationException(String str) {
        super(str);
    }

    public EMAuthenticationException(String str, Throwable th) {
        super(str);
        super.initCause(th);
    }
}
