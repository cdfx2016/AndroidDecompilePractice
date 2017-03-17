package org.apache.harmony.javax.security.auth;

public interface Destroyable {
    void destroy() throws DestroyFailedException;

    boolean isDestroyed();
}
