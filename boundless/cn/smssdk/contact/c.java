package cn.smssdk.contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.mob.tools.utils.DeviceHelper;
import java.util.ArrayList;
import java.util.HashMap;

/* compiled from: Querier */
public class c {
    private ContentResolver a;
    private Context b;

    public c(Context context, ContentResolver contentResolver) {
        this.a = contentResolver;
        this.b = context;
    }

    public ArrayList<HashMap<String, Object>> a(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        ArrayList<HashMap<String, Object>> arrayList;
        try {
            if (!DeviceHelper.getInstance(this.b).checkPermission("android.permission.READ_CONTACTS")) {
                return null;
            }
        } catch (Throwable th) {
        }
        Cursor query = this.a.query(uri, strArr, str, strArr2, str2);
        if (query == null) {
            arrayList = null;
        } else if (query.getCount() == 0) {
            return null;
        } else {
            if (query.moveToFirst()) {
                ArrayList<HashMap<String, Object>> arrayList2 = new ArrayList();
                do {
                    HashMap hashMap = new HashMap();
                    int columnCount = query.getColumnCount();
                    for (int i = 0; i < columnCount; i++) {
                        Object string;
                        String columnName = query.getColumnName(i);
                        try {
                            string = query.getString(i);
                        } catch (Throwable th2) {
                            string = query.getBlob(i);
                        }
                        hashMap.put(columnName, string);
                    }
                    arrayList2.add(hashMap);
                } while (query.moveToNext());
                arrayList = arrayList2;
            } else {
                arrayList = null;
            }
            query.close();
        }
        return arrayList;
    }
}
