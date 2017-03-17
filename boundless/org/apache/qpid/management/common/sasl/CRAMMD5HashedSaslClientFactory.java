package org.apache.qpid.management.common.sasl;

import de.measite.smack.Sasl;
import java.util.Map;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.sasl.SaslClient;
import org.apache.harmony.javax.security.sasl.SaslClientFactory;
import org.apache.harmony.javax.security.sasl.SaslException;

public class CRAMMD5HashedSaslClientFactory implements SaslClientFactory {
    public static final String MECHANISM = "CRAM-MD5-HASHED";

    public SaslClient createSaslClient(String[] strArr, String str, String str2, String str3, Map<String, ?> map, CallbackHandler callbackHandler) throws SaslException {
        int i = 0;
        while (i < strArr.length) {
            if (!strArr[i].equals(MECHANISM)) {
                i++;
            } else if (callbackHandler == null) {
                throw new SaslException("CallbackHandler must not be null");
            } else {
                return Sasl.createSaslClient(new String[]{Constants.MECH_CRAMMD5}, str, str2, str3, map, callbackHandler);
            }
        }
        return null;
    }

    public String[] getMechanismNames(Map map) {
        return new String[]{MECHANISM};
    }
}
