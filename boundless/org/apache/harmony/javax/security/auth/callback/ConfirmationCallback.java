package org.apache.harmony.javax.security.auth.callback;

import java.io.Serializable;

public class ConfirmationCallback implements Callback, Serializable {
    public static final int CANCEL = 2;
    public static final int ERROR = 2;
    public static final int INFORMATION = 0;
    public static final int NO = 1;
    public static final int OK = 3;
    public static final int OK_CANCEL_OPTION = 2;
    public static final int UNSPECIFIED_OPTION = -1;
    public static final int WARNING = 1;
    public static final int YES = 0;
    public static final int YES_NO_CANCEL_OPTION = 1;
    public static final int YES_NO_OPTION = 0;
    private static final long serialVersionUID = -9095656433782481624L;
    private int defaultOption;
    private int messageType;
    private int optionType = -1;
    private String[] options;
    private String prompt;
    private int selection;

    public ConfirmationCallback(int i, int i2, int i3) {
        if (i > 2 || i < 0) {
            throw new IllegalArgumentException("auth.16");
        }
        switch (i2) {
            case 0:
                if (!(i3 == 0 || i3 == 1)) {
                    throw new IllegalArgumentException("auth.17");
                }
            case 1:
                if (!(i3 == 0 || i3 == 1 || i3 == 2)) {
                    throw new IllegalArgumentException("auth.17");
                }
            case 2:
                if (!(i3 == 3 || i3 == 2)) {
                    throw new IllegalArgumentException("auth.17");
                }
            default:
                throw new IllegalArgumentException("auth.18");
        }
        this.messageType = i;
        this.optionType = i2;
        this.defaultOption = i3;
    }

    public ConfirmationCallback(int i, String[] strArr, int i2) {
        if (i > 2 || i < 0) {
            throw new IllegalArgumentException("auth.16");
        } else if (strArr == null || strArr.length == 0) {
            throw new IllegalArgumentException("auth.1A");
        } else {
            int i3 = 0;
            while (i3 < strArr.length) {
                if (strArr[i3] == null || strArr[i3].length() == 0) {
                    throw new IllegalArgumentException("auth.1A");
                }
                i3++;
            }
            if (i2 < 0 || i2 >= strArr.length) {
                throw new IllegalArgumentException("auth.17");
            }
            this.options = strArr;
            this.defaultOption = i2;
            this.messageType = i;
        }
    }

    public ConfirmationCallback(String str, int i, int i2, int i3) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("auth.14");
        } else if (i > 2 || i < 0) {
            throw new IllegalArgumentException("auth.16");
        } else {
            switch (i2) {
                case 0:
                    if (!(i3 == 0 || i3 == 1)) {
                        throw new IllegalArgumentException("auth.17");
                    }
                case 1:
                    if (!(i3 == 0 || i3 == 1 || i3 == 2)) {
                        throw new IllegalArgumentException("auth.17");
                    }
                case 2:
                    if (!(i3 == 3 || i3 == 2)) {
                        throw new IllegalArgumentException("auth.17");
                    }
                default:
                    throw new IllegalArgumentException("auth.18");
            }
            this.prompt = str;
            this.messageType = i;
            this.optionType = i2;
            this.defaultOption = i3;
        }
    }

    public ConfirmationCallback(String str, int i, String[] strArr, int i2) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("auth.14");
        } else if (i > 2 || i < 0) {
            throw new IllegalArgumentException("auth.16");
        } else if (strArr == null || strArr.length == 0) {
            throw new IllegalArgumentException("auth.1A");
        } else {
            int i3 = 0;
            while (i3 < strArr.length) {
                if (strArr[i3] == null || strArr[i3].length() == 0) {
                    throw new IllegalArgumentException("auth.1A");
                }
                i3++;
            }
            if (i2 < 0 || i2 >= strArr.length) {
                throw new IllegalArgumentException("auth.17");
            }
            this.options = strArr;
            this.defaultOption = i2;
            this.messageType = i;
            this.prompt = str;
        }
    }

    public int getDefaultOption() {
        return this.defaultOption;
    }

    public int getMessageType() {
        return this.messageType;
    }

    public int getOptionType() {
        return this.optionType;
    }

    public String[] getOptions() {
        return this.options;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public int getSelectedIndex() {
        return this.selection;
    }

    public void setSelectedIndex(int i) {
        if (this.options == null) {
            switch (this.optionType) {
                case 0:
                    if (!(i == 0 || i == 1)) {
                        throw new IllegalArgumentException("auth.19");
                    }
                case 1:
                    if (!(i == 0 || i == 1 || i == 2)) {
                        throw new IllegalArgumentException("auth.19");
                    }
                case 2:
                    if (!(i == 3 || i == 2)) {
                        throw new IllegalArgumentException("auth.19");
                    }
            }
            this.selection = i;
        } else if (i < 0 || i > this.options.length) {
            throw new ArrayIndexOutOfBoundsException("auth.1B");
        } else {
            this.selection = i;
        }
    }
}
