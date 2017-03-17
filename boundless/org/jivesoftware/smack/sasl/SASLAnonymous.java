package org.jivesoftware.smack.sasl;

import java.io.IOException;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.sasl.SASLMechanism.AuthMechanism;
import org.jivesoftware.smack.sasl.SASLMechanism.Response;

public class SASLAnonymous extends SASLMechanism {
    public SASLAnonymous(SASLAuthentication sASLAuthentication) {
        super(sASLAuthentication);
    }

    protected void authenticate() throws IOException {
        getSASLAuthentication().send(new AuthMechanism(getName(), null));
    }

    public void authenticate(String str, String str2, String str3) throws IOException {
        authenticate();
    }

    public void authenticate(String str, String str2, CallbackHandler callbackHandler) throws IOException {
        authenticate();
    }

    public void challengeReceived(String str) throws IOException {
        getSASLAuthentication().send(new Response());
    }

    protected String getName() {
        return "ANONYMOUS";
    }
}
