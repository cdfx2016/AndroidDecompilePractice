package org.apache.harmony.javax.security.auth.login;

import org.apache.harmony.javax.security.auth.AuthPermission;

public abstract class Configuration {
    private static final AuthPermission GET_LOGIN_CONFIGURATION = new AuthPermission("getLoginConfiguration");
    private static final String LOGIN_CONFIGURATION_PROVIDER = "login.configuration.provider";
    private static final AuthPermission SET_LOGIN_CONFIGURATION = new AuthPermission("setLoginConfiguration");
    private static Configuration configuration;

    protected Configuration() {
    }

    static Configuration getAccessibleConfiguration() {
        Configuration configuration = configuration;
        if (configuration == null) {
            synchronized (Configuration.class) {
                if (configuration == null) {
                    configuration = getDefaultProvider();
                }
                configuration = configuration;
            }
        }
        return configuration;
    }

    public static Configuration getConfiguration() {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(GET_LOGIN_CONFIGURATION);
        }
        return getAccessibleConfiguration();
    }

    private static final Configuration getDefaultProvider() {
        return new Configuration() {
            public AppConfigurationEntry[] getAppConfigurationEntry(String str) {
                return new AppConfigurationEntry[0];
            }

            public void refresh() {
            }
        };
    }

    public static void setConfiguration(Configuration configuration) {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SET_LOGIN_CONFIGURATION);
        }
        configuration = configuration;
    }

    public abstract AppConfigurationEntry[] getAppConfigurationEntry(String str);

    public abstract void refresh();
}
