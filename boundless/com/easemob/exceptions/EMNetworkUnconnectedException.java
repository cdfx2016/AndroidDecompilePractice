package com.easemob.exceptions;

public class EMNetworkUnconnectedException extends EaseMobException {
    private static final long serialVersionUID = 1;

    public EMNetworkUnconnectedException(String str) {
        super(str);
    }

    public EMNetworkUnconnectedException(String str, Throwable th) {
        super(str);
        super.initCause(th);
    }
}
