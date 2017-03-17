package org.apache.harmony.javax.security.auth.login;

import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import org.apache.harmony.javax.security.auth.AuthPermission;
import org.apache.harmony.javax.security.auth.Subject;
import org.apache.harmony.javax.security.auth.callback.Callback;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.harmony.javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import org.apache.harmony.javax.security.auth.spi.LoginModule;

public class LoginContext {
    private static final String DEFAULT_CALLBACK_HANDLER_PROPERTY = "auth.login.defaultCallbackHandler";
    private static final int OPTIONAL = 0;
    private static final int REQUIRED = 1;
    private static final int REQUISITE = 2;
    private static final int SUFFICIENT = 3;
    private CallbackHandler callbackHandler;
    private ClassLoader contextClassLoader;
    private boolean loggedIn;
    private Module[] modules;
    private Map<String, ?> sharedState;
    private Subject subject;
    private AccessControlContext userContext;
    private boolean userProvidedConfig;
    private boolean userProvidedSubject;

    private class ContextedCallbackHandler implements CallbackHandler {
        private final CallbackHandler hiddenHandlerRef;

        ContextedCallbackHandler(CallbackHandler callbackHandler) {
            this.hiddenHandlerRef = callbackHandler;
        }

        public void handle(final Callback[] callbackArr) throws IOException, UnsupportedCallbackException {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                    public Void run() throws IOException, UnsupportedCallbackException {
                        ContextedCallbackHandler.this.hiddenHandlerRef.handle(callbackArr);
                        return null;
                    }
                }, LoginContext.this.userContext);
            } catch (PrivilegedActionException e) {
                if (e.getCause() instanceof UnsupportedCallbackException) {
                    throw ((UnsupportedCallbackException) e.getCause());
                }
                throw ((IOException) e.getCause());
            }
        }
    }

    private final class Module {
        AppConfigurationEntry entry;
        int flag;
        Class<?> klass;
        LoginModule module;

        Module(AppConfigurationEntry appConfigurationEntry) {
            this.entry = appConfigurationEntry;
            LoginModuleControlFlag controlFlag = appConfigurationEntry.getControlFlag();
            if (controlFlag == LoginModuleControlFlag.OPTIONAL) {
                this.flag = 0;
            } else if (controlFlag == LoginModuleControlFlag.REQUISITE) {
                this.flag = 2;
            } else if (controlFlag == LoginModuleControlFlag.SUFFICIENT) {
                this.flag = 3;
            } else {
                this.flag = 1;
            }
        }

        void create(Subject subject, CallbackHandler callbackHandler, Map<String, ?> map) throws LoginException {
            String loginModuleName = this.entry.getLoginModuleName();
            if (this.klass == null) {
                try {
                    this.klass = Class.forName(loginModuleName, false, LoginContext.this.contextClassLoader);
                } catch (Throwable e) {
                    throw ((LoginException) new LoginException("auth.39 " + loginModuleName).initCause(e));
                }
            }
            if (this.module == null) {
                try {
                    this.module = (LoginModule) this.klass.newInstance();
                    this.module.initialize(subject, callbackHandler, map, this.entry.getOptions());
                } catch (Throwable e2) {
                    throw ((LoginException) new LoginException("auth.3A " + loginModuleName).initCause(e2));
                } catch (Throwable e22) {
                    throw ((LoginException) new LoginException("auth.3A" + loginModuleName).initCause(e22));
                }
            }
        }

        int getFlag() {
            return this.flag;
        }
    }

    public LoginContext(String str) throws LoginException {
        init(str, null, null, null);
    }

    public LoginContext(String str, Subject subject) throws LoginException {
        if (subject == null) {
            throw new LoginException("auth.03");
        }
        init(str, subject, null, null);
    }

    public LoginContext(String str, Subject subject, CallbackHandler callbackHandler) throws LoginException {
        if (subject == null) {
            throw new LoginException("auth.03");
        } else if (callbackHandler == null) {
            throw new LoginException("auth.34");
        } else {
            init(str, subject, callbackHandler, null);
        }
    }

    public LoginContext(String str, Subject subject, CallbackHandler callbackHandler, Configuration configuration) throws LoginException {
        init(str, subject, callbackHandler, configuration);
    }

    public LoginContext(String str, CallbackHandler callbackHandler) throws LoginException {
        if (callbackHandler == null) {
            throw new LoginException("auth.34");
        }
        init(str, null, callbackHandler, null);
    }

    private void init(String str, Subject subject, final CallbackHandler callbackHandler, Configuration configuration) throws LoginException {
        int i = 0;
        this.subject = subject;
        this.userProvidedSubject = subject != null;
        if (str == null) {
            throw new LoginException("auth.00");
        }
        if (configuration == null) {
            configuration = Configuration.getAccessibleConfiguration();
        } else {
            this.userProvidedConfig = true;
        }
        SecurityManager securityManager = System.getSecurityManager();
        if (!(securityManager == null || this.userProvidedConfig)) {
            securityManager.checkPermission(new AuthPermission("createLoginContext." + str));
        }
        AppConfigurationEntry[] appConfigurationEntry = configuration.getAppConfigurationEntry(str);
        if (appConfigurationEntry == null) {
            if (!(securityManager == null || this.userProvidedConfig)) {
                securityManager.checkPermission(new AuthPermission("createLoginContext.other"));
            }
            appConfigurationEntry = configuration.getAppConfigurationEntry("other");
            if (appConfigurationEntry == null) {
                throw new LoginException("auth.35 " + str);
            }
        }
        this.modules = new Module[appConfigurationEntry.length];
        while (i < this.modules.length) {
            this.modules[i] = new Module(appConfigurationEntry[i]);
            i++;
        }
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                public Void run() throws Exception {
                    LoginContext.this.contextClassLoader = Thread.currentThread().getContextClassLoader();
                    if (LoginContext.this.contextClassLoader == null) {
                        LoginContext.this.contextClassLoader = ClassLoader.getSystemClassLoader();
                    }
                    if (callbackHandler == null) {
                        String property = Security.getProperty(LoginContext.DEFAULT_CALLBACK_HANDLER_PROPERTY);
                        if (!(property == null || property.length() == 0)) {
                            LoginContext.this.callbackHandler = (CallbackHandler) Class.forName(property, true, LoginContext.this.contextClassLoader).newInstance();
                        }
                    } else {
                        LoginContext.this.callbackHandler = callbackHandler;
                    }
                    return null;
                }
            });
            if (this.userProvidedConfig) {
                this.userContext = AccessController.getContext();
            } else if (this.callbackHandler != null) {
                this.userContext = AccessController.getContext();
                this.callbackHandler = new ContextedCallbackHandler(this.callbackHandler);
            }
        } catch (PrivilegedActionException e) {
            throw ((LoginException) new LoginException("auth.36").initCause(e.getCause()));
        }
    }

    private void loginImpl() throws LoginException {
        int length;
        Throwable th;
        boolean z;
        boolean z2;
        Module[] moduleArr;
        int i = 0;
        if (this.subject == null) {
            this.subject = new Subject();
        }
        if (this.sharedState == null) {
            this.sharedState = new HashMap();
        }
        Throwable th2 = null;
        int[] iArr = new int[4];
        int[] iArr2 = new int[4];
        for (Module module : this.modules) {
            try {
                module.create(this.subject, this.callbackHandler, this.sharedState);
                if (module.module.login()) {
                    int flag = module.getFlag();
                    iArr2[flag] = iArr2[flag] + 1;
                    flag = module.getFlag();
                    iArr[flag] = iArr[flag] + 1;
                    if (module.getFlag() == 3) {
                        break;
                    }
                } else {
                    continue;
                }
            } catch (Throwable th3) {
                th = th3;
                if (th2 != null) {
                    th = th2;
                }
                if (module.klass == null) {
                    iArr2[1] = iArr2[1] + 1;
                    th2 = th;
                    break;
                }
                int flag2 = module.getFlag();
                iArr2[flag2] = iArr2[flag2] + 1;
                if (module.getFlag() == 2) {
                    th2 = th;
                    break;
                }
                th2 = th;
            }
        }
        if (iArr[1] == iArr2[1]) {
            if (iArr[2] != iArr2[2]) {
                z = true;
            } else if (iArr2[1] != 0 || iArr2[2] != 0) {
                z = false;
            } else if (!(iArr[0] == 0 && iArr[3] == 0)) {
                z = false;
            }
            iArr = new int[]{0, 0, 0, 0};
            if (z) {
                th = th2;
                for (Module module2 : this.modules) {
                    if (module2.klass != null) {
                        int flag3 = module2.getFlag();
                        iArr2[flag3] = iArr2[flag3] + 1;
                        try {
                            module2.module.commit();
                            flag2 = module2.getFlag();
                            iArr[flag2] = iArr[flag2] + 1;
                        } catch (Throwable th22) {
                            if (th == null) {
                                th = th22;
                            }
                        }
                    }
                }
            } else {
                th = th22;
            }
            if (iArr[1] == iArr2[1]) {
                if (iArr[2] == iArr2[2]) {
                    z2 = true;
                } else if (iArr2[1] == 0 || iArr2[2] != 0) {
                    z2 = false;
                } else if (!(iArr[0] == 0 && iArr[3] == 0)) {
                    z2 = false;
                }
                if (z2) {
                    this.loggedIn = true;
                    return;
                }
                moduleArr = this.modules;
                length = moduleArr.length;
                while (i < length) {
                    try {
                        moduleArr[i].module.abort();
                    } catch (Throwable th222) {
                        if (th == null) {
                            th = th222;
                        }
                    }
                    i++;
                }
                if ((th instanceof PrivilegedActionException) && th.getCause() != null) {
                    th = th.getCause();
                }
                if (th instanceof LoginException) {
                    throw ((LoginException) new LoginException("auth.37").initCause(th));
                }
                throw ((LoginException) th);
            }
            z2 = true;
            if (z2) {
                this.loggedIn = true;
                return;
            }
            moduleArr = this.modules;
            length = moduleArr.length;
            while (i < length) {
                moduleArr[i].module.abort();
                i++;
            }
            th = th.getCause();
            if (th instanceof LoginException) {
                throw ((LoginException) new LoginException("auth.37").initCause(th));
            }
            throw ((LoginException) th);
        }
        z = true;
        iArr = new int[]{0, 0, 0, 0};
        if (z) {
            th = th222;
        } else {
            th = th222;
            for (length = 0; length < r8; length++) {
                if (module2.klass != null) {
                    int flag32 = module2.getFlag();
                    iArr2[flag32] = iArr2[flag32] + 1;
                    module2.module.commit();
                    flag2 = module2.getFlag();
                    iArr[flag2] = iArr[flag2] + 1;
                }
            }
        }
        if (iArr[1] == iArr2[1]) {
            if (iArr[2] == iArr2[2]) {
                if (iArr2[1] == 0) {
                }
                z2 = false;
            } else {
                z2 = true;
            }
            if (z2) {
                moduleArr = this.modules;
                length = moduleArr.length;
                while (i < length) {
                    moduleArr[i].module.abort();
                    i++;
                }
                th = th.getCause();
                if (th instanceof LoginException) {
                    throw ((LoginException) th);
                }
                throw ((LoginException) new LoginException("auth.37").initCause(th));
            }
            this.loggedIn = true;
            return;
        }
        z2 = true;
        if (z2) {
            this.loggedIn = true;
            return;
        }
        moduleArr = this.modules;
        length = moduleArr.length;
        while (i < length) {
            moduleArr[i].module.abort();
            i++;
        }
        th = th.getCause();
        if (th instanceof LoginException) {
            throw ((LoginException) new LoginException("auth.37").initCause(th));
        }
        throw ((LoginException) th);
    }

    private void logoutImpl() throws LoginException {
        int i = 0;
        if (this.subject == null) {
            throw new LoginException("auth.38");
        }
        this.loggedIn = false;
        Throwable th = null;
        for (Module module : this.modules) {
            try {
                module.module.logout();
                i++;
            } catch (Throwable th2) {
                if (th == null) {
                    th = th2;
                }
            }
        }
        if (th != null || i == 0) {
            Throwable cause = (!(th instanceof PrivilegedActionException) || th.getCause() == null) ? th : th.getCause();
            if (cause instanceof LoginException) {
                throw ((LoginException) cause);
            }
            throw ((LoginException) new LoginException("auth.37").initCause(cause));
        }
    }

    public Subject getSubject() {
        return (this.userProvidedSubject || this.loggedIn) ? this.subject : null;
    }

    public void login() throws LoginException {
        PrivilegedExceptionAction anonymousClass2 = new PrivilegedExceptionAction<Void>() {
            public Void run() throws LoginException {
                LoginContext.this.loginImpl();
                return null;
            }
        };
        try {
            if (this.userProvidedConfig) {
                AccessController.doPrivileged(anonymousClass2, this.userContext);
            } else {
                AccessController.doPrivileged(anonymousClass2);
            }
        } catch (PrivilegedActionException e) {
            throw ((LoginException) e.getException());
        }
    }

    public void logout() throws LoginException {
        PrivilegedExceptionAction anonymousClass3 = new PrivilegedExceptionAction<Void>() {
            public Void run() throws LoginException {
                LoginContext.this.logoutImpl();
                return null;
            }
        };
        try {
            if (this.userProvidedConfig) {
                AccessController.doPrivileged(anonymousClass3, this.userContext);
            } else {
                AccessController.doPrivileged(anonymousClass3);
            }
        } catch (PrivilegedActionException e) {
            throw ((LoginException) e.getException());
        }
    }
}
