package com.easemob.chat;

import com.easemob.chat.EMMessage.Direct;
import com.easemob.chat.EMMessage.Status;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.core.b;
import com.easemob.util.EMLog;
import com.xiaomi.mipush.sdk.Constants;
import java.util.Iterator;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Body;
import org.json.JSONArray;
import org.json.JSONObject;

class MessageEncoder {
    public static final String ATTR_ACTION = "action";
    public static final String ATTR_ADDRESS = "addr";
    public static final String ATTR_EXT = "ext";
    public static final String ATTR_FILENAME = "filename";
    public static final String ATTR_FROM = "from";
    public static final String ATTR_LATITUDE = "lat";
    public static final String ATTR_LENGTH = "length";
    public static final String ATTR_LOCALURL = "localurl";
    public static final String ATTR_LONGITUDE = "lng";
    public static final String ATTR_MSG = "msg";
    public static final String ATTR_PARAM = "param";
    public static final String ATTR_SECRET = "secret";
    public static final String ATTR_THUMBNAIL = "thumb";
    public static final String ATTR_THUMBNAIL_SECRET = "thumb_secret";
    public static final String ATTR_TO = "to";
    public static final String ATTR_TYPE = "type";
    private static final String ATTR_TYPE_CMD = "cmd";
    private static final String ATTR_TYPE_IMG = "img";
    private static final String ATTR_TYPE_LOCATION = "loc";
    private static final String ATTR_TYPE_TXT = "txt";
    private static final String ATTR_TYPE_VOICE = "audio";
    public static final String ATTR_URL = "url";
    private static final String TAG = "encoder";

    MessageEncoder() {
    }

    private static void addCmdBody(StringBuffer stringBuffer, EMMessage eMMessage) {
        stringBuffer.append("\"type\":\"cmd\",");
        CmdMessageBody cmdMessageBody = (CmdMessageBody) eMMessage.body;
        stringBuffer.append("\"action\":\"" + cmdMessageBody.action + "\",");
        stringBuffer.append("\"param\":[");
        if (cmdMessageBody.params != null) {
            for (String str : cmdMessageBody.params) {
                stringBuffer.append("\"" + str + "\",");
            }
        }
        stringBuffer.append("]");
    }

    private static void addExtAttr(StringBuffer stringBuffer, EMMessage eMMessage) {
        stringBuffer.append(Constants.ACCEPT_TIME_SEPARATOR_SP);
        stringBuffer.append("\"ext\":{");
        int i = 1;
        for (String str : eMMessage.attributes.keySet()) {
            String str2;
            stringBuffer.append("\"" + str2 + "\":");
            Object obj = eMMessage.attributes.get(str2);
            if (obj instanceof String) {
                str2 = (String) obj;
                if (str2.startsWith("{") && str2.endsWith("}") && str2.contains(":")) {
                    stringBuffer.append(obj);
                } else {
                    stringBuffer.append("\"" + obj + "\"");
                }
            } else if (!(obj instanceof Boolean)) {
                stringBuffer.append(((Integer) obj).toString());
            } else if (((Boolean) obj).booleanValue()) {
                stringBuffer.append("true");
            } else {
                stringBuffer.append("false");
            }
            if (i < eMMessage.attributes.size()) {
                stringBuffer.append(Constants.ACCEPT_TIME_SEPARATOR_SP);
            }
            i++;
        }
        stringBuffer.append("}");
    }

    private static void addImageBody(StringBuffer stringBuffer, EMMessage eMMessage, boolean z) {
        stringBuffer.append("\"type\":\"img\",");
        ImageMessageBody imageMessageBody = (ImageMessageBody) eMMessage.body;
        stringBuffer.append("\"url\":\"" + imageMessageBody.remoteUrl + "\",");
        if (z) {
            stringBuffer.append("\"localurl\":\"" + imageMessageBody.localUrl + "\",");
        }
        stringBuffer.append("\"filename\":\"" + imageMessageBody.fileName + "\",");
        stringBuffer.append("\"thumb\":\"" + imageMessageBody.thumbnailUrl + "\",");
        stringBuffer.append("\"secret\":\"" + imageMessageBody.secret + "\"");
        if (imageMessageBody.thumbnailSecret != null) {
            stringBuffer.append(",\"thumb_secret\":\"" + imageMessageBody.thumbnailSecret + "\"");
        }
    }

    private static void addLocationBody(StringBuffer stringBuffer, EMMessage eMMessage) {
        stringBuffer.append("\"type\":\"loc\",");
        LocationMessageBody locationMessageBody = (LocationMessageBody) eMMessage.body;
        stringBuffer.append("\"addr\":\"" + locationMessageBody.address + "\",");
        stringBuffer.append("\"lat\":" + locationMessageBody.latitude + Constants.ACCEPT_TIME_SEPARATOR_SP);
        stringBuffer.append("\"lng\":" + locationMessageBody.longitude);
    }

    private static void addTxtBody(StringBuffer stringBuffer, EMMessage eMMessage) {
        stringBuffer.append("\"type\":\"txt\",");
        stringBuffer.append("\"msg\":\"" + ((TextMessageBody) eMMessage.body).message + "\"");
    }

    private static void addVoiceBody(StringBuffer stringBuffer, EMMessage eMMessage, boolean z) {
        stringBuffer.append("\"type\":\"audio\",");
        VoiceMessageBody voiceMessageBody = (VoiceMessageBody) eMMessage.body;
        stringBuffer.append("\"url\":\"" + voiceMessageBody.remoteUrl + "\",");
        if (z) {
            stringBuffer.append("\"localurl\":\"" + voiceMessageBody.localUrl + "\",");
        }
        stringBuffer.append("\"filename\":\"" + voiceMessageBody.fileName + "\",");
        stringBuffer.append("\"length\":" + voiceMessageBody.length + Constants.ACCEPT_TIME_SEPARATOR_SP);
        stringBuffer.append("\"secret\":\"" + voiceMessageBody.secret + "\"");
    }

    public static String getJSONMsg(EMMessage eMMessage, boolean z) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{");
        stringBuffer.append("\"from\":\"" + eMMessage.from.username + "\",");
        stringBuffer.append("\"to\":\"" + eMMessage.to.username + "\",");
        stringBuffer.append("\"bodies\":[{");
        if (eMMessage.type == Type.TXT) {
            addTxtBody(stringBuffer, eMMessage);
        } else if (eMMessage.type == Type.IMAGE) {
            addImageBody(stringBuffer, eMMessage, z);
        } else if (eMMessage.type == Type.VOICE) {
            addVoiceBody(stringBuffer, eMMessage, z);
        } else if (eMMessage.type == Type.LOCATION) {
            addLocationBody(stringBuffer, eMMessage);
        } else if (eMMessage.type == Type.CMD) {
            addCmdBody(stringBuffer, eMMessage);
        }
        stringBuffer.append("}]");
        if (eMMessage.attributes != null) {
            addExtAttr(stringBuffer, eMMessage);
        }
        stringBuffer.append("}");
        stringBuffer.toString();
        return stringBuffer.toString();
    }

    public static EMMessage getMsgFromJson(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            EMContact eMContact = new EMContact(jSONObject.getString("from"));
            EMContact eMContact2 = new EMContact(jSONObject.getString("to"));
            JSONArray jSONArray = jSONObject.getJSONArray("bodies");
            if (jSONArray.length() < 1) {
                EMLog.d(TAG, "wrong msg without body");
                return null;
            }
            EMMessage eMMessage;
            JSONObject jSONObject2 = jSONArray.getJSONObject(0);
            String string = jSONObject2.getString(ATTR_TYPE);
            if (string.equals(ATTR_TYPE_TXT)) {
                eMMessage = new EMMessage(Type.TXT);
                eMMessage.addBody(new TextMessageBody(jSONObject2.getString("msg")));
            } else if (string.equals(ATTR_TYPE_IMG)) {
                eMMessage = new EMMessage(Type.IMAGE);
                r5 = new ImageMessageBody(jSONObject2.getString(ATTR_FILENAME), jSONObject2.getString(ATTR_URL), jSONObject2.getString(ATTR_THUMBNAIL));
                if (jSONObject2.has(ATTR_LOCALURL)) {
                    r5.localUrl = jSONObject2.getString(ATTR_LOCALURL);
                }
                if (jSONObject2.has(ATTR_SECRET)) {
                    r5.setSecret(jSONObject2.getString(ATTR_SECRET));
                }
                if (jSONObject2.has(ATTR_THUMBNAIL_SECRET)) {
                    r5.setSecret(jSONObject2.getString(ATTR_THUMBNAIL_SECRET));
                }
                eMMessage.addBody(r5);
            } else if (string.equals("audio")) {
                eMMessage = new EMMessage(Type.VOICE);
                r5 = new VoiceMessageBody(jSONObject2.getString(ATTR_FILENAME), jSONObject2.getString(ATTR_URL), jSONObject2.getInt(ATTR_LENGTH));
                if (jSONObject2.has(ATTR_LOCALURL)) {
                    r5.localUrl = jSONObject2.getString(ATTR_LOCALURL);
                }
                if (jSONObject2.has(ATTR_SECRET)) {
                    r5.setSecret(jSONObject2.getString(ATTR_SECRET));
                }
                eMMessage.addBody(r5);
            } else if (string.equals(ATTR_TYPE_LOCATION)) {
                EMMessage eMMessage2 = new EMMessage(Type.LOCATION);
                eMMessage2.addBody(new LocationMessageBody(jSONObject2.getString(ATTR_ADDRESS), jSONObject2.getDouble(ATTR_LATITUDE), jSONObject2.getDouble(ATTR_LONGITUDE)));
                eMMessage = eMMessage2;
            } else if (string.equals(ATTR_TYPE_CMD)) {
                eMMessage = new EMMessage(Type.CMD);
                String[] strArr = (String[]) null;
                if (jSONObject2.has(ATTR_PARAM)) {
                    JSONArray jSONArray2 = jSONObject2.getJSONArray(ATTR_PARAM);
                    strArr = new String[jSONArray2.length()];
                    for (int i = 0; i < strArr.length; i++) {
                        strArr[i] = jSONArray2.optString(i);
                    }
                }
                eMMessage.addBody(new CmdMessageBody(jSONObject2.getString(ATTR_ACTION), strArr));
            } else {
                eMMessage = null;
            }
            if (eMMessage != null) {
                eMMessage.from = eMContact;
                eMMessage.to = eMContact2;
            }
            if (jSONObject.has(ATTR_EXT)) {
                JSONObject jSONObject3 = jSONObject.getJSONObject(ATTR_EXT);
                Iterator keys = jSONObject3.keys();
                while (keys.hasNext()) {
                    string = (String) keys.next();
                    Object obj = jSONObject3.get(string);
                    if (obj instanceof String) {
                        eMMessage.setAttribute(string, (String) obj);
                    } else if (obj instanceof Integer) {
                        eMMessage.setAttribute(string, ((Integer) obj).intValue());
                    } else if (obj instanceof Boolean) {
                        eMMessage.setAttribute(string, ((Boolean) obj).booleanValue());
                    } else if (obj instanceof JSONObject) {
                        eMMessage.setAttribute(string, ((JSONObject) obj).toString());
                    } else {
                        EMLog.e("msg", "unknow additonal msg attr:" + obj.getClass().getName());
                    }
                }
            }
            return eMMessage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static EMMessage parseXmppMsg(Message message) {
        if (message.getExtension(b.a, b.b) != null) {
            EMLog.d(TAG, "it is encrypted message, decripting");
            try {
                String decryptMessage = EMEncryptUtils.decryptMessage(message.getBody(), EMContactManager.getUserNameFromEid(message.getFrom()));
                for (Body removeBody : message.getBodies()) {
                    message.removeBody(removeBody);
                }
                message.setBody(decryptMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String body = message.getBody();
        if (body.startsWith("{") && body.endsWith("}")) {
            EMMessage msgFromJson = getMsgFromJson(body);
            if (msgFromJson == null) {
                EMLog.e(TAG, "wrong message format:" + message.toXML());
                return null;
            }
            msgFromJson.direct = Direct.RECEIVE;
            msgFromJson.msgId = message.getPacketID();
            msgFromJson.status = Status.CREATE;
            msgFromJson.unread = true;
            return msgFromJson;
        }
        EMLog.d(TAG, "msg not in json format, ignore");
        return null;
    }
}
