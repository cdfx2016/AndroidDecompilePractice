package com.easemob.exceptions;

public class EMDuplicateResourceException extends EaseMobException {
    private static final long serialVersionUID = 1;

    public EMDuplicateResourceException(String str) {
        super(str);
    }

    public EMDuplicateResourceException(String str, Throwable th) {
        super(str);
        super.initCause(th);
    }
}
