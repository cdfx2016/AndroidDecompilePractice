package org.apache.harmony.javax.security.sasl;

import org.apache.harmony.javax.security.auth.callback.ChoiceCallback;

public class RealmChoiceCallback extends ChoiceCallback {
    private static final long serialVersionUID = -8588141348846281332L;

    public RealmChoiceCallback(String str, String[] strArr, int i, boolean z) {
        super(str, strArr, i, z);
    }
}
