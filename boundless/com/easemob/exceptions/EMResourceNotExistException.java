package com.easemob.exceptions;

public class EMResourceNotExistException extends EaseMobException {
    private static final long serialVersionUID = 1;

    public EMResourceNotExistException(String str) {
        super(str);
    }

    public EMResourceNotExistException(String str, Throwable th) {
        super(str);
        super.initCause(th);
    }
}
