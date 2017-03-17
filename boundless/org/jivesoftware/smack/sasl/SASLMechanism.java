package org.jivesoftware.smack.sasl;

import de.measite.smack.Sasl;
import java.io.IOException;
import java.util.HashMap;
import org.apache.harmony.javax.security.auth.callback.Callback;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.auth.callback.NameCallback;
import org.apache.harmony.javax.security.auth.callback.PasswordCallback;
import org.apache.harmony.javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.harmony.javax.security.sasl.RealmCallback;
import org.apache.harmony.javax.security.sasl.RealmChoiceCallback;
import org.apache.harmony.javax.security.sasl.SaslClient;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

public abstract class SASLMechanism implements CallbackHandler {
    protected String authenticationId;
    protected String hostname;
    protected String password;
    private SASLAuthentication saslAuthentication;
    protected SaslClient sc;

    public class AuthMechanism extends Packet {
        private final String authenticationText;
        private final String name;

        public AuthMechanism(String str, String str2) {
            if (str == null) {
                throw new NullPointerException("SASL mechanism name shouldn't be null.");
            }
            this.name = str;
            this.authenticationText = str2;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<auth mechanism=\"").append(this.name);
            stringBuilder.append("\" xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">");
            if (this.authenticationText != null && this.authenticationText.trim().length() > 0) {
                stringBuilder.append(this.authenticationText);
            }
            stringBuilder.append("</auth>");
            return stringBuilder.toString();
        }
    }

    public static class Challenge extends Packet {
        private final String data;

        public Challenge(String str) {
            this.data = str;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<challenge xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">");
            if (this.data != null && this.data.trim().length() > 0) {
                stringBuilder.append(this.data);
            }
            stringBuilder.append("</challenge>");
            return stringBuilder.toString();
        }
    }

    public static class Failure extends Packet {
        private final String condition;

        public Failure(String str) {
            this.condition = str;
        }

        public String getCondition() {
            return this.condition;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<failure xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">");
            if (this.condition != null && this.condition.trim().length() > 0) {
                stringBuilder.append("<").append(this.condition).append("/>");
            }
            stringBuilder.append("</failure>");
            return stringBuilder.toString();
        }
    }

    public class Response extends Packet {
        private final String authenticationText;

        public Response() {
            this.authenticationText = null;
        }

        public Response(String str) {
            if (str == null || str.trim().length() == 0) {
                this.authenticationText = null;
            } else {
                this.authenticationText = str;
            }
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<response xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">");
            if (this.authenticationText != null) {
                stringBuilder.append(this.authenticationText);
            }
            stringBuilder.append("</response>");
            return stringBuilder.toString();
        }
    }

    public static class Success extends Packet {
        private final String data;

        public Success(String str) {
            this.data = str;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<success xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">");
            if (this.data != null && this.data.trim().length() > 0) {
                stringBuilder.append(this.data);
            }
            stringBuilder.append("</success>");
            return stringBuilder.toString();
        }
    }

    public SASLMechanism(SASLAuthentication sASLAuthentication) {
        this.saslAuthentication = sASLAuthentication;
    }

    protected void authenticate() throws IOException, XMPPException {
        String str = null;
        try {
            if (this.sc.hasInitialResponse()) {
                str = StringUtils.encodeBase64(this.sc.evaluateChallenge(new byte[0]), false);
            }
            getSASLAuthentication().send(new AuthMechanism(getName(), str));
        } catch (Throwable e) {
            throw new XMPPException("SASL authentication failed", e);
        }
    }

    public void authenticate(String str, String str2, String str3) throws IOException, XMPPException {
        this.authenticationId = str;
        this.password = str3;
        this.hostname = str2;
        String str4 = str;
        this.sc = Sasl.createSaslClient(new String[]{getName()}, str4, "xmpp", str2, new HashMap(), this);
        authenticate();
    }

    public void authenticate(String str, String str2, CallbackHandler callbackHandler) throws IOException, XMPPException {
        String str3 = str;
        this.sc = Sasl.createSaslClient(new String[]{getName()}, str3, "xmpp", str2, new HashMap(), callbackHandler);
        authenticate();
    }

    public void challengeReceived(String str) throws IOException {
        byte[] evaluateChallenge = str != null ? this.sc.evaluateChallenge(StringUtils.decodeBase64(str)) : this.sc.evaluateChallenge(new byte[0]);
        getSASLAuthentication().send(evaluateChallenge == null ? new Response() : new Response(StringUtils.encodeBase64(evaluateChallenge, false)));
    }

    protected abstract String getName();

    protected SASLAuthentication getSASLAuthentication() {
        return this.saslAuthentication;
    }

    public void handle(Callback[] callbackArr) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbackArr.length; i++) {
            if (callbackArr[i] instanceof NameCallback) {
                ((NameCallback) callbackArr[i]).setName(this.authenticationId);
            } else if (callbackArr[i] instanceof PasswordCallback) {
                ((PasswordCallback) callbackArr[i]).setPassword(this.password.toCharArray());
            } else if (callbackArr[i] instanceof RealmCallback) {
                ((RealmCallback) callbackArr[i]).setText(this.hostname);
            } else if (!(callbackArr[i] instanceof RealmChoiceCallback)) {
                throw new UnsupportedCallbackException(callbackArr[i]);
            }
        }
    }
}
