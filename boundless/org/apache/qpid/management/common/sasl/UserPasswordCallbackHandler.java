package org.apache.qpid.management.common.sasl;

import java.io.IOException;
import org.apache.harmony.javax.security.auth.callback.Callback;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.auth.callback.NameCallback;
import org.apache.harmony.javax.security.auth.callback.PasswordCallback;
import org.apache.harmony.javax.security.auth.callback.UnsupportedCallbackException;

public class UserPasswordCallbackHandler implements CallbackHandler {
    private char[] pwchars;
    private String user;

    public UserPasswordCallbackHandler(String str, String str2) {
        this.user = str;
        this.pwchars = str2.toCharArray();
    }

    private void clearPassword() {
        if (this.pwchars != null) {
            for (int i = 0; i < this.pwchars.length; i++) {
                this.pwchars[i] = '\u0000';
            }
            this.pwchars = null;
        }
    }

    protected void finalize() {
        clearPassword();
    }

    public void handle(Callback[] callbackArr) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbackArr.length; i++) {
            if (callbackArr[i] instanceof NameCallback) {
                ((NameCallback) callbackArr[i]).setName(this.user);
            } else if (callbackArr[i] instanceof PasswordCallback) {
                ((PasswordCallback) callbackArr[i]).setPassword(this.pwchars);
            } else {
                throw new UnsupportedCallbackException(callbackArr[i]);
            }
        }
    }
}
