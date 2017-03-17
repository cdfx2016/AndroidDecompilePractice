package org.apache.qpid.management.common.sasl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.harmony.javax.security.auth.callback.Callback;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.auth.callback.NameCallback;
import org.apache.harmony.javax.security.auth.callback.PasswordCallback;
import org.apache.harmony.javax.security.auth.callback.UnsupportedCallbackException;

public class UsernameHashedPasswordCallbackHandler implements CallbackHandler {
    private char[] pwchars;
    private String user;

    public UsernameHashedPasswordCallbackHandler(String str, String str2) throws Exception {
        this.user = str;
        this.pwchars = getHash(str2);
    }

    private void clearPassword() {
        if (this.pwchars != null) {
            for (int i = 0; i < this.pwchars.length; i++) {
                this.pwchars[i] = '\u0000';
            }
            this.pwchars = null;
        }
    }

    public static char[] getHash(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        int i = 0;
        byte[] bytes = str.getBytes("utf-8");
        MessageDigest instance = MessageDigest.getInstance("MD5");
        for (byte update : bytes) {
            instance.update(update);
        }
        byte[] digest = instance.digest();
        char[] cArr = new char[digest.length];
        int length = digest.length;
        int i2 = 0;
        while (i < length) {
            int i3 = i2 + 1;
            cArr[i2] = (char) digest[i];
            i++;
            i2 = i3;
        }
        return cArr;
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
