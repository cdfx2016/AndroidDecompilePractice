package de.greenrobot.event;

import android.util.Log;
import butterknife.internal.ButterKnifeProcessor;
import com.fanyu.boundless.util.FileUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

class SubscriberMethodFinder {
    private static final int BRIDGE = 64;
    private static final SubscriberIndex INDEX;
    private static final Map<String, List<SubscriberMethod>> METHOD_CACHE = new HashMap();
    private static final int MODIFIERS_IGNORE = 5192;
    private static final int SYNTHETIC = 4096;
    private final boolean strictMethodVerification;

    static {
        SubscriberIndex newIndex = null;
        try {
            newIndex = (SubscriberIndex) Class.forName("de.greenrobot.event.GeneratedSubscriberIndex").newInstance();
        } catch (ClassNotFoundException e) {
            Log.d(EventBus.TAG, "No subscriber index available, reverting to dynamic look-up");
        } catch (Exception e2) {
            Log.w(EventBus.TAG, "Could not init subscriber index, reverting to dynamic look-up", e2);
        }
        INDEX = newIndex;
    }

    SubscriberMethodFinder(boolean strictMethodVerification) {
        this.strictMethodVerification = strictMethodVerification;
    }

    List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass, boolean forceReflection) {
        String key = subscriberClass.getName();
        synchronized (METHOD_CACHE) {
            List<SubscriberMethod> subscriberMethods = (List) METHOD_CACHE.get(key);
        }
        if (subscriberMethods != null) {
            return subscriberMethods;
        }
        if (INDEX == null || forceReflection) {
            subscriberMethods = findSubscriberMethodsWithReflection(subscriberClass);
        } else {
            subscriberMethods = findSubscriberMethodsWithIndex(subscriberClass);
            if (subscriberMethods.isEmpty()) {
                subscriberMethods = findSubscriberMethodsWithReflection(subscriberClass);
            }
        }
        if (subscriberMethods.isEmpty()) {
            throw new EventBusException("Subscriber " + subscriberClass + " and its super classes have no public methods with the @Subscribe annotation");
        }
        synchronized (METHOD_CACHE) {
            METHOD_CACHE.put(key, subscriberMethods);
        }
        return subscriberMethods;
    }

    private List<SubscriberMethod> findSubscriberMethodsWithIndex(Class<?> subscriberClass) {
        Class<?> clazz = subscriberClass;
        while (clazz != null) {
            SubscriberMethod[] array = INDEX.getSubscribersFor(clazz);
            if (array == null || array.length <= 0) {
                String name = clazz.getName();
                if (name.startsWith(ButterKnifeProcessor.JAVA_PREFIX) || name.startsWith("javax.") || name.startsWith(ButterKnifeProcessor.ANDROID_PREFIX)) {
                    break;
                }
                clazz = clazz.getSuperclass();
            } else {
                List<SubscriberMethod> arrayList = new ArrayList();
                for (SubscriberMethod subscriberMethod : array) {
                    arrayList.add(subscriberMethod);
                }
                return arrayList;
            }
        }
        return Collections.EMPTY_LIST;
    }

    private List<SubscriberMethod> findSubscriberMethodsWithReflection(Class<?> subscriberClass) {
        List<SubscriberMethod> subscriberMethods = new ArrayList();
        Class<?> clazz = subscriberClass;
        HashSet<String> eventTypesFound = new HashSet();
        StringBuilder methodKeyBuilder = new StringBuilder();
        while (clazz != null) {
            String name = clazz.getName();
            if (!name.startsWith(ButterKnifeProcessor.JAVA_PREFIX)) {
                if (name.startsWith("javax.")) {
                    break;
                }
                if (name.startsWith(ButterKnifeProcessor.ANDROID_PREFIX)) {
                    break;
                }
                for (Method method : clazz.getDeclaredMethods()) {
                    int modifiers = method.getModifiers();
                    if ((modifiers & 1) != 0 && (modifiers & MODIFIERS_IGNORE) == 0) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes.length == 1) {
                            Subscribe subscribeAnnotation = (Subscribe) method.getAnnotation(Subscribe.class);
                            if (subscribeAnnotation != null) {
                                String methodName = method.getName();
                                Class<?> eventType = parameterTypes[0];
                                methodKeyBuilder.setLength(0);
                                methodKeyBuilder.append(methodName);
                                methodKeyBuilder.append('>').append(eventType.getName());
                                if (eventTypesFound.add(methodKeyBuilder.toString())) {
                                    subscriberMethods.add(new SubscriberMethod(method, eventType, subscribeAnnotation.threadMode(), subscribeAnnotation.priority(), subscribeAnnotation.sticky()));
                                }
                            }
                        } else if (this.strictMethodVerification && method.isAnnotationPresent(Subscribe.class)) {
                            throw new EventBusException("@Subscribe method " + (name + FileUtil.FILE_EXTENSION_SEPARATOR + method.getName()) + "must have exactly 1 parameter but has " + parameterTypes.length);
                        }
                    } else if (this.strictMethodVerification && method.isAnnotationPresent(Subscribe.class)) {
                        throw new EventBusException((name + FileUtil.FILE_EXTENSION_SEPARATOR + method.getName()) + " is a illegal @Subscribe method: must be public, non-static, and non-abstract");
                    }
                }
                clazz = clazz.getSuperclass();
            } else {
                break;
            }
        }
        return subscriberMethods;
    }

    static void clearCaches() {
        synchronized (METHOD_CACHE) {
            METHOD_CACHE.clear();
        }
    }
}
