package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONStreamAware;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.deserializer.Jdk8DateCodec;
import com.alibaba.fastjson.parser.deserializer.OptionalCodec;
import com.alibaba.fastjson.support.springfox.SwaggerJsonSerializer;
import com.alibaba.fastjson.util.ASMUtils;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.IdentityHashMap;
import com.alibaba.fastjson.util.ServiceLoader;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.File;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import javax.xml.datatype.XMLGregorianCalendar;

public class SerializeConfig {
    private static boolean awtError = false;
    public static final SerializeConfig globalInstance = new SerializeConfig();
    private static boolean jdk8Error = false;
    private static boolean oracleJdbcError = false;
    private static boolean springfoxError = false;
    private boolean asm;
    private ASMSerializerFactory asmFactory;
    public PropertyNamingStrategy propertyNamingStrategy;
    private final IdentityHashMap<Type, ObjectSerializer> serializers;
    protected String typeKey;

    public String getTypeKey() {
        return this.typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    private final JavaBeanSerializer createASMSerializer(SerializeBeanInfo beanInfo) throws Exception {
        JavaBeanSerializer serializer = this.asmFactory.createJavaBeanSerializer(beanInfo);
        for (FieldSerializer fieldDeser : serializer.sortedGetters) {
            Class<?> fieldClass = fieldDeser.fieldInfo.fieldClass;
            if (fieldClass.isEnum() && !(getObjectWriter(fieldClass) instanceof EnumSerializer)) {
                serializer.writeDirect = false;
            }
        }
        return serializer;
    }

    private final ObjectSerializer createJavaBeanSerializer(Class<?> clazz) {
        SerializeBeanInfo beanInfo = TypeUtils.buildBeanInfo(clazz, null, this.propertyNamingStrategy);
        if (beanInfo.fields.length == 0 && Iterable.class.isAssignableFrom(clazz)) {
            return MiscCodec.instance;
        }
        return createJavaBeanSerializer(beanInfo);
    }

    public ObjectSerializer createJavaBeanSerializer(SerializeBeanInfo beanInfo) {
        int i = 0;
        JSONType jsonType = beanInfo.jsonType;
        if (jsonType != null) {
            Class<?> serializerClass = jsonType.serializer();
            if (serializerClass != Void.class) {
                try {
                    Object seralizer = serializerClass.newInstance();
                    if (seralizer instanceof ObjectSerializer) {
                        return (ObjectSerializer) seralizer;
                    }
                } catch (Throwable th) {
                }
            }
            if (!jsonType.asm()) {
                this.asm = false;
            }
        }
        Class<?> clazz = beanInfo.beanType;
        if (!Modifier.isPublic(beanInfo.beanType.getModifiers())) {
            return new JavaBeanSerializer(beanInfo);
        }
        boolean asm = this.asm;
        if ((asm && this.asmFactory.classLoader.isExternalClass(clazz)) || clazz == Serializable.class || clazz == Object.class) {
            asm = false;
        }
        if (asm && !ASMUtils.checkName(clazz.getSimpleName())) {
            asm = false;
        }
        if (asm) {
            FieldInfo[] fieldInfoArr = beanInfo.fields;
            int length = fieldInfoArr.length;
            while (i < length) {
                JSONField annotation = fieldInfoArr[i].getAnnotation();
                if (annotation != null && (!ASMUtils.checkName(annotation.name()) || annotation.format().length() != 0 || annotation.jsonDirect() || annotation.serializeUsing() != Void.class)) {
                    asm = false;
                    break;
                }
                i++;
            }
        }
        if (asm) {
            try {
                ObjectSerializer asmSerializer = createASMSerializer(beanInfo);
                if (asmSerializer != null) {
                    return asmSerializer;
                }
            } catch (ClassFormatError e) {
            } catch (ClassCastException e2) {
            } catch (Throwable e3) {
                JSONException jSONException = new JSONException("create asm serializer error, class " + clazz, e3);
            }
        }
        return new JavaBeanSerializer(beanInfo);
    }

    public boolean isAsmEnable() {
        return this.asm;
    }

    public void setAsmEnable(boolean asmEnable) {
        if (!ASMUtils.IS_ANDROID) {
            this.asm = asmEnable;
        }
    }

    public static SerializeConfig getGlobalInstance() {
        return globalInstance;
    }

    public SerializeConfig() {
        this(1024);
    }

    public SerializeConfig(int tableSize) {
        this.asm = !ASMUtils.IS_ANDROID;
        this.typeKey = JSON.DEFAULT_TYPE_KEY;
        this.serializers = new IdentityHashMap(1024);
        try {
            if (this.asm) {
                this.asmFactory = new ASMSerializerFactory();
            }
        } catch (NoClassDefFoundError e) {
            this.asm = false;
        } catch (ExceptionInInitializerError e2) {
            this.asm = false;
        }
        put((Type) Boolean.class, BooleanCodec.instance);
        put((Type) Character.class, CharacterCodec.instance);
        put((Type) Byte.class, IntegerCodec.instance);
        put((Type) Short.class, IntegerCodec.instance);
        put((Type) Integer.class, IntegerCodec.instance);
        put((Type) Long.class, LongCodec.instance);
        put((Type) Float.class, FloatCodec.instance);
        put((Type) Double.class, DoubleSerializer.instance);
        put((Type) BigDecimal.class, BigDecimalCodec.instance);
        put((Type) BigInteger.class, BigIntegerCodec.instance);
        put((Type) String.class, StringCodec.instance);
        put((Type) byte[].class, PrimitiveArraySerializer.instance);
        put((Type) short[].class, PrimitiveArraySerializer.instance);
        put((Type) int[].class, PrimitiveArraySerializer.instance);
        put((Type) long[].class, PrimitiveArraySerializer.instance);
        put((Type) float[].class, PrimitiveArraySerializer.instance);
        put((Type) double[].class, PrimitiveArraySerializer.instance);
        put((Type) boolean[].class, PrimitiveArraySerializer.instance);
        put((Type) char[].class, PrimitiveArraySerializer.instance);
        put((Type) Object[].class, ObjectArrayCodec.instance);
        put((Type) Class.class, MiscCodec.instance);
        put((Type) SimpleDateFormat.class, MiscCodec.instance);
        put((Type) Currency.class, new MiscCodec());
        put((Type) TimeZone.class, MiscCodec.instance);
        put((Type) InetAddress.class, MiscCodec.instance);
        put((Type) Inet4Address.class, MiscCodec.instance);
        put((Type) Inet6Address.class, MiscCodec.instance);
        put((Type) InetSocketAddress.class, MiscCodec.instance);
        put((Type) File.class, MiscCodec.instance);
        put((Type) Appendable.class, AppendableSerializer.instance);
        put((Type) StringBuffer.class, AppendableSerializer.instance);
        put((Type) StringBuilder.class, AppendableSerializer.instance);
        put((Type) Charset.class, ToStringSerializer.instance);
        put((Type) Pattern.class, ToStringSerializer.instance);
        put((Type) Locale.class, ToStringSerializer.instance);
        put((Type) URI.class, ToStringSerializer.instance);
        put((Type) URL.class, ToStringSerializer.instance);
        put((Type) UUID.class, ToStringSerializer.instance);
        put((Type) AtomicBoolean.class, AtomicCodec.instance);
        put((Type) AtomicInteger.class, AtomicCodec.instance);
        put((Type) AtomicLong.class, AtomicCodec.instance);
        put((Type) AtomicReference.class, ReferenceCodec.instance);
        put((Type) AtomicIntegerArray.class, AtomicCodec.instance);
        put((Type) AtomicLongArray.class, AtomicCodec.instance);
        put((Type) WeakReference.class, ReferenceCodec.instance);
        put((Type) SoftReference.class, ReferenceCodec.instance);
    }

    public void addFilter(Class<?> clazz, SerializeFilter filter) {
        ObjectSerializer serializer = getObjectWriter(clazz);
        if (serializer instanceof SerializeFilterable) {
            SerializeFilterable filterable = (SerializeFilterable) serializer;
            if (this == globalInstance || filterable != MapSerializer.instance) {
                filterable.addFilter(filter);
                return;
            }
            ObjectSerializer newMapSer = new MapSerializer();
            put((Type) clazz, newMapSer);
            newMapSer.addFilter(filter);
        }
    }

    public void config(Class<?> clazz, SerializerFeature feature, boolean value) {
        ObjectSerializer serializer = getObjectWriter(clazz, false);
        SerializeBeanInfo beanInfo;
        if (serializer == null) {
            beanInfo = TypeUtils.buildBeanInfo(clazz, null, this.propertyNamingStrategy);
            if (value) {
                beanInfo.features |= feature.mask;
            } else {
                beanInfo.features &= feature.mask ^ -1;
            }
            put((Type) clazz, createJavaBeanSerializer(beanInfo));
        } else if (serializer instanceof JavaBeanSerializer) {
            beanInfo = ((JavaBeanSerializer) serializer).beanInfo;
            int originalFeaturs = beanInfo.features;
            if (value) {
                beanInfo.features |= feature.mask;
            } else {
                beanInfo.features &= feature.mask ^ -1;
            }
            if (originalFeaturs != beanInfo.features && serializer.getClass() != JavaBeanSerializer.class) {
                put((Type) clazz, createJavaBeanSerializer(beanInfo));
            }
        }
    }

    public ObjectSerializer getObjectWriter(Class<?> clazz) {
        return getObjectWriter(clazz, true);
    }

    private ObjectSerializer getObjectWriter(Class<?> clazz, boolean create) {
        ObjectSerializer autowired;
        ObjectSerializer objectSerializer = (ObjectSerializer) this.serializers.get(clazz);
        if (objectSerializer == null) {
            try {
                for (Object o : ServiceLoader.load(AutowiredObjectSerializer.class, Thread.currentThread().getContextClassLoader())) {
                    if (o instanceof AutowiredObjectSerializer) {
                        autowired = (AutowiredObjectSerializer) o;
                        for (Type forType : autowired.getAutowiredFor()) {
                            put(forType, autowired);
                        }
                    }
                }
            } catch (ClassCastException e) {
            }
            objectSerializer = (ObjectSerializer) this.serializers.get(clazz);
        }
        if (objectSerializer == null) {
            ClassLoader classLoader = JSON.class.getClassLoader();
            if (classLoader != Thread.currentThread().getContextClassLoader()) {
                try {
                    for (Object o2 : ServiceLoader.load(AutowiredObjectSerializer.class, classLoader)) {
                        if (o2 instanceof AutowiredObjectSerializer) {
                            autowired = (AutowiredObjectSerializer) o2;
                            for (Type forType2 : autowired.getAutowiredFor()) {
                                put(forType2, autowired);
                            }
                        }
                    }
                } catch (ClassCastException e2) {
                }
                objectSerializer = (ObjectSerializer) this.serializers.get(clazz);
            }
        }
        if (objectSerializer == null) {
            if (Map.class.isAssignableFrom(clazz)) {
                put((Type) clazz, MapSerializer.instance);
            } else if (List.class.isAssignableFrom(clazz)) {
                put((Type) clazz, ListSerializer.instance);
            } else if (Collection.class.isAssignableFrom(clazz)) {
                put((Type) clazz, CollectionCodec.instance);
            } else if (Date.class.isAssignableFrom(clazz)) {
                put((Type) clazz, DateCodec.instance);
            } else if (JSONAware.class.isAssignableFrom(clazz)) {
                put((Type) clazz, JSONAwareSerializer.instance);
            } else if (JSONSerializable.class.isAssignableFrom(clazz)) {
                put((Type) clazz, JSONSerializableSerializer.instance);
            } else if (JSONStreamAware.class.isAssignableFrom(clazz)) {
                put((Type) clazz, MiscCodec.instance);
            } else if (clazz.isEnum() || (clazz.getSuperclass() != null && clazz.getSuperclass().isEnum())) {
                put((Type) clazz, EnumSerializer.instance);
            } else if (clazz.isArray()) {
                Class<?> componentType = clazz.getComponentType();
                put((Type) clazz, new ArraySerializer(componentType, getObjectWriter(componentType)));
            } else if (Throwable.class.isAssignableFrom(clazz)) {
                SerializeBeanInfo beanInfo = TypeUtils.buildBeanInfo(clazz, null, this.propertyNamingStrategy);
                beanInfo.features |= SerializerFeature.WriteClassName.mask;
                put((Type) clazz, new JavaBeanSerializer(beanInfo));
            } else if (TimeZone.class.isAssignableFrom(clazz)) {
                put((Type) clazz, MiscCodec.instance);
            } else if (Appendable.class.isAssignableFrom(clazz)) {
                put((Type) clazz, AppendableSerializer.instance);
            } else if (Charset.class.isAssignableFrom(clazz)) {
                put((Type) clazz, ToStringSerializer.instance);
            } else if (Enumeration.class.isAssignableFrom(clazz)) {
                put((Type) clazz, EnumerationSerializer.instance);
            } else if (Calendar.class.isAssignableFrom(clazz) || XMLGregorianCalendar.class.isAssignableFrom(clazz)) {
                put((Type) clazz, CalendarCodec.instance);
            } else if (Clob.class.isAssignableFrom(clazz)) {
                put((Type) clazz, ClobSeriliazer.instance);
            } else if (TypeUtils.isPath(clazz)) {
                put((Type) clazz, ToStringSerializer.instance);
            } else if (Iterator.class.isAssignableFrom(clazz)) {
                put((Type) clazz, MiscCodec.instance);
            } else {
                String className = clazz.getName();
                if (className.startsWith("java.awt.") && AwtCodec.support(clazz)) {
                    if (!awtError) {
                        try {
                            put(Class.forName("java.awt.Color"), AwtCodec.instance);
                            put(Class.forName("java.awt.Font"), AwtCodec.instance);
                            put(Class.forName("java.awt.Point"), AwtCodec.instance);
                            put(Class.forName("java.awt.Rectangle"), AwtCodec.instance);
                        } catch (Throwable th) {
                            awtError = true;
                        }
                    }
                    return AwtCodec.instance;
                }
                if (!jdk8Error && (className.startsWith("java.time.") || className.startsWith("java.util.Optional"))) {
                    try {
                        put(Class.forName("java.time.LocalDateTime"), Jdk8DateCodec.instance);
                        put(Class.forName("java.time.LocalDate"), Jdk8DateCodec.instance);
                        put(Class.forName("java.time.LocalTime"), Jdk8DateCodec.instance);
                        put(Class.forName("java.time.ZonedDateTime"), Jdk8DateCodec.instance);
                        put(Class.forName("java.time.OffsetDateTime"), Jdk8DateCodec.instance);
                        put(Class.forName("java.time.OffsetTime"), Jdk8DateCodec.instance);
                        put(Class.forName("java.time.ZoneOffset"), Jdk8DateCodec.instance);
                        put(Class.forName("java.time.ZoneRegion"), Jdk8DateCodec.instance);
                        put(Class.forName("java.time.Period"), Jdk8DateCodec.instance);
                        put(Class.forName("java.time.Duration"), Jdk8DateCodec.instance);
                        put(Class.forName("java.time.Instant"), Jdk8DateCodec.instance);
                        put(Class.forName("java.util.Optional"), OptionalCodec.instance);
                        put(Class.forName("java.util.OptionalDouble"), OptionalCodec.instance);
                        put(Class.forName("java.util.OptionalInt"), OptionalCodec.instance);
                        put(Class.forName("java.util.OptionalLong"), OptionalCodec.instance);
                        objectSerializer = (ObjectSerializer) this.serializers.get(clazz);
                        if (objectSerializer != null) {
                            return objectSerializer;
                        }
                    } catch (Throwable th2) {
                        jdk8Error = true;
                    }
                }
                if (!oracleJdbcError && className.startsWith("oracle.sql.")) {
                    try {
                        put(Class.forName("oracle.sql.DATE"), DateCodec.instance);
                        put(Class.forName("oracle.sql.TIMESTAMP"), DateCodec.instance);
                        objectSerializer = (ObjectSerializer) this.serializers.get(clazz);
                        if (objectSerializer != null) {
                            return objectSerializer;
                        }
                    } catch (Throwable th3) {
                        oracleJdbcError = true;
                    }
                }
                if (!springfoxError && className.equals("springfox.documentation.spring.web.json.Json")) {
                    try {
                        put(Class.forName("springfox.documentation.spring.web.json.Json"), SwaggerJsonSerializer.instance);
                        objectSerializer = (ObjectSerializer) this.serializers.get(clazz);
                        if (objectSerializer != null) {
                            return objectSerializer;
                        }
                    } catch (ClassNotFoundException e3) {
                        springfoxError = true;
                    }
                }
                if (TypeUtils.isProxy(clazz)) {
                    ObjectSerializer superWriter = getObjectWriter(clazz.getSuperclass());
                    put((Type) clazz, superWriter);
                    return superWriter;
                } else if (create) {
                    put((Type) clazz, createJavaBeanSerializer((Class) clazz));
                }
            }
            objectSerializer = (ObjectSerializer) this.serializers.get(clazz);
        }
        return objectSerializer;
    }

    public final ObjectSerializer get(Type key) {
        return (ObjectSerializer) this.serializers.get(key);
    }

    public boolean put(Object type, Object value) {
        return put((Type) type, (ObjectSerializer) value);
    }

    public boolean put(Type type, ObjectSerializer value) {
        return this.serializers.put(type, value);
    }
}
