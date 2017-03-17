package org.apache.harmony.javax.security.sasl;

public interface SaslServer {
    void dispose() throws SaslException;

    byte[] evaluateResponse(byte[] bArr) throws SaslException;

    String getAuthorizationID();

    String getMechanismName();

    Object getNegotiatedProperty(String str);

    boolean isComplete();

    byte[] unwrap(byte[] bArr, int i, int i2) throws SaslException;

    byte[] wrap(byte[] bArr, int i, int i2) throws SaslException;
}
