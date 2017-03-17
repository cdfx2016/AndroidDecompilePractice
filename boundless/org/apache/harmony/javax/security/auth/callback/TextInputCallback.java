package org.apache.harmony.javax.security.auth.callback;

import java.io.Serializable;

public class TextInputCallback implements Callback, Serializable {
    private static final long serialVersionUID = -8064222478852811804L;
    private String defaultText;
    private String inputText;
    private String prompt;

    public TextInputCallback(String str) {
        setPrompt(str);
    }

    public TextInputCallback(String str, String str2) {
        setPrompt(str);
        setDefaultText(str2);
    }

    private void setDefaultText(String str) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("auth.15");
        }
        this.defaultText = str;
    }

    private void setPrompt(String str) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("auth.14");
        }
        this.prompt = str;
    }

    public String getDefaultText() {
        return this.defaultText;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public String getText() {
        return this.inputText;
    }

    public void setText(String str) {
        this.inputText = str;
    }
}
