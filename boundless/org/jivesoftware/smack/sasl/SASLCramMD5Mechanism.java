package org.jivesoftware.smack.sasl;

import org.apache.qpid.management.common.sasl.Constants;
import org.jivesoftware.smack.SASLAuthentication;

public class SASLCramMD5Mechanism extends SASLMechanism {
    public SASLCramMD5Mechanism(SASLAuthentication sASLAuthentication) {
        super(sASLAuthentication);
    }

    protected String getName() {
        return Constants.MECH_CRAMMD5;
    }
}
