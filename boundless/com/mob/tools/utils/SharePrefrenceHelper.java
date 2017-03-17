package com.mob.tools.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Base64;
import com.mob.tools.MobLog;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SharePrefrenceHelper {
    private Context context;
    private SharedPreferences prefrence;

    public SharePrefrenceHelper(Context c) {
        this.context = c.getApplicationContext();
    }

    public void open(String name) {
        open(name, 0);
    }

    public void open(String name, int version) {
        this.prefrence = this.context.getSharedPreferences(name + "_" + version, 0);
    }

    public void putString(String key, String value) {
        Editor editor = this.prefrence.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return this.prefrence.getString(key, "");
    }

    public void putBoolean(String key, Boolean value) {
        Editor editor = this.prefrence.edit();
        editor.putBoolean(key, value.booleanValue());
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return this.prefrence.getBoolean(key, false);
    }

    public void putLong(String key, Long value) {
        Editor editor = this.prefrence.edit();
        editor.putLong(key, value.longValue());
        editor.commit();
    }

    public long getLong(String key) {
        return this.prefrence.getLong(key, 0);
    }

    public void putInt(String key, Integer value) {
        Editor editor = this.prefrence.edit();
        editor.putInt(key, value.intValue());
        editor.commit();
    }

    public int getInt(String key) {
        return this.prefrence.getInt(key, 0);
    }

    public void putFloat(String key, Float value) {
        Editor editor = this.prefrence.edit();
        editor.putFloat(key, value.floatValue());
        editor.commit();
    }

    public float getFloat(String key) {
        return this.prefrence.getFloat(key, 0.0f);
    }

    public void put(String key, Object value) {
        if (value != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(value);
                oos.flush();
                oos.close();
                putString(key, Base64.encodeToString(baos.toByteArray(), 2));
            } catch (Throwable t) {
                MobLog.getInstance().w(t);
            }
        }
    }

    public Object get(String key) {
        try {
            String base64 = getString(key);
            if (TextUtils.isEmpty(base64)) {
                return null;
            }
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(Base64.decode(base64, 2)));
            Object value = ois.readObject();
            ois.close();
            return value;
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return null;
        }
    }

    public void remove(String key) {
        Editor editor = this.prefrence.edit();
        editor.remove(key);
        editor.commit();
    }
}
