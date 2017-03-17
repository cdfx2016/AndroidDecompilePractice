package org.jivesoftware.smack.sasl;

import org.apache.qpid.management.common.sasl.Constants;
import org.jivesoftware.smack.SASLAuthentication;

public class SASLPlainMechanism extends SASLMechanism {
    public SASLPlainMechanism(SASLAuthentication sASLAuthentication) {
        super(sASLAuthentication);
    }

    protected String getName() {
        return Constants.MECH_PLAIN;
    }
}
