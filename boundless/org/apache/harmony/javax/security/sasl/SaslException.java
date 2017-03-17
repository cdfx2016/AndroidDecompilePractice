package org.apache.harmony.javax.security.sasl;

import java.io.IOException;

public class SaslException extends IOException {
    private static final long serialVersionUID = 4579784287983423626L;
    private Throwable _exception;

    public SaslException(String str) {
        super(str);
    }

    public SaslException(String str, Throwable th) {
        super(str);
        if (th != null) {
            super.initCause(th);
            this._exception = th;
        }
    }

    public Throwable getCause() {
        return this._exception;
    }

    public Throwable initCause(Throwable th) {
        super.initCause(th);
        this._exception = th;
        return this;
    }

    public String toString() {
        if (this._exception == null) {
            return super.toString();
        }
        StringBuilder stringBuilder = new StringBuilder(super.toString());
        stringBuilder.append(", caused by: ");
        stringBuilder.append(this._exception.toString());
        return stringBuilder.toString();
    }
}
