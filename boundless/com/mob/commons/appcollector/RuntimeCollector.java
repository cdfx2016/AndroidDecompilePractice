package com.mob.commons.appcollector;

import android.content.Context;
import android.os.Build.VERSION;
import cn.finalteam.toolsfinal.ShellUtils;
import com.easemob.util.HanziToPinyin.Token;
import com.fanyu.boundless.config.Preferences;
import com.mob.commons.a;
import com.mob.commons.c;
import com.mob.commons.d;
import com.mob.commons.e;
import com.mob.tools.MobLog;
import com.mob.tools.utils.ReflectHelper;
import com.mob.tools.utils.ResHelper;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class RuntimeCollector {
    private static final String a = (VERSION.SDK_INT >= 16 ? "^u\\d+_a\\d+" : "^app_\\d+");
    private static RuntimeCollector b;
    private Context c;

    public static synchronized void startCollector(Context context, String str) {
        synchronized (RuntimeCollector.class) {
            startCollector(context);
        }
    }

    public static synchronized void startCollector(Context context) {
        synchronized (RuntimeCollector.class) {
            if (b == null) {
                b = new RuntimeCollector(context);
                b.a();
            }
        }
    }

    private RuntimeCollector(Context context) {
        this.c = context.getApplicationContext();
    }

    private void a() {
        Thread anonymousClass1 = new Thread(this) {
            final /* synthetic */ RuntimeCollector a;

            {
                this.a = r1;
            }

            public void run() {
                e.a(new File(ResHelper.getCacheRoot(this.a.c), "comm/locks/.rc_lock"), new Runnable(this) {
                    final /* synthetic */ AnonymousClass1 a;

                    {
                        this.a = r1;
                    }

                    public void run() {
                        if (!d.a(this.a.a.c, "comm/tags/.rcTag")) {
                            this.a.a.b();
                        }
                    }
                });
            }
        };
        anonymousClass1.setPriority(1);
        anonymousClass1.start();
    }

    private void b() {
        Process exec;
        Process process;
        Throwable th;
        Object obj;
        OutputStream outputStream;
        long j;
        String str;
        long d;
        Object obj2;
        OutputStream outputStream2;
        Object obj3;
        OutputStream outputStream3 = null;
        File file = new File(ResHelper.getCacheRoot(this.c), "comm/dbs/.plst");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        String absolutePath = file.getAbsolutePath();
        long c = c();
        d.b(this.c, "comm/tags/.rcTag");
        try {
            exec = Runtime.getRuntime().exec(ShellUtils.COMMAND_SH);
            try {
                outputStream3 = exec.getOutputStream();
                process = exec;
            } catch (Throwable th2) {
                th = th2;
                try {
                    MobLog.getInstance().w(th);
                    process = exec;
                    d.c(this.c, "comm/tags/.rcTag");
                    obj = 1;
                    outputStream = outputStream3;
                    j = c;
                    while (true) {
                        try {
                            if (a.b(this.c)) {
                                if (!file.exists()) {
                                    file.createNewFile();
                                }
                                c = a.a(this.c);
                                outputStream.write(("top -d 0 -n 1 | grep -E -v 'root|shell|system' >> " + absolutePath + " && echo \"" + "======================" + "\" >> " + absolutePath + "\n").getBytes("ascii"));
                                if (obj != null) {
                                    str = "echo \"" + c + "_0\" >> " + absolutePath + "\n";
                                    obj = null;
                                } else {
                                    str = "echo \"" + c + "_" + a.c(this.c) + "\" >> " + absolutePath + "\n";
                                }
                                outputStream.write(str.getBytes("ascii"));
                                outputStream.flush();
                                if (c >= j) {
                                    outputStream.write(ShellUtils.COMMAND_EXIT.getBytes("ascii"));
                                    outputStream.flush();
                                    outputStream.close();
                                    process.waitFor();
                                    process.destroy();
                                    if (a(absolutePath)) {
                                        d = d();
                                        if (d <= 0) {
                                            d = j;
                                        }
                                        obj2 = 1;
                                        c = d;
                                    } else {
                                        c = j;
                                        obj2 = obj;
                                    }
                                    try {
                                        d.b(this.c, "comm/tags/.rcTag");
                                        try {
                                            exec = Runtime.getRuntime().exec(ShellUtils.COMMAND_SH);
                                            try {
                                                process = exec;
                                                outputStream2 = exec.getOutputStream();
                                            } catch (Throwable th3) {
                                                th = th3;
                                                try {
                                                    MobLog.getInstance().w(th);
                                                    process = exec;
                                                    outputStream2 = outputStream;
                                                    d.c(this.c, "comm/tags/.rcTag");
                                                    obj3 = obj2;
                                                    outputStream3 = outputStream2;
                                                    obj = obj3;
                                                    Thread.sleep((long) (a.c(this.c) * 1000));
                                                    outputStream = outputStream3;
                                                    j = c;
                                                } catch (Throwable th4) {
                                                    th = th4;
                                                    process = exec;
                                                    obj = obj2;
                                                    outputStream3 = outputStream;
                                                    MobLog.getInstance().d(th);
                                                    outputStream = outputStream3;
                                                    j = c;
                                                }
                                            }
                                        } catch (Throwable th5) {
                                            th = th5;
                                            exec = process;
                                            MobLog.getInstance().w(th);
                                            process = exec;
                                            outputStream2 = outputStream;
                                            d.c(this.c, "comm/tags/.rcTag");
                                            obj3 = obj2;
                                            outputStream3 = outputStream2;
                                            obj = obj3;
                                            Thread.sleep((long) (a.c(this.c) * 1000));
                                            outputStream = outputStream3;
                                            j = c;
                                        }
                                        try {
                                            d.c(this.c, "comm/tags/.rcTag");
                                            obj3 = obj2;
                                            outputStream3 = outputStream2;
                                            obj = obj3;
                                            Thread.sleep((long) (a.c(this.c) * 1000));
                                            outputStream = outputStream3;
                                            j = c;
                                        } catch (Throwable th6) {
                                            th = th6;
                                            obj3 = obj2;
                                            outputStream3 = outputStream2;
                                            obj = obj3;
                                            MobLog.getInstance().d(th);
                                            outputStream = outputStream3;
                                            j = c;
                                        }
                                    } catch (Throwable th7) {
                                        th = th7;
                                        obj = obj2;
                                        outputStream3 = outputStream;
                                        MobLog.getInstance().d(th);
                                        outputStream = outputStream3;
                                        j = c;
                                    }
                                }
                            }
                            c = j;
                            outputStream3 = outputStream;
                            try {
                                Thread.sleep((long) (a.c(this.c) * 1000));
                                outputStream = outputStream3;
                                j = c;
                            } catch (Throwable th8) {
                                th = th8;
                                MobLog.getInstance().d(th);
                                outputStream = outputStream3;
                                j = c;
                            }
                        } catch (Throwable th9) {
                            th = th9;
                            c = j;
                            outputStream3 = outputStream;
                            MobLog.getInstance().d(th);
                            outputStream = outputStream3;
                            j = c;
                        }
                    }
                } catch (Throwable th10) {
                    return;
                }
            }
        } catch (Throwable th11) {
            th = th11;
            exec = outputStream3;
            MobLog.getInstance().w(th);
            process = exec;
            d.c(this.c, "comm/tags/.rcTag");
            obj = 1;
            outputStream = outputStream3;
            j = c;
            while (true) {
                if (a.b(this.c)) {
                    if (file.exists()) {
                        file.createNewFile();
                    }
                    c = a.a(this.c);
                    outputStream.write(("top -d 0 -n 1 | grep -E -v 'root|shell|system' >> " + absolutePath + " && echo \"" + "======================" + "\" >> " + absolutePath + "\n").getBytes("ascii"));
                    if (obj != null) {
                        str = "echo \"" + c + "_" + a.c(this.c) + "\" >> " + absolutePath + "\n";
                    } else {
                        str = "echo \"" + c + "_0\" >> " + absolutePath + "\n";
                        obj = null;
                    }
                    outputStream.write(str.getBytes("ascii"));
                    outputStream.flush();
                    if (c >= j) {
                        outputStream.write(ShellUtils.COMMAND_EXIT.getBytes("ascii"));
                        outputStream.flush();
                        outputStream.close();
                        process.waitFor();
                        process.destroy();
                        if (a(absolutePath)) {
                            c = j;
                            obj2 = obj;
                        } else {
                            d = d();
                            if (d <= 0) {
                                d = j;
                            }
                            obj2 = 1;
                            c = d;
                        }
                        d.b(this.c, "comm/tags/.rcTag");
                        exec = Runtime.getRuntime().exec(ShellUtils.COMMAND_SH);
                        process = exec;
                        outputStream2 = exec.getOutputStream();
                        d.c(this.c, "comm/tags/.rcTag");
                        obj3 = obj2;
                        outputStream3 = outputStream2;
                        obj = obj3;
                        Thread.sleep((long) (a.c(this.c) * 1000));
                        outputStream = outputStream3;
                        j = c;
                    }
                }
                c = j;
                outputStream3 = outputStream;
                Thread.sleep((long) (a.c(this.c) * 1000));
                outputStream = outputStream3;
                j = c;
            }
        }
        d.c(this.c, "comm/tags/.rcTag");
        obj = 1;
        outputStream = outputStream3;
        j = c;
        while (true) {
            if (a.b(this.c)) {
                if (file.exists()) {
                    file.createNewFile();
                }
                c = a.a(this.c);
                outputStream.write(("top -d 0 -n 1 | grep -E -v 'root|shell|system' >> " + absolutePath + " && echo \"" + "======================" + "\" >> " + absolutePath + "\n").getBytes("ascii"));
                if (obj != null) {
                    str = "echo \"" + c + "_0\" >> " + absolutePath + "\n";
                    obj = null;
                } else {
                    str = "echo \"" + c + "_" + a.c(this.c) + "\" >> " + absolutePath + "\n";
                }
                outputStream.write(str.getBytes("ascii"));
                outputStream.flush();
                if (c >= j) {
                    outputStream.write(ShellUtils.COMMAND_EXIT.getBytes("ascii"));
                    outputStream.flush();
                    outputStream.close();
                    process.waitFor();
                    process.destroy();
                    if (a(absolutePath)) {
                        d = d();
                        if (d <= 0) {
                            d = j;
                        }
                        obj2 = 1;
                        c = d;
                    } else {
                        c = j;
                        obj2 = obj;
                    }
                    d.b(this.c, "comm/tags/.rcTag");
                    exec = Runtime.getRuntime().exec(ShellUtils.COMMAND_SH);
                    process = exec;
                    outputStream2 = exec.getOutputStream();
                    d.c(this.c, "comm/tags/.rcTag");
                    obj3 = obj2;
                    outputStream3 = outputStream2;
                    obj = obj3;
                    Thread.sleep((long) (a.c(this.c) * 1000));
                    outputStream = outputStream3;
                    j = c;
                }
            }
            c = j;
            outputStream3 = outputStream;
            Thread.sleep((long) (a.c(this.c) * 1000));
            outputStream = outputStream3;
            j = c;
        }
    }

    private long c() {
        long a = a.a(this.c);
        try {
            File file = new File(ResHelper.getCacheRoot(this.c), "comm/dbs/.nulplt");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
                long readLong = dataInputStream.readLong();
                dataInputStream.close();
                return readLong;
            }
            file.createNewFile();
            d();
            return a + 86400000;
        } catch (Throwable th) {
            MobLog.getInstance().d(th);
            return a + 86400000;
        }
    }

    private long d() {
        long a = a.a(this.c) + 86400000;
        try {
            File file = new File(ResHelper.getCacheRoot(this.c), "comm/dbs/.nulplt");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
            dataOutputStream.writeLong(a);
            dataOutputStream.flush();
            dataOutputStream.close();
            return a;
        } catch (Throwable th) {
            MobLog.getInstance().d(th);
            return 0;
        }
    }

    private boolean a(String str) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        a(str, arrayList, arrayList2);
        a(a(a(a(arrayList), arrayList), arrayList2), arrayList2);
        return b(str);
    }

    private void a(String str, ArrayList<ArrayList<HashMap<String, String>>> arrayList, ArrayList<long[]> arrayList2) {
        try {
            HashMap e = e();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(str), "utf-8"));
            String readLine = bufferedReader.readLine();
            for (int i = 0; i < 7; i++) {
                readLine = bufferedReader.readLine();
            }
            ArrayList arrayList3 = new ArrayList();
            for (readLine = 
/*
Method generation error in method: com.mob.commons.appcollector.RuntimeCollector.a(java.lang.String, java.util.ArrayList, java.util.ArrayList):void
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r1_2 'readLine' java.lang.String) = (r1_1 'readLine' java.lang.String), (r1_3 'readLine' java.lang.String) binds: {(r1_3 'readLine' java.lang.String)=B:4:0x001f, (r1_1 'readLine' java.lang.String)=B:2:?} in method: com.mob.commons.appcollector.RuntimeCollector.a(java.lang.String, java.util.ArrayList, java.util.ArrayList):void
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:184)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:61)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:277)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:328)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:265)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:228)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:118)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:83)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:19)
	at jadx.core.ProcessClass.process(ProcessClass.java:43)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:530)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:514)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:220)
	... 19 more

*/

            private void a(String str, HashMap<String, String[]> hashMap, ArrayList<HashMap<String, String>> arrayList) {
                String[] split = str.replaceAll(" +", Token.SEPARATOR).split(Token.SEPARATOR);
                if (split != null && split.length >= 10) {
                    Object obj = split[split.length - 1];
                    if (split[split.length - 2].matches(a) && !"top".equals(obj)) {
                        String[] strArr = (String[]) hashMap.get(obj);
                        if (strArr != null) {
                            HashMap hashMap2 = new HashMap();
                            hashMap2.put("pkg", obj);
                            hashMap2.put(Preferences.sbry, strArr[0]);
                            hashMap2.put("version", strArr[1]);
                            hashMap2.put("pcy", split[split.length - 3]);
                            arrayList.add(hashMap2);
                        }
                    }
                }
            }

            private HashMap<String, String[]> e() {
                ArrayList arrayList;
                try {
                    Object[] objArr = new Object[]{Boolean.valueOf(false)};
                    arrayList = (ArrayList) ReflectHelper.invokeInstanceMethod(ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", this.c), "getInstalledApp", objArr);
                } catch (Throwable th) {
                    MobLog.getInstance().w(th);
                    arrayList = new ArrayList();
                }
                HashMap<String, String[]> hashMap = new HashMap();
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    hashMap.put((String) ((HashMap) it.next()).get("pkg"), new String[]{(String) r0.get(Preferences.sbry), (String) ((HashMap) it.next()).get("version")});
                }
                return hashMap;
            }

            private HashMap<String, Integer> a(ArrayList<ArrayList<HashMap<String, String>>> arrayList) {
                HashMap<String, Integer> hashMap = new HashMap();
                Iterator it = arrayList.iterator();
                int i = 0;
                while (it.hasNext()) {
                    Iterator it2 = ((ArrayList) it.next()).iterator();
                    int i2 = i;
                    while (it2.hasNext()) {
                        HashMap hashMap2 = (HashMap) it2.next();
                        String str = ((String) hashMap2.get("pkg")) + ":" + ((String) hashMap2.get("version"));
                        if (!hashMap.containsKey(str)) {
                            hashMap.put(str, Integer.valueOf(i2));
                            i2++;
                        }
                    }
                    i = i2;
                }
                return hashMap;
            }

            private HashMap<String, String>[][] a(HashMap<String, Integer> hashMap, ArrayList<ArrayList<HashMap<String, String>>> arrayList) {
                HashMap[][] hashMapArr = (HashMap[][]) Array.newInstance(HashMap.class, new int[]{hashMap.size(), arrayList.size()});
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    ArrayList arrayList2 = (ArrayList) arrayList.get(i);
                    int size2 = arrayList2.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        HashMap hashMap2 = (HashMap) arrayList2.get(i2);
                        hashMapArr[((Integer) hashMap.get(((String) hashMap2.get("pkg")) + ":" + ((String) hashMap2.get("version")))).intValue()][i] = hashMap2;
                    }
                }
                return hashMapArr;
            }

            private ArrayList<HashMap<String, Object>> a(HashMap<String, String>[][] hashMapArr, ArrayList<long[]> arrayList) {
                ArrayList<HashMap<String, Object>> arrayList2 = new ArrayList(hashMapArr.length);
                for (HashMap<String, String>[] hashMapArr2 : hashMapArr) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("runtimes", Long.valueOf(0));
                    hashMap.put("fg", Integer.valueOf(0));
                    hashMap.put("bg", Integer.valueOf(0));
                    hashMap.put("empty", Integer.valueOf(0));
                    arrayList2.add(hashMap);
                    int length = hashMapArr2.length - 1;
                    while (length >= 0) {
                        if (hashMapArr2[length] != null) {
                            hashMap.put("runtimes", Long.valueOf((length == 0 ? 0 : ((long[]) arrayList.get(length))[1]) + ((Long) ResHelper.forceCast(hashMap.get("runtimes"), Long.valueOf(0))).longValue()));
                            if ("fg".equals(hashMapArr2[length].get("pcy"))) {
                                hashMap.put("fg", Integer.valueOf(((Integer) ResHelper.forceCast(hashMap.get("fg"), Integer.valueOf(0))).intValue() + 1));
                            } else if ("bg".equals(hashMapArr2[length].get("pcy"))) {
                                hashMap.put("bg", Integer.valueOf(((Integer) ResHelper.forceCast(hashMap.get("bg"), Integer.valueOf(0))).intValue() + 1));
                            } else {
                                hashMap.put("empty", Integer.valueOf(((Integer) ResHelper.forceCast(hashMap.get("empty"), Integer.valueOf(0))).intValue() + 1));
                            }
                            hashMap.put("pkg", hashMapArr2[length].get("pkg"));
                            hashMap.put(Preferences.sbry, hashMapArr2[length].get(Preferences.sbry));
                            hashMap.put("version", hashMapArr2[length].get("version"));
                        }
                        length--;
                    }
                }
                return arrayList2;
            }

            private void a(ArrayList<HashMap<String, Object>> arrayList, ArrayList<long[]> arrayList2) {
                HashMap hashMap = new HashMap();
                hashMap.put(MessageEncoder.ATTR_TYPE, "APP_RUNTIMES");
                hashMap.put("list", arrayList);
                hashMap.put("datatime", Long.valueOf(a.a(this.c)));
                hashMap.put("recordat", Long.valueOf(((long[]) arrayList2.get(0))[0]));
                long j = 0;
                for (int i = 1; i < arrayList2.size(); i++) {
                    j += ((long[]) arrayList2.get(i))[1];
                }
                hashMap.put("sdk_runtime_len", Long.valueOf(j));
                hashMap.put("top_count", Integer.valueOf(arrayList2.size()));
                c.a(this.c).a(a.a(this.c), hashMap);
            }

            private boolean b(String str) {
                try {
                    File file = new File(str);
                    file.delete();
                    file.createNewFile();
                    return true;
                } catch (Throwable th) {
                    MobLog.getInstance().d(th);
                    return false;
                }
            }
        }
