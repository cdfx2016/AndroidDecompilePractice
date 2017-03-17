package org.jivesoftware.smack.sasl;

import org.jivesoftware.smack.SASLAuthentication;

public class SASLDigestMD5Mechanism extends SASLMechanism {
    public SASLDigestMD5Mechanism(SASLAuthentication sASLAuthentication) {
        super(sASLAuthentication);
    }

    protected String getName() {
        return "DIGEST-MD5";
    }
}
