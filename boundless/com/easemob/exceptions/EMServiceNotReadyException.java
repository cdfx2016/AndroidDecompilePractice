package com.easemob.exceptions;

public class EMServiceNotReadyException extends EaseMobException {
    private static final long serialVersionUID = 1;

    public EMServiceNotReadyException(String str) {
        super(str);
    }

    public EMServiceNotReadyException(String str, Throwable th) {
        super(str);
        super.initCause(th);
    }
}
