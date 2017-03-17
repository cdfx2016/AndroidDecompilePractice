package com.easemob.exceptions;

public class EMPermissionException extends EaseMobException {
    private static final long serialVersionUID = 1;

    public EMPermissionException(String str) {
        super(str);
    }

    public EMPermissionException(String str, Throwable th) {
        super(str);
        super.initCause(th);
    }
}
