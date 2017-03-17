package okhttp3.internal.tls;

import cn.finalteam.toolsfinal.coder.RSACoder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.X509TrustManager;

public abstract class CertificateChainCleaner {

    static final class AndroidCertificateChainCleaner extends CertificateChainCleaner {
        private final Method checkServerTrusted;
        private final Object x509TrustManagerExtensions;

        AndroidCertificateChainCleaner(Object x509TrustManagerExtensions, Method checkServerTrusted) {
            this.x509TrustManagerExtensions = x509TrustManagerExtensions;
            this.checkServerTrusted = checkServerTrusted;
        }

        public List<Certificate> clean(List<Certificate> chain, String hostname) throws SSLPeerUnverifiedException {
            try {
                X509Certificate[] certificates = (X509Certificate[]) chain.toArray(new X509Certificate[chain.size()]);
                return (List) this.checkServerTrusted.invoke(this.x509TrustManagerExtensions, new Object[]{certificates, RSACoder.KEY_ALGORITHM, hostname});
            } catch (InvocationTargetException e) {
                SSLPeerUnverifiedException exception = new SSLPeerUnverifiedException(e.getMessage());
                exception.initCause(e);
                throw exception;
            } catch (IllegalAccessException e2) {
                throw new AssertionError(e2);
            }
        }
    }

    static final class BasicCertificateChainCleaner extends CertificateChainCleaner {
        private static final int MAX_SIGNERS = 9;
        private final TrustRootIndex trustRootIndex;

        public BasicCertificateChainCleaner(TrustRootIndex trustRootIndex) {
            this.trustRootIndex = trustRootIndex;
        }

        public List<Certificate> clean(List<Certificate> chain, String hostname) throws SSLPeerUnverifiedException {
            Deque<Certificate> queue = new ArrayDeque(chain);
            List<Certificate> result = new ArrayList();
            result.add(queue.removeFirst());
            boolean foundTrustedCertificate = false;
            int c = 0;
            while (c < 9) {
                X509Certificate toVerify = (X509Certificate) result.get(result.size() - 1);
                X509Certificate trustedCert = this.trustRootIndex.findByIssuerAndSignature(toVerify);
                if (trustedCert != null) {
                    if (result.size() > 1 || !toVerify.equals(trustedCert)) {
                        result.add(trustedCert);
                    }
                    if (!verifySignature(trustedCert, trustedCert)) {
                        foundTrustedCertificate = true;
                        c++;
                    }
                } else {
                    Iterator<Certificate> i = queue.iterator();
                    while (i.hasNext()) {
                        X509Certificate signingCert = (X509Certificate) i.next();
                        if (verifySignature(toVerify, signingCert)) {
                            i.remove();
                            result.add(signingCert);
                            c++;
                        }
                    }
                    if (!foundTrustedCertificate) {
                        throw new SSLPeerUnverifiedException("Failed to find a trusted cert that signed " + toVerify);
                    }
                }
                return result;
            }
            throw new SSLPeerUnverifiedException("Certificate chain too long: " + result);
        }

        private boolean verifySignature(X509Certificate toVerify, X509Certificate signingCert) {
            if (!toVerify.getIssuerDN().equals(signingCert.getSubjectDN())) {
                return false;
            }
            try {
                toVerify.verify(signingCert.getPublicKey());
                return true;
            } catch (GeneralSecurityException e) {
                return false;
            }
        }
    }

    public abstract List<Certificate> clean(List<Certificate> list, String str) throws SSLPeerUnverifiedException;

    public static CertificateChainCleaner get(X509TrustManager trustManager) {
        try {
            Class<?> extensionsClass = Class.forName("android.net.http.X509TrustManagerExtensions");
            return new AndroidCertificateChainCleaner(extensionsClass.getConstructor(new Class[]{X509TrustManager.class}).newInstance(new Object[]{trustManager}), extensionsClass.getMethod("checkServerTrusted", new Class[]{X509Certificate[].class, String.class, String.class}));
        } catch (Exception e) {
            return new BasicCertificateChainCleaner(TrustRootIndex.get(trustManager));
        }
    }

    public static CertificateChainCleaner get(X509Certificate... caCerts) {
        return new BasicCertificateChainCleaner(TrustRootIndex.get(caCerts));
    }
}
