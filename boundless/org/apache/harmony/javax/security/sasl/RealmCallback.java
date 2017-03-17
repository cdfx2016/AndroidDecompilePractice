package org.apache.harmony.javax.security.sasl;

import org.apache.harmony.javax.security.auth.callback.TextInputCallback;

public class RealmCallback extends TextInputCallback {
    private static final long serialVersionUID = -4342673378785456908L;

    public RealmCallback(String str) {
        super(str);
    }

    public RealmCallback(String str, String str2) {
        super(str, str2);
    }
}
