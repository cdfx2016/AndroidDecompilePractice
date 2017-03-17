package org.apache.harmony.javax.security.auth.spi;

import java.util.Map;
import org.apache.harmony.javax.security.auth.Subject;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.auth.login.LoginException;

public interface LoginModule {
    boolean abort() throws LoginException;

    boolean commit() throws LoginException;

    void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> map, Map<String, ?> map2);

    boolean login() throws LoginException;

    boolean logout() throws LoginException;
}
