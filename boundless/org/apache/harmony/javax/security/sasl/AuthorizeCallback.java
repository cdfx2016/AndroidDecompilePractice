package org.apache.harmony.javax.security.sasl;

import java.io.Serializable;
import org.apache.harmony.javax.security.auth.callback.Callback;

public class AuthorizeCallback implements Callback, Serializable {
    private static final long serialVersionUID = -2353344186490470805L;
    private final String authenticationID;
    private final String authorizationID;
    private boolean authorized;
    private String authorizedID;

    public AuthorizeCallback(String str, String str2) {
        this.authenticationID = str;
        this.authorizationID = str2;
        this.authorizedID = str2;
    }

    public String getAuthenticationID() {
        return this.authenticationID;
    }

    public String getAuthorizationID() {
        return this.authorizationID;
    }

    public String getAuthorizedID() {
        return this.authorized ? this.authorizedID : null;
    }

    public boolean isAuthorized() {
        return this.authorized;
    }

    public void setAuthorized(boolean z) {
        this.authorized = z;
    }

    public void setAuthorizedID(String str) {
        if (str != null) {
            this.authorizedID = str;
        }
    }
}
