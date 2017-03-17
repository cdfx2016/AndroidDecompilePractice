package de.greenrobot.event;

import java.util.HashMap;
import java.util.Map;

abstract class SubscriberIndex {
    private Map<Class<?>, SubscriberMethod[]> map = new HashMap();

    abstract SubscriberMethod[] createSubscribersFor(Class<?> cls);

    SubscriberIndex() {
    }

    SubscriberMethod[] getSubscribersFor(Class<?> subscriberClass) {
        SubscriberMethod[] entries = (SubscriberMethod[]) this.map.get(subscriberClass);
        if (entries == null) {
            entries = createSubscribersFor(subscriberClass);
            if (entries != null) {
                this.map.put(subscriberClass, entries);
            }
        }
        return entries;
    }

    SubscriberMethod createSubscriberMethod(Class<?> subscriberClass, String methodName, Class<?> eventType, ThreadMode threadMode, int priority, boolean sticky) {
        try {
            return new SubscriberMethod(subscriberClass.getDeclaredMethod(methodName, new Class[]{eventType}), eventType, threadMode, priority, sticky);
        } catch (NoSuchMethodException e) {
            throw new EventBusException("Could not find subscriber method in " + subscriberClass + ". Maybe a missing ProGuard rule?", e);
        }
    }
}
