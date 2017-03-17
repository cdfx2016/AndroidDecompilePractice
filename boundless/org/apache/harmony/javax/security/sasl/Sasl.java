package org.apache.harmony.javax.security.sasl;

import java.security.Provider;
import java.security.Security;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;

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

    private Sasl() {
    }

    public static SaslClient createSaslClient(String[] strArr, String str, String str2, String str3, Map<String, ?> map, CallbackHandler callbackHandler) throws SaslException {
        if (strArr == null) {
            throw new NullPointerException("auth.33");
        }
        Collection<SaslClientFactory> findFactories = findFactories(CLIENTFACTORYSRV);
        if (findFactories.isEmpty()) {
            return null;
        }
        for (SaslClientFactory saslClientFactory : findFactories) {
            Object obj;
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
            if (obj != null) {
                SaslClient createSaslClient = saslClientFactory.createSaslClient(strArr, str, str2, str3, map, callbackHandler);
                if (createSaslClient != null) {
                    return createSaslClient;
                }
            }
        }
        return null;
    }

    public static SaslServer createSaslServer(String str, String str2, String str3, Map<String, ?> map, CallbackHandler callbackHandler) throws SaslException {
        if (str == null) {
            throw new NullPointerException("auth.32");
        }
        Collection<SaslServerFactory> findFactories = findFactories(SERVERFACTORYSRV);
        if (findFactories.isEmpty()) {
            return null;
        }
        for (SaslServerFactory saslServerFactory : findFactories) {
            Object obj;
            String[] mechanismNames = saslServerFactory.getMechanismNames(null);
            if (mechanismNames != null) {
                for (String equals : mechanismNames) {
                    if (equals.equals(str)) {
                        obj = 1;
                        break;
                    }
                }
            }
            obj = null;
            if (obj != null) {
                SaslServer createSaslServer = saslServerFactory.createSaslServer(str, str2, str3, map, callbackHandler);
                if (createSaslServer != null) {
                    return createSaslServer;
                }
            }
        }
        return null;
    }

    private static Collection<?> findFactories(String str) {
        HashSet hashSet = new HashSet();
        Provider[] providers = Security.getProviders();
        if (providers == null || providers.length == 0) {
            return hashSet;
        }
        HashSet hashSet2 = new HashSet();
        for (int i = 0; i < providers.length; i++) {
            String name = providers[i].getName();
            Enumeration keys = providers[i].keys();
            while (keys.hasMoreElements()) {
                String str2 = (String) keys.nextElement();
                if (str2.startsWith(str)) {
                    str2 = providers[i].getProperty(str2);
                    try {
                        if (hashSet2.add(name.concat(str2))) {
                            hashSet.add(newInstance(str2, providers[i]));
                        }
                    } catch (SaslException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return hashSet;
    }

    public static Enumeration<SaslClientFactory> getSaslClientFactories() {
        return Collections.enumeration(findFactories(CLIENTFACTORYSRV));
    }

    public static Enumeration<SaslServerFactory> getSaslServerFactories() {
        return Collections.enumeration(findFactories(SERVERFACTORYSRV));
    }

    private static Object newInstance(String str, Provider provider) throws SaslException {
        String str2 = "auth.31";
        ClassLoader classLoader = provider.getClass().getClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        try {
            return Class.forName(str, true, classLoader).newInstance();
        } catch (Throwable e) {
            throw new SaslException(str2 + str, e);
        } catch (Throwable e2) {
            throw new SaslException(str2 + str, e2);
        } catch (Throwable e22) {
            throw new SaslException(str2 + str, e22);
        }
    }
}
