package org.apache.qpid.management.common.sasl;

import java.util.Map;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.sasl.SaslClient;
import org.apache.harmony.javax.security.sasl.SaslClientFactory;
import org.apache.harmony.javax.security.sasl.SaslException;

public class ClientSaslFactory implements SaslClientFactory {
    public SaslClient createSaslClient(String[] strArr, String str, String str2, String str3, Map map, CallbackHandler callbackHandler) throws SaslException {
        for (String equals : strArr) {
            if (equals.equals(Constants.MECH_PLAIN)) {
                return new PlainSaslClient(str, callbackHandler);
            }
        }
        return null;
    }

    public String[] getMechanismNames(Map map) {
        return new String[]{Constants.MECH_PLAIN};
    }
}
