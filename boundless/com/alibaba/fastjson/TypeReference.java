package com.alibaba.fastjson;

import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TypeReference<T> {
    static ConcurrentMap<Type, Type> classTypeCache = new ConcurrentHashMap(16, AdaptiveVideoTrackSelection.DEFAULT_BANDWIDTH_FRACTION, 1);
    protected final Type type;

    protected TypeReference() {
        this.type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected TypeReference(Type... actualTypeArguments) {
        Class<?> thisClass = getClass();
        ParameterizedType argType = ((ParameterizedType) thisClass.getGenericSuperclass()).getActualTypeArguments()[0];
        Type rawType = argType.getRawType();
        Type[] argTypes = argType.getActualTypeArguments();
        int actualIndex = 0;
        for (int i = 0; i < argTypes.length; i++) {
            if (argTypes[i] instanceof TypeVariable) {
                int actualIndex2 = actualIndex + 1;
                argTypes[i] = actualTypeArguments[actualIndex];
                if (actualIndex2 >= actualTypeArguments.length) {
                    actualIndex = actualIndex2;
                    break;
                }
                actualIndex = actualIndex2;
            }
        }
        Type key = new ParameterizedTypeImpl(argTypes, thisClass, rawType);
        Type cachedType = (Type) classTypeCache.get(key);
        if (cachedType == null) {
            classTypeCache.putIfAbsent(key, key);
            cachedType = (Type) classTypeCache.get(key);
        }
        this.type = cachedType;
    }

    public Type getType() {
        return this.type;
    }
}
