package org.apache.harmony.javax.security.sasl;

public interface SaslClient {
    void dispose() throws SaslException;

    byte[] evaluateChallenge(byte[] bArr) throws SaslException;

    String getMechanismName();

    Object getNegotiatedProperty(String str);

    boolean hasInitialResponse();

    boolean isComplete();

    byte[] unwrap(byte[] bArr, int i, int i2) throws SaslException;

    byte[] wrap(byte[] bArr, int i, int i2) throws SaslException;
}
