package com.mob.tools.network;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class MultiPart extends HTTPPart {
    private ArrayList<HTTPPart> parts = new ArrayList();

    public MultiPart append(HTTPPart part) throws Throwable {
        this.parts.add(part);
        return this;
    }

    protected InputStream getInputStream() throws Throwable {
        MultiPartInputStream mpis = new MultiPartInputStream();
        Iterator i$ = this.parts.iterator();
        while (i$.hasNext()) {
            mpis.addInputStream(((HTTPPart) i$.next()).getInputStream());
        }
        return mpis;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator i$ = this.parts.iterator();
        while (i$.hasNext()) {
            sb.append(((HTTPPart) i$.next()).toString());
        }
        return sb.toString();
    }

    protected long length() throws Throwable {
        long length = 0;
        Iterator i$ = this.parts.iterator();
        while (i$.hasNext()) {
            length += ((HTTPPart) i$.next()).length();
        }
        return length;
    }
}
