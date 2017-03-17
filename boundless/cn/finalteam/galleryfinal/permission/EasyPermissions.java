package cn.finalteam.galleryfinal.permission;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import cn.finalteam.galleryfinal.utils.ILogger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EasyPermissions {
    private static final String TAG = "EasyPermissions";

    public interface PermissionCallbacks extends OnRequestPermissionsResultCallback {
        void onPermissionsDenied(List<String> list);

        void onPermissionsGranted(List<String> list);
    }

    public static boolean hasPermissions(Context context, String... perms) {
        for (String perm : perms) {
            boolean hasPerm;
            if (ContextCompat.checkSelfPermission(context, perm) == 0) {
                hasPerm = true;
            } else {
                hasPerm = false;
            }
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }

    public static void requestPermissions(Object object, String rationale, int requestCode, String... perms) {
        requestPermissions(object, rationale, 17039370, 17039360, requestCode, perms);
    }

    public static void requestPermissions(final Object object, String rationale, @StringRes int positiveButton, @StringRes int negativeButton, final int requestCode, final String... perms) {
        checkCallingObjectSuitability(object);
        boolean shouldShowRationale = false;
        for (String perm : perms) {
            if (shouldShowRationale || shouldShowRequestPermissionRationale(object, perm)) {
                shouldShowRationale = true;
            } else {
                shouldShowRationale = false;
            }
        }
        if (shouldShowRationale) {
            new Builder(getActivity(object)).setMessage(rationale).setPositiveButton(positiveButton, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    EasyPermissions.executePermissionsRequest(object, perms, requestCode);
                }
            }).setNegativeButton(negativeButton, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    PermissionCallbacks callbacks = object;
                    if (callbacks != null) {
                        callbacks.onPermissionsDenied(new ArrayList());
                    }
                }
            }).create().show();
        } else {
            executePermissionsRequest(object, perms, requestCode);
        }
    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, Object object) {
        checkCallingObjectSuitability(object);
        PermissionCallbacks callbacks = (PermissionCallbacks) object;
        ArrayList<String> granted = new ArrayList();
        ArrayList<String> denied = new ArrayList();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == 0) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }
        if (!granted.isEmpty()) {
            callbacks.onPermissionsGranted(granted);
        }
        if (!denied.isEmpty()) {
            callbacks.onPermissionsDenied(denied);
        }
        if (!granted.isEmpty() && denied.isEmpty()) {
            runAnnotatedMethods(object, requestCode);
        }
    }

    private static boolean shouldShowRequestPermissionRationale(Object object, String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        }
        if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        }
        return false;
    }

    public static void executePermissionsRequest(Object object, String[] perms, int requestCode) {
        checkCallingObjectSuitability(object);
        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms, requestCode);
        }
    }

    private static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        }
        if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        }
        return null;
    }

    private static void runAnnotatedMethods(Object object, int requestCode) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(AfterPermissionGranted.class) && ((AfterPermissionGranted) method.getAnnotation(AfterPermissionGranted.class)).value() == requestCode) {
                if (method.getParameterTypes().length > 0) {
                    throw new RuntimeException("Cannot execute non-void method " + method.getName());
                }
                try {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    method.invoke(object, new Object[0]);
                } catch (IllegalAccessException e) {
                    ILogger.e(TAG, "runDefaultMethod:IllegalAccessException", e);
                } catch (InvocationTargetException e2) {
                    ILogger.e(TAG, "runDefaultMethod:InvocationTargetException", e2);
                }
            }
        }
    }

    public static void checkCallingObjectSuitability(Object object) {
        if (!(object instanceof Fragment) && !(object instanceof Activity)) {
            throw new IllegalArgumentException("Caller must be an Activity or a Fragment.");
        } else if (!(object instanceof PermissionCallbacks)) {
            throw new IllegalArgumentException("Caller must implement PermissionCallbacks.");
        }
    }
}
