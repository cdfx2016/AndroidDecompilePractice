package com.mob.tools.utils;

import android.text.TextUtils;
import android.util.Base64;
import com.mob.tools.MobLog;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class LocalDB {
    private File dbFile;
    private HashMap<String, Object> map;

    public void open(String filePath) {
        try {
            if (!TextUtils.isEmpty(filePath)) {
                this.dbFile = new File(filePath);
                if (this.dbFile.exists()) {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.dbFile));
                    this.map = (HashMap) ois.readObject();
                    ois.close();
                }
            }
        } catch (Throwable t) {
            MobLog.getInstance().d(t);
        }
    }

    private void commit() {
        if (this.map != null && this.dbFile != null) {
            try {
                if (!this.dbFile.getParentFile().exists()) {
                    this.dbFile.getParentFile().mkdirs();
                }
                synchronized (this.map) {
                    FileOutputStream fos = new FileOutputStream(this.dbFile);
                    if (fos.getChannel().tryLock() != null) {
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(this.map);
                        oos.flush();
                        oos.close();
                    } else {
                        fos.close();
                    }
                }
            } catch (Throwable t) {
                MobLog.getInstance().w(t);
            }
        }
    }

    private void put(String key, Object value) {
        if (this.map == null) {
            this.map = new HashMap();
        }
        this.map.put(key, value);
    }

    private Object get(String key) {
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }

    public void putString(String key, String value) {
        put(key, value);
        commit();
    }

    public String getString(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return String.valueOf(value);
    }

    public void putBoolean(String key, Boolean value) {
        put(key, value);
        commit();
    }

    public boolean getBoolean(String key) {
        Object value = get(key);
        if (value != null && (value instanceof Boolean)) {
            return ((Boolean) value).booleanValue();
        }
        return false;
    }

    public void putLong(String key, Long value) {
        put(key, value);
        commit();
    }

    public long getLong(String key) {
        Object value = get(key);
        if (value != null && (value instanceof Long)) {
            return ((Long) value).longValue();
        }
        return 0;
    }

    public void putInt(String key, Integer value) {
        put(key, value);
        commit();
    }

    public int getInt(String key) {
        Object value = get(key);
        if (value != null && (value instanceof Integer)) {
            return ((Integer) value).intValue();
        }
        return 0;
    }

    public void putFloat(String key, Float value) {
        put(key, value);
        commit();
    }

    public float getFloat(String key) {
        Object value = get(key);
        if (value != null && (value instanceof Integer)) {
            return ((Float) value).floatValue();
        }
        return 0.0f;
    }

    public void remove(String key) {
        if (this.map == null) {
            this.map = new HashMap();
        }
        this.map.remove(key);
        commit();
    }

    public void putObject(String key, Object value) {
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

    public Object getObject(String key) {
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
}
