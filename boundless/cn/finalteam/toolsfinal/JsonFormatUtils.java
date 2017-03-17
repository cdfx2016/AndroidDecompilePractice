package cn.finalteam.toolsfinal;

import com.easemob.util.HanziToPinyin.Token;
import com.xiaomi.mipush.sdk.Constants;
import java.util.ArrayList;

public class JsonFormatUtils {
    public static String formatJson(String json) {
        String fillStringUnit = "\t";
        if (json == null || json.trim().length() == 0) {
            return "";
        }
        int fixedLenth = 0;
        ArrayList<String> tokenList = new ArrayList();
        String jsonTemp = json;
        while (jsonTemp.length() > 0) {
            String token = getToken(jsonTemp);
            jsonTemp = jsonTemp.substring(token.length());
            tokenList.add(token.trim());
        }
        int i = 0;
        while (i < tokenList.size()) {
            int length = ((String) tokenList.get(i)).getBytes().length;
            if (length > fixedLenth && i < tokenList.size() - 1 && ((String) tokenList.get(i + 1)).equals(":")) {
                fixedLenth = length;
            }
            i++;
        }
        StringBuilder buf = new StringBuilder();
        int count = 0;
        i = 0;
        while (i < tokenList.size()) {
            token = (String) tokenList.get(i);
            if (token.equals(Constants.ACCEPT_TIME_SEPARATOR_SP)) {
                buf.append(token);
                doFill(buf, count, fillStringUnit);
            } else if (token.equals(":")) {
                buf.append(Token.SEPARATOR).append(token).append(Token.SEPARATOR);
            } else if (token.equals("{")) {
                if (((String) tokenList.get(i + 1)).equals("}")) {
                    i++;
                    buf.append("{ }");
                } else {
                    count++;
                    buf.append(token);
                    doFill(buf, count, fillStringUnit);
                }
            } else if (token.equals("}")) {
                count--;
                doFill(buf, count, fillStringUnit);
                buf.append(token);
            } else if (token.equals("[")) {
                if (((String) tokenList.get(i + 1)).equals("]")) {
                    i++;
                    buf.append("[ ]");
                } else {
                    count++;
                    buf.append(token);
                    doFill(buf, count, fillStringUnit);
                }
            } else if (token.equals("]")) {
                count--;
                doFill(buf, count, fillStringUnit);
                buf.append(token);
            } else {
                buf.append(token);
                if (i < tokenList.size() - 1 && ((String) tokenList.get(i + 1)).equals(":")) {
                    int fillLength = fixedLenth - token.getBytes().length;
                    if (fillLength > 0) {
                        for (int j = 0; j < fillLength; j++) {
                            buf.append(Token.SEPARATOR);
                        }
                    }
                }
            }
            i++;
        }
        return buf.toString();
    }

    private static String getToken(String json) {
        StringBuilder buf = new StringBuilder();
        boolean isInYinHao = false;
        while (json.length() > 0) {
            String token = json.substring(0, 1);
            json = json.substring(1);
            if (isInYinHao || !(token.equals(":") || token.equals("{") || token.equals("}") || token.equals("[") || token.equals("]") || token.equals(Constants.ACCEPT_TIME_SEPARATOR_SP))) {
                if (token.equals("\\")) {
                    buf.append(token);
                    buf.append(json.substring(0, 1));
                    json = json.substring(1);
                } else if (token.equals("\"")) {
                    buf.append(token);
                    if (isInYinHao) {
                        break;
                    }
                    isInYinHao = true;
                } else {
                    buf.append(token);
                }
            } else if (buf.toString().trim().length() == 0) {
                buf.append(token);
            }
        }
        return buf.toString();
    }

    private static void doFill(StringBuilder buf, int count, String fillStringUnit) {
        buf.append("\n");
        for (int i = 0; i < count; i++) {
            buf.append(fillStringUnit);
        }
    }
}
