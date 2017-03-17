package com.novell.sasl.client;

import java.util.Map;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.sasl.SaslClient;
import org.apache.harmony.javax.security.sasl.SaslException;

public class ExternalSaslClient implements SaslClient {
    private static final int STATE_DISPOSED = 3;
    private static final int STATE_INITIAL = 0;
    private static final int STATE_INVALID_SERVER_RESPONSE = 2;
    private static final int STATE_VALID_SERVER_RESPONSE = 1;
    private String m_authorizationId = "";
    private CallbackHandler m_cbh;
    private Map m_props;
    private String m_protocol = "";
    private String m_serverName = "";
    private int m_state;

    private ExternalSaslClient(String str, String str2, String str3, Map map, CallbackHandler callbackHandler) {
        this.m_authorizationId = str;
        this.m_protocol = str2;
        this.m_serverName = str3;
        this.m_props = map;
        this.m_cbh = callbackHandler;
        this.m_state = 0;
    }

    public static SaslClient getClient(String str, String str2, String str3, Map map, CallbackHandler callbackHandler) {
        return new ExternalSaslClient(str, str2, str3, map, callbackHandler);
    }

    public void dispose() throws SaslException {
        if (this.m_state != 3) {
            this.m_state = 3;
        }
    }

    public byte[] evaluateChallenge(byte[] bArr) throws SaslException {
        switch (this.m_state) {
            case 0:
                if (bArr.length != 0) {
                    this.m_state = 2;
                    throw new SaslException("Unexpected non-zero length response.");
                }
                this.m_state = 1;
                return null;
            case 2:
                throw new SaslException("Authentication sequence is complete");
            case 3:
                throw new SaslException("Client has been disposed");
            default:
                throw new SaslException("Unknown client state.");
        }
    }

    public String getMechanismName() {
        return "EXTERNAL";
    }

    public Object getNegotiatedProperty(String str) {
        if (this.m_state == 1) {
            return "javax.security.sasl.qop".equals(str) ? "auth" : null;
        } else {
            throw new IllegalStateException("getNegotiatedProperty: authentication exchange not complete.");
        }
    }

    public boolean hasInitialResponse() {
        return false;
    }

    public boolean isComplete() {
        return this.m_state == 1 || this.m_state == 2 || this.m_state == 3;
    }

    public byte[] unwrap(byte[] bArr, int i, int i2) throws SaslException {
        throw new IllegalStateException("unwrap: QOP has neither integrity nor privacy>");
    }

    public byte[] wrap(byte[] bArr, int i, int i2) throws SaslException {
        throw new IllegalStateException("wrap: QOP has neither integrity nor privacy>");
    }
}
