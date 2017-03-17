package org.apache.harmony.javax.security.auth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Principal;
import java.util.Set;

public final class PrivateCredentialPermission extends Permission {
    private static final String READ = "read";
    private static final long serialVersionUID = 5284372143517237068L;
    private String credentialClass;
    private transient int offset;
    private transient CredOwner[] set;

    private static final class CredOwner implements Serializable {
        private static final long serialVersionUID = -5607449830436408266L;
        private transient boolean isClassWildcard;
        private transient boolean isPNameWildcard;
        String principalClass;
        String principalName;

        CredOwner(String str, String str2) {
            if ("*".equals(str)) {
                this.isClassWildcard = true;
            }
            if ("*".equals(str2)) {
                this.isPNameWildcard = true;
            }
            if (!this.isClassWildcard || this.isPNameWildcard) {
                this.principalClass = str;
                this.principalName = str2;
                return;
            }
            throw new IllegalArgumentException("auth.12");
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CredOwner)) {
                return false;
            }
            CredOwner credOwner = (CredOwner) obj;
            return this.principalClass.equals(credOwner.principalClass) && this.principalName.equals(credOwner.principalName);
        }

        public int hashCode() {
            return this.principalClass.hashCode() + this.principalName.hashCode();
        }

        boolean implies(Object obj) {
            if (obj == this) {
                return true;
            }
            CredOwner credOwner = (CredOwner) obj;
            return (this.isClassWildcard || this.principalClass.equals(credOwner.principalClass)) && (this.isPNameWildcard || this.principalName.equals(credOwner.principalName));
        }
    }

    public PrivateCredentialPermission(String str, String str2) {
        super(str);
        if (READ.equalsIgnoreCase(str2)) {
            initTargetName(str);
            return;
        }
        throw new IllegalArgumentException("auth.11");
    }

    PrivateCredentialPermission(String str, Set<Principal> set) {
        super(str);
        this.credentialClass = str;
        this.set = new CredOwner[set.size()];
        for (Principal principal : set) {
            Object obj;
            CredOwner credOwner = new CredOwner(principal.getClass().getName(), principal.getName());
            for (int i = 0; i < this.offset; i++) {
                if (this.set[i].equals(credOwner)) {
                    obj = 1;
                    break;
                }
            }
            obj = null;
            if (obj == null) {
                CredOwner[] credOwnerArr = this.set;
                int i2 = this.offset;
                this.offset = i2 + 1;
                credOwnerArr[i2] = credOwner;
            }
        }
    }

    private void initTargetName(String str) {
        if (str == null) {
            throw new NullPointerException("auth.0E");
        }
        String trim = str.trim();
        if (trim.length() == 0) {
            throw new IllegalArgumentException("auth.0F");
        }
        int indexOf = trim.indexOf(32);
        if (indexOf == -1) {
            throw new IllegalArgumentException("auth.10");
        }
        this.credentialClass = trim.substring(0, indexOf);
        indexOf++;
        int length = trim.length();
        int i = 0;
        while (indexOf < length) {
            indexOf = trim.indexOf(32, indexOf);
            int indexOf2 = trim.indexOf(34, indexOf + 2);
            if (indexOf == -1 || indexOf2 == -1 || trim.charAt(indexOf + 1) != '\"') {
                throw new IllegalArgumentException("auth.10");
            }
            i++;
            indexOf = indexOf2 + 2;
        }
        if (i < 1) {
            throw new IllegalArgumentException("auth.10");
        }
        indexOf = trim.indexOf(32) + 1;
        this.set = new CredOwner[i];
        indexOf2 = 0;
        while (indexOf2 < i) {
            length = trim.indexOf(32, indexOf);
            int indexOf3 = trim.indexOf(34, length + 2);
            CredOwner credOwner = new CredOwner(trim.substring(indexOf, length), trim.substring(length + 2, indexOf3));
            for (indexOf = 0; indexOf < this.offset; indexOf++) {
                if (this.set[indexOf].equals(credOwner)) {
                    indexOf = 1;
                    break;
                }
            }
            indexOf = 0;
            if (indexOf == 0) {
                CredOwner[] credOwnerArr = this.set;
                length = this.offset;
                this.offset = length + 1;
                credOwnerArr[length] = credOwner;
            }
            indexOf2++;
            indexOf = indexOf3 + 2;
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        initTargetName(getName());
    }

    private boolean sameMembers(Object[] objArr, Object[] objArr2, int i) {
        if (objArr == null && objArr2 == null) {
            return true;
        }
        if (objArr == null || objArr2 == null) {
            return false;
        }
        for (int i2 = 0; i2 < i; i2++) {
            boolean z;
            for (int i3 = 0; i3 < i; i3++) {
                if (objArr[i2].equals(objArr2[i3])) {
                    z = true;
                    break;
                }
            }
            z = false;
            if (!z) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrivateCredentialPermission privateCredentialPermission = (PrivateCredentialPermission) obj;
        return this.credentialClass.equals(privateCredentialPermission.credentialClass) && this.offset == privateCredentialPermission.offset && sameMembers(this.set, privateCredentialPermission.set, this.offset);
    }

    public String getActions() {
        return READ;
    }

    public String getCredentialClass() {
        return this.credentialClass;
    }

    public String[][] getPrincipals() {
        String[][] strArr = (String[][]) Array.newInstance(String.class, new int[]{this.offset, 2});
        for (int i = 0; i < strArr.length; i++) {
            strArr[i][0] = this.set[i].principalClass;
            strArr[i][1] = this.set[i].principalName;
        }
        return strArr;
    }

    public int hashCode() {
        int i = 0;
        int i2 = 0;
        while (i < this.offset) {
            i2 += this.set[i].hashCode();
            i++;
        }
        return getCredentialClass().hashCode() + i2;
    }

    public boolean implies(Permission permission) {
        if (permission == null || getClass() != permission.getClass()) {
            return false;
        }
        PrivateCredentialPermission privateCredentialPermission = (PrivateCredentialPermission) permission;
        if (!"*".equals(this.credentialClass) && !this.credentialClass.equals(privateCredentialPermission.getCredentialClass())) {
            return false;
        }
        if (privateCredentialPermission.offset == 0) {
            return true;
        }
        CredOwner[] credOwnerArr = this.set;
        CredOwner[] credOwnerArr2 = privateCredentialPermission.set;
        int i = this.offset;
        int i2 = privateCredentialPermission.offset;
        int i3 = 0;
        while (i3 < i) {
            int i4 = 0;
            while (i4 < i2 && !credOwnerArr[i3].implies(credOwnerArr2[i4])) {
                i4++;
            }
            if (i4 == credOwnerArr2.length) {
                return false;
            }
            i3++;
        }
        return true;
    }

    public PermissionCollection newPermissionCollection() {
        return null;
    }
}
