package org.jivesoftware.smackx;

import org.jivesoftware.smack.util.StringUtils;

public class XHTMLText {
    private StringBuilder text = new StringBuilder(30);

    public XHTMLText(String str, String str2) {
        appendOpenBodyTag(str, str2);
    }

    private void appendOpenBodyTag(String str, String str2) {
        StringBuilder stringBuilder = new StringBuilder("<body");
        if (str != null) {
            stringBuilder.append(" style=\"");
            stringBuilder.append(str);
            stringBuilder.append("\"");
        }
        if (str2 != null) {
            stringBuilder.append(" xml:lang=\"");
            stringBuilder.append(str2);
            stringBuilder.append("\"");
        }
        stringBuilder.append(">");
        this.text.append(stringBuilder.toString());
    }

    private String closeBodyTag() {
        return "</body>";
    }

    public void append(String str) {
        this.text.append(StringUtils.escapeForXML(str));
    }

    public void appendBrTag() {
        this.text.append("<br/>");
    }

    public void appendCloseAnchorTag() {
        this.text.append("</a>");
    }

    public void appendCloseBlockQuoteTag() {
        this.text.append("</blockquote>");
    }

    public void appendCloseCodeTag() {
        this.text.append("</code>");
    }

    public void appendCloseEmTag() {
        this.text.append("</em>");
    }

    public void appendCloseHeaderTag(int i) {
        if (i <= 3 && i >= 1) {
            StringBuilder stringBuilder = new StringBuilder("</h");
            stringBuilder.append(i);
            stringBuilder.append(">");
            this.text.append(stringBuilder.toString());
        }
    }

    public void appendCloseInlinedQuoteTag() {
        this.text.append("</q>");
    }

    public void appendCloseOrderedListTag() {
        this.text.append("</ol>");
    }

    public void appendCloseParagraphTag() {
        this.text.append("</p>");
    }

    public void appendCloseSpanTag() {
        this.text.append("</span>");
    }

    public void appendCloseStrongTag() {
        this.text.append("</strong>");
    }

    public void appendCloseUnorderedListTag() {
        this.text.append("</ul>");
    }

    public void appendImageTag(String str, String str2, String str3, String str4, String str5) {
        StringBuilder stringBuilder = new StringBuilder("<img");
        if (str != null) {
            stringBuilder.append(" align=\"");
            stringBuilder.append(str);
            stringBuilder.append("\"");
        }
        if (str2 != null) {
            stringBuilder.append(" alt=\"");
            stringBuilder.append(str2);
            stringBuilder.append("\"");
        }
        if (str3 != null) {
            stringBuilder.append(" height=\"");
            stringBuilder.append(str3);
            stringBuilder.append("\"");
        }
        if (str4 != null) {
            stringBuilder.append(" src=\"");
            stringBuilder.append(str4);
            stringBuilder.append("\"");
        }
        if (str5 != null) {
            stringBuilder.append(" width=\"");
            stringBuilder.append(str5);
            stringBuilder.append("\"");
        }
        stringBuilder.append(">");
        this.text.append(stringBuilder.toString());
    }

    public void appendLineItemTag(String str) {
        StringBuilder stringBuilder = new StringBuilder("<li");
        if (str != null) {
            stringBuilder.append(" style=\"");
            stringBuilder.append(str);
            stringBuilder.append("\"");
        }
        stringBuilder.append(">");
        this.text.append(stringBuilder.toString());
    }

    public void appendOpenAnchorTag(String str, String str2) {
        StringBuilder stringBuilder = new StringBuilder("<a");
        if (str != null) {
            stringBuilder.append(" href=\"");
            stringBuilder.append(str);
            stringBuilder.append("\"");
        }
        if (str2 != null) {
            stringBuilder.append(" style=\"");
            stringBuilder.append(str2);
            stringBuilder.append("\"");
        }
        stringBuilder.append(">");
        this.text.append(stringBuilder.toString());
    }

    public void appendOpenBlockQuoteTag(String str) {
        StringBuilder stringBuilder = new StringBuilder("<blockquote");
        if (str != null) {
            stringBuilder.append(" style=\"");
            stringBuilder.append(str);
            stringBuilder.append("\"");
        }
        stringBuilder.append(">");
        this.text.append(stringBuilder.toString());
    }

    public void appendOpenCiteTag() {
        this.text.append("<cite>");
    }

    public void appendOpenCodeTag() {
        this.text.append("<code>");
    }

    public void appendOpenEmTag() {
        this.text.append("<em>");
    }

    public void appendOpenHeaderTag(int i, String str) {
        if (i <= 3 && i >= 1) {
            StringBuilder stringBuilder = new StringBuilder("<h");
            stringBuilder.append(i);
            if (str != null) {
                stringBuilder.append(" style=\"");
                stringBuilder.append(str);
                stringBuilder.append("\"");
            }
            stringBuilder.append(">");
            this.text.append(stringBuilder.toString());
        }
    }

    public void appendOpenInlinedQuoteTag(String str) {
        StringBuilder stringBuilder = new StringBuilder("<q");
        if (str != null) {
            stringBuilder.append(" style=\"");
            stringBuilder.append(str);
            stringBuilder.append("\"");
        }
        stringBuilder.append(">");
        this.text.append(stringBuilder.toString());
    }

    public void appendOpenOrderedListTag(String str) {
        StringBuilder stringBuilder = new StringBuilder("<ol");
        if (str != null) {
            stringBuilder.append(" style=\"");
            stringBuilder.append(str);
            stringBuilder.append("\"");
        }
        stringBuilder.append(">");
        this.text.append(stringBuilder.toString());
    }

    public void appendOpenParagraphTag(String str) {
        StringBuilder stringBuilder = new StringBuilder("<p");
        if (str != null) {
            stringBuilder.append(" style=\"");
            stringBuilder.append(str);
            stringBuilder.append("\"");
        }
        stringBuilder.append(">");
        this.text.append(stringBuilder.toString());
    }

    public void appendOpenSpanTag(String str) {
        StringBuilder stringBuilder = new StringBuilder("<span");
        if (str != null) {
            stringBuilder.append(" style=\"");
            stringBuilder.append(str);
            stringBuilder.append("\"");
        }
        stringBuilder.append(">");
        this.text.append(stringBuilder.toString());
    }

    public void appendOpenStrongTag() {
        this.text.append("<strong>");
    }

    public void appendOpenUnorderedListTag(String str) {
        StringBuilder stringBuilder = new StringBuilder("<ul");
        if (str != null) {
            stringBuilder.append(" style=\"");
            stringBuilder.append(str);
            stringBuilder.append("\"");
        }
        stringBuilder.append(">");
        this.text.append(stringBuilder.toString());
    }

    public String toString() {
        return this.text.toString().concat(closeBodyTag());
    }
}
