package org.jivesoftware.smack.sasl;

import org.jivesoftware.smack.SASLAuthentication;

public class SASLExternalMechanism extends SASLMechanism {
    public SASLExternalMechanism(SASLAuthentication sASLAuthentication) {
        super(sASLAuthentication);
    }

    protected String getName() {
        return "EXTERNAL";
    }
}
