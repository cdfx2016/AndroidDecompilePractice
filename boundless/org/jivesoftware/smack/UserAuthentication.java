package org.jivesoftware.smack;

import org.apache.harmony.javax.security.auth.callback.CallbackHandler;

interface UserAuthentication {
    String authenticate(String str, String str2, String str3) throws XMPPException;

    String authenticate(String str, String str2, CallbackHandler callbackHandler) throws XMPPException;

    String authenticateAnonymously() throws XMPPException;
}
