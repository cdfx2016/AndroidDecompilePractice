package cn.finalteam.toolsfinal;

import android.content.Context;
import cn.finalteam.toolsfinal.coder.MD5Coder;
import cn.finalteam.toolsfinal.io.FileUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class AppCacheUtils {
    public static final String DEFAULT_CACHE_NAME = "appCache";
    private static Map<String, AppCacheUtils> mCacheUtilsMap = new HashMap();
    private File mCacheFile;

    private AppCacheUtils(File cacheFile) {
        this.mCacheFile = cacheFile;
        FileUtils.mkdirs(cacheFile);
    }

    public static AppCacheUtils getInstance(Context ctx) {
        return getInstance(StorageUtils.getIndividualCacheDirectory(ctx));
    }

    public static AppCacheUtils getInstance(String path, String cacheDirName) {
        return getInstance(new File(path, cacheDirName));
    }

    public static AppCacheUtils getInstance(Context ctx, String cacheDirName) {
        return getInstance(new File(StorageUtils.getIndividualCacheDirectory(ctx), cacheDirName));
    }

    public static AppCacheUtils getInstance(File file) {
        AppCacheUtils appCacheUtils = (AppCacheUtils) mCacheUtilsMap.get(file.getAbsolutePath());
        if (appCacheUtils != null) {
            return appCacheUtils;
        }
        appCacheUtils = new AppCacheUtils(file);
        mCacheUtilsMap.put(file.getAbsolutePath(), appCacheUtils);
        return appCacheUtils;
    }

    public void put(String key, int value) {
        put(key, value + "");
    }

    public void put(String key, float value) {
        put(key, value + "");
    }

    public void put(String key, double value) {
        put(key, value + "");
    }

    public void put(String key, boolean value) {
        put(key, value + "");
    }

    public void put(String key, long value) {
        put(key, value + "");
    }

    public void put(String key, String value) {
        IOException e;
        Throwable th;
        if (!StringUtils.isEmpty(key)) {
            if (StringUtils.isEmpty(value)) {
                value = "";
            }
            BufferedWriter out = null;
            try {
                BufferedWriter out2 = new BufferedWriter(new FileWriter(newFile(key)), 1024);
                try {
                    out2.write(value);
                    if (out2 != null) {
                        try {
                            out2.flush();
                            out2.close();
                            out = out2;
                            return;
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            out = out2;
                            return;
                        }
                    }
                } catch (IOException e3) {
                    e2 = e3;
                    out = out2;
                    try {
                        e2.printStackTrace();
                        if (out != null) {
                            try {
                                out.flush();
                                out.close();
                            } catch (IOException e22) {
                                e22.printStackTrace();
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (out != null) {
                            try {
                                out.flush();
                                out.close();
                            } catch (IOException e222) {
                                e222.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    out = out2;
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                    throw th;
                }
            } catch (IOException e4) {
                e222 = e4;
                e222.printStackTrace();
                if (out != null) {
                    out.flush();
                    out.close();
                }
            }
        }
    }

    public void put(String key, byte[] value) {
        Exception e;
        Throwable th;
        if (value != null && value.length != 0 && !StringUtils.isEmpty(key)) {
            FileOutputStream out = null;
            try {
                FileOutputStream out2 = new FileOutputStream(newFile(key));
                try {
                    out2.write(value);
                    if (out2 != null) {
                        try {
                            out2.flush();
                            out2.close();
                            out = out2;
                            return;
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            out = out2;
                            return;
                        }
                    }
                } catch (Exception e3) {
                    e = e3;
                    out = out2;
                    try {
                        e.printStackTrace();
                        if (out != null) {
                            try {
                                out.flush();
                                out.close();
                            } catch (IOException e22) {
                                e22.printStackTrace();
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (out != null) {
                            try {
                                out.flush();
                                out.close();
                            } catch (IOException e222) {
                                e222.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    out = out2;
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                    throw th;
                }
            } catch (Exception e4) {
                e = e4;
                e.printStackTrace();
                if (out != null) {
                    out.flush();
                    out.close();
                }
            }
        }
    }

    public void put(String key, JSONArray value) {
        if (value != null) {
            put(key, value.toString());
        }
    }

    public void put(String key, JSONObject value) {
        if (value != null) {
            put(key, value.toString());
        }
    }

    public void put(String key, Serializable value) {
        ByteArrayOutputStream byteArrayOutputStream;
        Exception e;
        Throwable th;
        if (!StringUtils.isEmpty(key) && value != null) {
            ObjectOutputStream oos = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream oos2 = new ObjectOutputStream(baos);
                    try {
                        oos2.writeObject(value);
                        put(key, baos.toByteArray());
                        try {
                            oos2.close();
                            oos = oos2;
                            byteArrayOutputStream = baos;
                        } catch (IOException e2) {
                            oos = oos2;
                            byteArrayOutputStream = baos;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        oos = oos2;
                        byteArrayOutputStream = baos;
                        try {
                            e.printStackTrace();
                            try {
                                oos.close();
                            } catch (IOException e4) {
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            try {
                                oos.close();
                            } catch (IOException e5) {
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        oos = oos2;
                        byteArrayOutputStream = baos;
                        oos.close();
                        throw th;
                    }
                } catch (Exception e6) {
                    e = e6;
                    byteArrayOutputStream = baos;
                    e.printStackTrace();
                    oos.close();
                } catch (Throwable th4) {
                    th = th4;
                    byteArrayOutputStream = baos;
                    oos.close();
                    throw th;
                }
            } catch (Exception e7) {
                e = e7;
                e.printStackTrace();
                oos.close();
            }
        }
    }

    public int getInt(String key, int defValue) {
        String sValue = getString(key);
        if (!StringUtils.isEmpty(sValue)) {
            try {
                return Integer.parseInt(sValue);
            } catch (Exception e) {
            }
        }
        return defValue;
    }

    public float getFloat(String key, float defValue) {
        String sValue = getString(key);
        if (!StringUtils.isEmpty(sValue)) {
            try {
                return Float.parseFloat(sValue);
            } catch (Exception e) {
            }
        }
        return defValue;
    }

    public Double getDouble(String key, double defValue) {
        String sValue = getString(key);
        if (!StringUtils.isEmpty(sValue)) {
            try {
                return Double.valueOf(Double.parseDouble(sValue));
            } catch (Exception e) {
            }
        }
        return Double.valueOf(defValue);
    }

    public long getLong(String key, long defValue) {
        String sValue = getString(key);
        if (!StringUtils.isEmpty(sValue)) {
            try {
                return Long.parseLong(sValue);
            } catch (Exception e) {
            }
        }
        return defValue;
    }

    public boolean getBoolean(String key, boolean defValue) {
        String sValue = getString(key);
        if (!StringUtils.isEmpty(sValue)) {
            try {
                return Boolean.parseBoolean(sValue);
            } catch (Exception e) {
            }
        }
        return defValue;
    }

    public String getString(String key) {
        IOException e;
        Throwable th;
        String str = null;
        if (!StringUtils.isEmpty(key)) {
            File file = newFile(key);
            if (file.exists()) {
                BufferedReader in = null;
                str = "";
                try {
                    BufferedReader in2 = new BufferedReader(new FileReader(file));
                    while (true) {
                        try {
                            String currentLine = in2.readLine();
                            if (currentLine == null) {
                                break;
                            }
                            str = str + currentLine;
                        } catch (IOException e2) {
                            e = e2;
                            in = in2;
                        } catch (Throwable th2) {
                            th = th2;
                            in = in2;
                        }
                    }
                    if (in2 != null) {
                        try {
                            in2.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                } catch (IOException e4) {
                    e3 = e4;
                    try {
                        e3.printStackTrace();
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e32) {
                                e32.printStackTrace();
                            }
                        }
                        return str;
                    } catch (Throwable th3) {
                        th = th3;
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e322) {
                                e322.printStackTrace();
                            }
                        }
                        throw th;
                    }
                }
            }
        }
        return str;
    }

    public Object getObject(String key) {
        Exception e;
        Throwable th;
        Object obj = null;
        if (!StringUtils.isEmpty(key)) {
            byte[] data = getBinary(key);
            if (data != null) {
                ByteArrayInputStream bais = null;
                ObjectInputStream ois = null;
                try {
                    ByteArrayInputStream bais2 = new ByteArrayInputStream(data);
                    try {
                        ObjectInputStream ois2 = new ObjectInputStream(bais2);
                        try {
                            obj = ois2.readObject();
                            if (bais2 != null) {
                                try {
                                    bais2.close();
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                            }
                            if (ois2 != null) {
                                try {
                                    ois2.close();
                                } catch (IOException e22) {
                                    e22.printStackTrace();
                                }
                            }
                        } catch (Exception e3) {
                            e = e3;
                            ois = ois2;
                            bais = bais2;
                            try {
                                e.printStackTrace();
                                if (bais != null) {
                                    try {
                                        bais.close();
                                    } catch (IOException e222) {
                                        e222.printStackTrace();
                                    }
                                }
                                if (ois != null) {
                                    try {
                                        ois.close();
                                    } catch (IOException e2222) {
                                        e2222.printStackTrace();
                                    }
                                }
                                return obj;
                            } catch (Throwable th2) {
                                th = th2;
                                if (bais != null) {
                                    try {
                                        bais.close();
                                    } catch (IOException e22222) {
                                        e22222.printStackTrace();
                                    }
                                }
                                if (ois != null) {
                                    try {
                                        ois.close();
                                    } catch (IOException e222222) {
                                        e222222.printStackTrace();
                                    }
                                }
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            ois = ois2;
                            bais = bais2;
                            if (bais != null) {
                                bais.close();
                            }
                            if (ois != null) {
                                ois.close();
                            }
                            throw th;
                        }
                    } catch (Exception e4) {
                        e = e4;
                        bais = bais2;
                        e.printStackTrace();
                        if (bais != null) {
                            bais.close();
                        }
                        if (ois != null) {
                            ois.close();
                        }
                        return obj;
                    } catch (Throwable th4) {
                        th = th4;
                        bais = bais2;
                        if (bais != null) {
                            bais.close();
                        }
                        if (ois != null) {
                            ois.close();
                        }
                        throw th;
                    }
                } catch (Exception e5) {
                    e = e5;
                    e.printStackTrace();
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                    return obj;
                }
            }
        }
        return obj;
    }

    public byte[] getBinary(String key) {
        Exception e;
        Throwable th;
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        RandomAccessFile rAFile = null;
        byte[] byteArray = null;
        try {
            File file = newFile(key);
            if (file.exists()) {
                RandomAccessFile rAFile2 = new RandomAccessFile(file, "r");
                try {
                    if (rAFile2.length() != 0) {
                        byteArray = new byte[((int) rAFile2.length())];
                        rAFile2.read(byteArray);
                    }
                    if (rAFile2 != null) {
                        try {
                            rAFile2.close();
                            rAFile = rAFile2;
                            return byteArray;
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            rAFile = rAFile2;
                            return byteArray;
                        }
                    }
                    return byteArray;
                } catch (Exception e3) {
                    e = e3;
                    rAFile = rAFile2;
                    try {
                        e.printStackTrace();
                        if (rAFile != null) {
                            return null;
                        }
                        try {
                            rAFile.close();
                            return null;
                        } catch (IOException e22) {
                            e22.printStackTrace();
                            return null;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (rAFile != null) {
                            try {
                                rAFile.close();
                            } catch (IOException e222) {
                                e222.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    rAFile = rAFile2;
                    if (rAFile != null) {
                        rAFile.close();
                    }
                    throw th;
                }
            }
            if (rAFile != null) {
                try {
                    rAFile.close();
                } catch (IOException e2222) {
                    e2222.printStackTrace();
                }
            }
            return null;
        } catch (Exception e4) {
            e = e4;
            e.printStackTrace();
            if (rAFile != null) {
                return null;
            }
            rAFile.close();
            return null;
        }
    }

    public JSONArray getJSONArray(String key) {
        try {
            return new JSONArray(getString(key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getJSONObject(String key) {
        try {
            return new JSONObject(getString(key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private File newFile(String key) {
        return new File(this.mCacheFile, MD5Coder.getMD5Code(key));
    }

    public void remove(String key) {
        try {
            newFile(key).delete();
        } catch (Exception e) {
        }
    }
}
