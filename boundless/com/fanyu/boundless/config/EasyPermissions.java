package com.fanyu.boundless.config;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EasyPermissions {
    public static final int SETTINGS_REQ_CODE = 16061;
    private static final String TAG = "EasyPermissions";

    public interface PermissionCallbacks extends OnRequestPermissionsResultCallback {
        void onPermissionsDenied(int i, List<String> list);

        void onPermissionsGranted(int i, List<String> list);
    }

    public static boolean hasPermissions(Context context, String... perms) {
        if (VERSION.SDK_INT < 23) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default");
            return true;
        }
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

    public static void MyRequestPermissions(Object object, int requestCode, String... perms) {
        myRequestPermissions(object, requestCode, perms);
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
            Activity activity = getActivity(object);
            if (activity != null) {
                new Builder(activity).setMessage((CharSequence) rationale).setPositiveButton(positiveButton, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EasyPermissions.executePermissionsRequest(object, perms, requestCode);
                    }
                }).setNegativeButton(negativeButton, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (object instanceof PermissionCallbacks) {
                            ((PermissionCallbacks) object).onPermissionsDenied(requestCode, Arrays.asList(perms));
                        }
                    }
                }).create().show();
                return;
            }
            return;
        }
        executePermissionsRequest(object, perms, requestCode);
    }

    public static void myRequestPermissions(Object object, int requestCode, String... perms) {
        checkCallingObjectSuitability(object);
        executePermissionsRequest(object, perms, requestCode);
    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, Object object) {
        checkCallingObjectSuitability(object);
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
        if (!granted.isEmpty() && (object instanceof PermissionCallbacks)) {
            ((PermissionCallbacks) object).onPermissionsGranted(requestCode, granted);
        }
        if (!denied.isEmpty() && (object instanceof PermissionCallbacks)) {
            ((PermissionCallbacks) object).onPermissionsDenied(requestCode, denied);
        }
        if (!granted.isEmpty() && denied.isEmpty()) {
            runAnnotatedMethods(object, requestCode);
        }
    }

    public static boolean checkDeniedPermissionsNeverAskAgain(Object object, String rationale, @StringRes int positiveButton, @StringRes int negativeButton, List<String> deniedPerms) {
        return checkDeniedPermissionsNeverAskAgain(object, rationale, positiveButton, negativeButton, null, deniedPerms);
    }

    public static boolean checkDeniedPermissionsNeverAskAgain(final Object object, String rationale, @StringRes int positiveButton, @StringRes int negativeButton, @Nullable OnClickListener negativeButtonOnClickListener, List<String> deniedPerms) {
        for (String perm : deniedPerms) {
            if (!shouldShowRequestPermissionRationale(object, perm)) {
                final Activity activity = getActivity(object);
                if (activity == null) {
                    return true;
                }
                new Builder(activity).setMessage((CharSequence) rationale).setPositiveButton(positiveButton, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                        EasyPermissions.startAppSettingsScreen(object, intent);
                    }
                }).setNegativeButton(negativeButton, negativeButtonOnClickListener).create().show();
                return true;
            }
        }
        return false;
    }

    @TargetApi(23)
    private static boolean shouldShowRequestPermissionRationale(Object object, String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        }
        if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        }
        if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
        }
        return false;
    }

    @TargetApi(23)
    private static void executePermissionsRequest(Object object, String[] perms, int requestCode) {
        checkCallingObjectSuitability(object);
        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).requestPermissions(perms, requestCode);
        }
    }

    @TargetApi(11)
    private static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        }
        if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        }
        if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        }
        return null;
    }

    @TargetApi(11)
    private static void startAppSettingsScreen(Object object, Intent intent) {
        if (object instanceof Activity) {
            ((Activity) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        } else if (object instanceof Fragment) {
            ((Fragment) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        }
    }

    private static void runAnnotatedMethods(Object object, int requestCode) {
        Class clazz = object.getClass();
        if (isUsingAndroidAnnotations(object)) {
            clazz = clazz.getSuperclass();
        }
        for (Method method : clazz.getDeclaredMethods()) {
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
                    Log.e(TAG, "runDefaultMethod:IllegalAccessException", e);
                } catch (InvocationTargetException e2) {
                    Log.e(TAG, "runDefaultMethod:InvocationTargetException", e2);
                }
            }
        }
    }

    private static void checkCallingObjectSuitability(Object object) {
        boolean isActivity = object instanceof Activity;
        boolean isSupportFragment = object instanceof Fragment;
        boolean isAppFragment = object instanceof android.app.Fragment;
        boolean isMinSdkM = VERSION.SDK_INT >= 23;
        if (!isSupportFragment && !isActivity) {
            if (!isAppFragment || !isMinSdkM) {
                if (isAppFragment) {
                    throw new IllegalArgumentException("Target SDK needs to be greater than 23 if caller is android.app.Fragment");
                }
                throw new IllegalArgumentException("Caller must be an Activity or a Fragment.");
            }
        }
    }

    private static boolean isUsingAndroidAnnotations(Object object) {
        boolean z = false;
        if (object.getClass().getSimpleName().endsWith("_")) {
            try {
                z = Class.forName("org.androidannotations.api.view.HasViews").isInstance(object);
            } catch (ClassNotFoundException e) {
            }
        }
        return z;
    }
}
