package de.measite.smack;

import com.novell.sasl.client.DigestMD5SaslClient;
import com.novell.sasl.client.ExternalSaslClient;
import java.util.Map;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.sasl.SaslClient;
import org.apache.harmony.javax.security.sasl.SaslException;
import org.apache.qpid.management.common.sasl.Constants;
import org.apache.qpid.management.common.sasl.PlainSaslClient;

public class SaslClientFactory implements org.apache.harmony.javax.security.sasl.SaslClientFactory {
    public SaslClient createSaslClient(String[] strArr, String str, String str2, String str3, Map<String, ?> map, CallbackHandler callbackHandler) throws SaslException {
        for (Object obj : strArr) {
            if (Constants.MECH_PLAIN.equals(obj)) {
                return new PlainSaslClient(str, callbackHandler);
            }
            if ("DIGEST-MD5".equals(obj)) {
                return DigestMD5SaslClient.getClient(str, str2, str3, map, callbackHandler);
            }
            if ("EXTERNAL".equals(obj)) {
                return ExternalSaslClient.getClient(str, str2, str3, map, callbackHandler);
            }
        }
        return null;
    }

    public String[] getMechanismNames(Map<String, ?> map) {
        return new String[]{Constants.MECH_PLAIN, "DIGEST-MD5", "EXTERNAL"};
    }
}
