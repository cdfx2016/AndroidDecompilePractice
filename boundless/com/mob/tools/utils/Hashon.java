package com.mob.tools.utils;

import android.text.TextUtils;
import com.mob.tools.MobLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Hashon {
    public <T> HashMap<String, T> fromJson(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return new HashMap();
        }
        try {
            if (jsonStr.startsWith("[") && jsonStr.endsWith("]")) {
                jsonStr = "{\"fakelist\":" + jsonStr + "}";
            }
            return fromJson(new JSONObject(jsonStr));
        } catch (Throwable t) {
            MobLog.getInstance().w(jsonStr, new Object[0]);
            MobLog.getInstance().w(t);
            return new HashMap();
        }
    }

    private <T> HashMap<String, T> fromJson(JSONObject json) throws JSONException {
        HashMap<String, T> map = new HashMap();
        Iterator<String> iKey = json.keys();
        while (iKey.hasNext()) {
            String key = (String) iKey.next();
            Object value = json.opt(key);
            if (JSONObject.NULL.equals(value)) {
                value = null;
            }
            if (value != null) {
                if (value instanceof JSONObject) {
                    value = fromJson((JSONObject) value);
                } else if (value instanceof JSONArray) {
                    value = fromJson((JSONArray) value);
                }
                map.put(key, value);
            }
        }
        return map;
    }

    private ArrayList<Object> fromJson(JSONArray array) throws JSONException {
        ArrayList<Object> list = new ArrayList();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            Object value = array.opt(i);
            if (value instanceof JSONObject) {
                value = fromJson((JSONObject) value);
            } else if (value instanceof JSONArray) {
                value = fromJson((JSONArray) value);
            }
            list.add(value);
        }
        return list;
    }

    public <T> String fromHashMap(HashMap<String, T> map) {
        try {
            JSONObject obj = getJSONObject(map);
            if (obj == null) {
                return "";
            }
            return obj.toString();
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return "";
        }
    }

    private <T> JSONObject getJSONObject(HashMap<String, T> map) throws JSONException {
        JSONObject json = new JSONObject();
        for (Entry<String, T> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof HashMap) {
                value = getJSONObject((HashMap) value);
            } else if (value instanceof ArrayList) {
                value = getJSONArray((ArrayList) value);
            } else if (isBasicArray(value)) {
                value = getJSONArray(arrayToList(value));
            }
            json.put((String) entry.getKey(), value);
        }
        return json;
    }

    private boolean isBasicArray(Object value) {
        return (value instanceof byte[]) || (value instanceof short[]) || (value instanceof int[]) || (value instanceof long[]) || (value instanceof float[]) || (value instanceof double[]) || (value instanceof char[]) || (value instanceof boolean[]) || (value instanceof String[]);
    }

    private ArrayList<?> arrayToList(Object value) {
        if (value instanceof byte[]) {
            ArrayList<?> arrayList = new ArrayList();
            for (byte item : (byte[]) value) {
                arrayList.add(Byte.valueOf(item));
            }
            return arrayList;
        } else if (value instanceof short[]) {
            ArrayList<Short> list = new ArrayList();
            for (short item2 : (short[]) value) {
                list.add(Short.valueOf(item2));
            }
            return list;
        } else if (value instanceof int[]) {
            ArrayList<Integer> list2 = new ArrayList();
            for (int item3 : (int[]) value) {
                list2.add(Integer.valueOf(item3));
            }
            return list2;
        } else if (value instanceof long[]) {
            ArrayList<Long> list3 = new ArrayList();
            for (long item4 : (long[]) value) {
                list3.add(Long.valueOf(item4));
            }
            return list3;
        } else if (value instanceof float[]) {
            ArrayList<Float> list4 = new ArrayList();
            for (float item5 : (float[]) value) {
                list4.add(Float.valueOf(item5));
            }
            return list4;
        } else if (value instanceof double[]) {
            ArrayList<Double> list5 = new ArrayList();
            for (double item6 : (double[]) value) {
                list5.add(Double.valueOf(item6));
            }
            return list5;
        } else if (value instanceof char[]) {
            ArrayList<Character> list6 = new ArrayList();
            for (char item7 : (char[]) value) {
                list6.add(Character.valueOf(item7));
            }
            return list6;
        } else if (value instanceof boolean[]) {
            ArrayList<Boolean> list7 = new ArrayList();
            for (boolean item8 : (boolean[]) value) {
                list7.add(Boolean.valueOf(item8));
            }
            return list7;
        } else if (!(value instanceof String[])) {
            return null;
        } else {
            ArrayList<String> list8 = new ArrayList();
            for (String item9 : (String[]) value) {
                list8.add(item9);
            }
            return list8;
        }
    }

    private JSONArray getJSONArray(ArrayList<Object> list) throws JSONException {
        JSONArray array = new JSONArray();
        Iterator i$ = list.iterator();
        while (i$.hasNext()) {
            Object value = i$.next();
            if (value instanceof HashMap) {
                value = getJSONObject((HashMap) value);
            } else if (value instanceof ArrayList) {
                value = getJSONArray((ArrayList) value);
            }
            array.put(value);
        }
        return array;
    }

    public String format(String jsonStr) {
        try {
            return format("", fromJson(jsonStr));
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return "";
        }
    }

    private String format(String sepStr, HashMap<String, Object> map) {
        StringBuffer sb = new StringBuffer();
        sb.append("{\n");
        String mySepStr = sepStr + "\t";
        int i = 0;
        for (Entry<String, Object> entry : map.entrySet()) {
            if (i > 0) {
                sb.append(",\n");
            }
            sb.append(mySepStr).append('\"').append((String) entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof HashMap) {
                sb.append(format(mySepStr, (HashMap) value));
            } else if (value instanceof ArrayList) {
                sb.append(format(mySepStr, (ArrayList) value));
            } else if (value instanceof String) {
                sb.append('\"').append(value).append('\"');
            } else {
                sb.append(value);
            }
            i++;
        }
        sb.append('\n').append(sepStr).append('}');
        return sb.toString();
    }

    private String format(String sepStr, ArrayList<Object> list) {
        StringBuffer sb = new StringBuffer();
        sb.append("[\n");
        String mySepStr = sepStr + "\t";
        int i = 0;
        Iterator i$ = list.iterator();
        while (i$.hasNext()) {
            Object value = i$.next();
            if (i > 0) {
                sb.append(",\n");
            }
            sb.append(mySepStr);
            if (value instanceof HashMap) {
                sb.append(format(mySepStr, (HashMap) value));
            } else if (value instanceof ArrayList) {
                sb.append(format(mySepStr, (ArrayList) value));
            } else if (value instanceof String) {
                sb.append('\"').append(value).append('\"');
            } else {
                sb.append(value);
            }
            i++;
        }
        sb.append('\n').append(sepStr).append(']');
        return sb.toString();
    }
}
