package org.apache.harmony.javax.security.sasl;

import java.util.Map;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;

public interface SaslClientFactory {
    SaslClient createSaslClient(String[] strArr, String str, String str2, String str3, Map<String, ?> map, CallbackHandler callbackHandler) throws SaslException;

    String[] getMechanismNames(Map<String, ?> map);
}
