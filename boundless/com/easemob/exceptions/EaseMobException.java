package com.easemob.exceptions;

public class EaseMobException extends Exception {
    private static final long serialVersionUID = 1;

    public EaseMobException(String str) {
        super(str);
    }

    public EaseMobException(String str, Throwable th) {
        super(str);
        super.initCause(th);
    }
}
