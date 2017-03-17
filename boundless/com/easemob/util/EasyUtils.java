package com.easemob.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.zip.GZIPOutputStream;

public class EasyUtils {
    private static Hashtable<String, String> resourceTable = new Hashtable();

    public static String convertByteArrayToString(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        int length = bArr.length;
        for (int i = 0; i < length; i++) {
            stringBuffer.append(String.format("0x%02X", new Object[]{Byte.valueOf(bArr[i])}));
        }
        return stringBuffer.toString();
    }

    public static String getAppResourceString(Context context, String str) {
        String str2 = (String) resourceTable.get(str);
        if (str2 == null) {
            str2 = context.getString(context.getResources().getIdentifier(str, "string", context.getPackageName()));
            if (str2 != null) {
                resourceTable.put(str, str2);
            }
        }
        return str2;
    }

    public static String getTimeStamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
    }

    public static String getTopActivityName(Context context) {
        return ((RunningTaskInfo) ((ActivityManager) context.getSystemService("activity")).getRunningTasks(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED).get(0)).topActivity.getClassName();
    }

    public static boolean isAppRunningForeground(Context context) {
        return context.getPackageName().equalsIgnoreCase(((RunningTaskInfo) ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1).get(0)).baseActivity.getPackageName());
    }

    public static boolean writeToZipFile(byte[] bArr, String str) {
        OutputStream fileOutputStream;
        GZIPOutputStream gZIPOutputStream;
        Exception e;
        OutputStream outputStream;
        Throwable th;
        GZIPOutputStream gZIPOutputStream2 = null;
        try {
            fileOutputStream = new FileOutputStream(str);
            try {
                gZIPOutputStream = new GZIPOutputStream(new BufferedOutputStream(fileOutputStream));
            } catch (Exception e2) {
                e = e2;
                gZIPOutputStream = null;
                outputStream = fileOutputStream;
                try {
                    e.printStackTrace();
                    if (gZIPOutputStream != null) {
                        try {
                            gZIPOutputStream.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e32) {
                            e32.printStackTrace();
                        }
                    }
                    return false;
                } catch (Throwable th2) {
                    th = th2;
                    fileOutputStream = outputStream;
                    gZIPOutputStream2 = gZIPOutputStream;
                    if (gZIPOutputStream2 != null) {
                        try {
                            gZIPOutputStream2.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e42) {
                            e42.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (gZIPOutputStream2 != null) {
                    gZIPOutputStream2.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                throw th;
            }
            try {
                gZIPOutputStream.write(bArr);
                if (gZIPOutputStream != null) {
                    try {
                        gZIPOutputStream.close();
                    } catch (IOException e322) {
                        e322.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e3222) {
                        e3222.printStackTrace();
                    }
                }
                if (EMLog.debugMode) {
                    File file = new File(str);
                    EMLog.d("zip", "data size:" + bArr.length + " zip file size:" + file.length() + "zip file ratio%: " + Double.valueOf(new DecimalFormat("#.##").format((((double) file.length()) / ((double) bArr.length)) * 100.0d)).doubleValue());
                }
                return true;
            } catch (Exception e5) {
                e = e5;
                outputStream = fileOutputStream;
                e.printStackTrace();
                if (gZIPOutputStream != null) {
                    gZIPOutputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                return false;
            } catch (Throwable th4) {
                th = th4;
                gZIPOutputStream2 = gZIPOutputStream;
                if (gZIPOutputStream2 != null) {
                    gZIPOutputStream2.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                throw th;
            }
        } catch (Exception e6) {
            e = e6;
            gZIPOutputStream = null;
            e.printStackTrace();
            if (gZIPOutputStream != null) {
                gZIPOutputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            return false;
        } catch (Throwable th5) {
            th = th5;
            fileOutputStream = null;
            if (gZIPOutputStream2 != null) {
                gZIPOutputStream2.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            throw th;
        }
    }
}
