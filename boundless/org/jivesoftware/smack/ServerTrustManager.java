package org.jivesoftware.smack;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.X509TrustManager;

class ServerTrustManager implements X509TrustManager {
    private static Pattern cnPattern = Pattern.compile("(?i)(cn=)([^,]*)");
    private static Map<KeyStoreOptions, KeyStore> stores = new HashMap();
    private ConnectionConfiguration configuration;
    private String server;
    private KeyStore trustStore;

    private static class KeyStoreOptions {
        private final String password;
        private final String path;
        private final String type;

        public KeyStoreOptions(String str, String str2, String str3) {
            this.type = str;
            this.path = str2;
            this.password = str3;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            KeyStoreOptions keyStoreOptions = (KeyStoreOptions) obj;
            if (this.password == null) {
                if (keyStoreOptions.password != null) {
                    return false;
                }
            } else if (!this.password.equals(keyStoreOptions.password)) {
                return false;
            }
            if (this.path == null) {
                if (keyStoreOptions.path != null) {
                    return false;
                }
            } else if (!this.path.equals(keyStoreOptions.path)) {
                return false;
            }
            return this.type == null ? keyStoreOptions.type == null : this.type.equals(keyStoreOptions.type);
        }

        public String getPassword() {
            return this.password;
        }

        public String getPath() {
            return this.path;
        }

        public String getType() {
            return this.type;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.path == null ? 0 : this.path.hashCode()) + (((this.password == null ? 0 : this.password.hashCode()) + 31) * 31)) * 31;
            if (this.type != null) {
                i = this.type.hashCode();
            }
            return hashCode + i;
        }
    }

    public ServerTrustManager(String str, ConnectionConfiguration connectionConfiguration) {
        Exception e;
        Throwable th;
        InputStream inputStream = null;
        this.configuration = connectionConfiguration;
        this.server = str;
        synchronized (stores) {
            KeyStoreOptions keyStoreOptions = new KeyStoreOptions(connectionConfiguration.getTruststoreType(), connectionConfiguration.getTruststorePath(), connectionConfiguration.getTruststorePassword());
            if (stores.containsKey(keyStoreOptions)) {
                this.trustStore = (KeyStore) stores.get(keyStoreOptions);
            } else {
                try {
                    this.trustStore = KeyStore.getInstance(keyStoreOptions.getType());
                    char[] toCharArray = keyStoreOptions.getPassword() != null ? keyStoreOptions.getPassword().toCharArray() : null;
                    if (keyStoreOptions.getPath() == null) {
                        this.trustStore.load(null, toCharArray);
                    } else {
                        InputStream fileInputStream = new FileInputStream(keyStoreOptions.getPath());
                        try {
                            this.trustStore.load(fileInputStream, toCharArray);
                            inputStream = fileInputStream;
                        } catch (Exception e2) {
                            e = e2;
                            inputStream = fileInputStream;
                            try {
                                this.trustStore = null;
                                e.printStackTrace();
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (IOException e3) {
                                    }
                                }
                                stores.put(keyStoreOptions, this.trustStore);
                                if (this.trustStore == null) {
                                    connectionConfiguration.setVerifyRootCAEnabled(false);
                                }
                                return;
                            } catch (Throwable th2) {
                                th = th2;
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (IOException e4) {
                                    }
                                }
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            inputStream = fileInputStream;
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            throw th;
                        }
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e5) {
                        }
                    }
                } catch (Exception e6) {
                    e = e6;
                    this.trustStore = null;
                    e.printStackTrace();
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    stores.put(keyStoreOptions, this.trustStore);
                    if (this.trustStore == null) {
                        connectionConfiguration.setVerifyRootCAEnabled(false);
                    }
                    return;
                }
                stores.put(keyStoreOptions, this.trustStore);
            }
            if (this.trustStore == null) {
                connectionConfiguration.setVerifyRootCAEnabled(false);
            }
        }
        return;
    }

    public static List<String> getPeerIdentity(X509Certificate x509Certificate) {
        List<String> subjectAlternativeNames = getSubjectAlternativeNames(x509Certificate);
        if (!subjectAlternativeNames.isEmpty()) {
            return subjectAlternativeNames;
        }
        Object name = x509Certificate.getSubjectDN().getName();
        Matcher matcher = cnPattern.matcher(name);
        if (matcher.find()) {
            name = matcher.group(2);
        }
        List<String> arrayList = new ArrayList();
        arrayList.add(name);
        return arrayList;
    }

    private static List<String> getSubjectAlternativeNames(X509Certificate x509Certificate) {
        List<String> arrayList = new ArrayList();
        try {
            if (x509Certificate.getSubjectAlternativeNames() == null) {
                arrayList = Collections.emptyList();
            }
        } catch (CertificateParsingException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        int i;
        int i2;
        KeyStoreException keyStoreException;
        Date date;
        int i3 = 0;
        int length = x509CertificateArr.length;
        List peerIdentity = getPeerIdentity(x509CertificateArr[0]);
        if (this.configuration.isVerifyChainEnabled()) {
            Object obj = null;
            i = length - 1;
            while (i >= 0) {
                X509Certificate x509Certificate = x509CertificateArr[i];
                Principal issuerDN = x509Certificate.getIssuerDN();
                Principal subjectDN = x509Certificate.getSubjectDN();
                if (obj != null) {
                    if (issuerDN.equals(obj)) {
                        try {
                            x509CertificateArr[i].verify(x509CertificateArr[i + 1].getPublicKey());
                        } catch (GeneralSecurityException e) {
                            throw new CertificateException("signature verification failed of " + peerIdentity);
                        }
                    }
                    throw new CertificateException("subject/issuer verification failed of " + peerIdentity);
                }
                i--;
                Principal principal = subjectDN;
            }
        }
        if (this.configuration.isVerifyRootCAEnabled()) {
            try {
                i = this.trustStore.getCertificateAlias(x509CertificateArr[length + -1]) != null ? 1 : 0;
                if (i == 0 && length == 1) {
                    try {
                        if (this.configuration.isSelfSignedCertificateEnabled()) {
                            System.out.println("Accepting self-signed certificate of remote server: " + peerIdentity);
                            i = 1;
                        }
                    } catch (KeyStoreException e2) {
                        KeyStoreException keyStoreException2 = e2;
                        i2 = i;
                        keyStoreException = keyStoreException2;
                        keyStoreException.printStackTrace();
                        i = i2;
                        if (i == 0) {
                            throw new CertificateException("root certificate not trusted of " + peerIdentity);
                        }
                        if (this.configuration.isNotMatchingDomainCheckEnabled()) {
                            if (peerIdentity.size() != 1) {
                            }
                            if (!peerIdentity.contains(this.server)) {
                                throw new CertificateException("target verification failed of " + peerIdentity);
                            }
                        }
                        if (!this.configuration.isExpiredCertificatesCheckEnabled()) {
                            date = new Date();
                            while (i3 < length) {
                                try {
                                    x509CertificateArr[i3].checkValidity(date);
                                    i3++;
                                } catch (GeneralSecurityException e3) {
                                    throw new CertificateException("invalid date of " + this.server);
                                }
                            }
                        }
                    }
                }
            } catch (KeyStoreException e4) {
                keyStoreException = e4;
                i2 = 0;
                keyStoreException.printStackTrace();
                i = i2;
                if (i == 0) {
                    throw new CertificateException("root certificate not trusted of " + peerIdentity);
                }
                if (this.configuration.isNotMatchingDomainCheckEnabled()) {
                    if (peerIdentity.size() != 1) {
                    }
                    if (peerIdentity.contains(this.server)) {
                        throw new CertificateException("target verification failed of " + peerIdentity);
                    }
                }
                if (!this.configuration.isExpiredCertificatesCheckEnabled()) {
                    date = new Date();
                    while (i3 < length) {
                        x509CertificateArr[i3].checkValidity(date);
                        i3++;
                    }
                }
            }
            if (i == 0) {
                throw new CertificateException("root certificate not trusted of " + peerIdentity);
            }
        }
        if (this.configuration.isNotMatchingDomainCheckEnabled()) {
            if (peerIdentity.size() != 1 && ((String) peerIdentity.get(0)).startsWith("*.")) {
                if (!this.server.endsWith(((String) peerIdentity.get(0)).replace("*.", ""))) {
                    throw new CertificateException("target verification failed of " + peerIdentity);
                }
            } else if (peerIdentity.contains(this.server)) {
                throw new CertificateException("target verification failed of " + peerIdentity);
            }
        }
        if (!this.configuration.isExpiredCertificatesCheckEnabled()) {
            date = new Date();
            while (i3 < length) {
                x509CertificateArr[i3].checkValidity(date);
                i3++;
            }
        }
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
