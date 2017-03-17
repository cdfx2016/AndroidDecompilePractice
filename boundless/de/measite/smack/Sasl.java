package de.measite.smack;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.sasl.SaslClient;
import org.apache.harmony.javax.security.sasl.SaslException;
import org.apache.harmony.javax.security.sasl.SaslServer;
import org.apache.harmony.javax.security.sasl.SaslServerFactory;

public class Sasl {
    private static final String CLIENTFACTORYSRV = "SaslClientFactory";
    public static final String MAX_BUFFER = "javax.security.sasl.maxbuffer";
    public static final String POLICY_FORWARD_SECRECY = "javax.security.sasl.policy.forward";
    public static final String POLICY_NOACTIVE = "javax.security.sasl.policy.noactive";
    public static final String POLICY_NOANONYMOUS = "javax.security.sasl.policy.noanonymous";
    public static final String POLICY_NODICTIONARY = "javax.security.sasl.policy.nodictionary";
    public static final String POLICY_NOPLAINTEXT = "javax.security.sasl.policy.noplaintext";
    public static final String POLICY_PASS_CREDENTIALS = "javax.security.sasl.policy.credentials";
    public static final String QOP = "javax.security.sasl.qop";
    public static final String RAW_SEND_SIZE = "javax.security.sasl.rawsendsize";
    public static final String REUSE = "javax.security.sasl.reuse";
    private static final String SERVERFACTORYSRV = "SaslServerFactory";
    public static final String SERVER_AUTH = "javax.security.sasl.server.authentication";
    public static final String STRENGTH = "javax.security.sasl.strength";

    public static SaslClient createSaslClient(String[] strArr, String str, String str2, String str3, Map<String, ?> map, CallbackHandler callbackHandler) throws SaslException {
        if (strArr == null) {
            throw new NullPointerException("auth.33");
        }
        Object obj;
        SaslClientFactory saslClientFactory = (SaslClientFactory) getSaslClientFactories().nextElement();
        String[] mechanismNames = saslClientFactory.getMechanismNames(null);
        if (mechanismNames != null) {
            obj = null;
            for (String equals : mechanismNames) {
                for (Object equals2 : strArr) {
                    if (equals.equals(equals2)) {
                        obj = 1;
                        break;
                    }
                }
            }
        } else {
            obj = null;
        }
        return obj != null ? saslClientFactory.createSaslClient(strArr, str, str2, str3, map, callbackHandler) : null;
    }

    public static SaslServer createSaslServer(String str, String str2, String str3, Map<String, ?> map, CallbackHandler callbackHandler) throws SaslException {
        return org.apache.harmony.javax.security.sasl.Sasl.createSaslServer(str, str2, str3, map, callbackHandler);
    }

    public static Enumeration<SaslClientFactory> getSaslClientFactories() {
        Hashtable hashtable = new Hashtable();
        hashtable.put(new SaslClientFactory(), new Object());
        return hashtable.keys();
    }

    public static Enumeration<SaslServerFactory> getSaslServerFactories() {
        return org.apache.harmony.javax.security.sasl.Sasl.getSaslServerFactories();
    }
}
