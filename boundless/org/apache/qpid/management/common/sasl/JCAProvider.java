package org.apache.qpid.management.common.sasl;

import java.security.Provider;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.harmony.javax.security.sasl.SaslClientFactory;

public class JCAProvider extends Provider {
    private static final long serialVersionUID = 1;

    public JCAProvider(Map<String, Class<? extends SaslClientFactory>> map) {
        super("AMQSASLProvider", 1.0d, "A JCA provider that registers all AMQ SASL providers that want to be registered");
        register(map);
    }

    private void register(Map<String, Class<? extends SaslClientFactory>> map) {
        for (Entry entry : map.entrySet()) {
            put("SaslClientFactory." + ((String) entry.getKey()), ((Class) entry.getValue()).getName());
        }
    }
}
