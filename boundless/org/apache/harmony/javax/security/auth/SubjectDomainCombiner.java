package org.apache.harmony.javax.security.auth;

import java.security.DomainCombiner;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.util.Set;

public class SubjectDomainCombiner implements DomainCombiner {
    private static final AuthPermission _GET = new AuthPermission("getSubjectFromDomainCombiner");
    private Subject subject;

    public SubjectDomainCombiner(Subject subject) {
        if (subject == null) {
            throw new NullPointerException();
        }
        this.subject = subject;
    }

    public ProtectionDomain[] combine(ProtectionDomain[] protectionDomainArr, ProtectionDomain[] protectionDomainArr2) {
        int length = protectionDomainArr != null ? protectionDomainArr.length + 0 : 0;
        if (protectionDomainArr2 != null) {
            length += protectionDomainArr2.length;
        }
        if (length == 0) {
            return null;
        }
        int i;
        Object obj = new ProtectionDomain[length];
        if (protectionDomainArr != null) {
            Set principals = this.subject.getPrincipals();
            Principal[] principalArr = (Principal[]) principals.toArray(new Principal[principals.size()]);
            i = 0;
            while (i < protectionDomainArr.length) {
                if (protectionDomainArr[i] != null) {
                    obj[i] = new ProtectionDomain(protectionDomainArr[i].getCodeSource(), protectionDomainArr[i].getPermissions(), protectionDomainArr[i].getClassLoader(), principalArr);
                }
                i++;
            }
        } else {
            i = 0;
        }
        if (protectionDomainArr2 != null) {
            System.arraycopy(protectionDomainArr2, 0, obj, i, protectionDomainArr2.length);
        }
        return obj;
    }

    public Subject getSubject() {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(_GET);
        }
        return this.subject;
    }
}
