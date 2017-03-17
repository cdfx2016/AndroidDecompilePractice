package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JavaBeanSerializer extends SerializeFilterable implements ObjectSerializer {
    protected SerializeBeanInfo beanInfo;
    protected final FieldSerializer[] getters;
    protected final FieldSerializer[] sortedGetters;

    public JavaBeanSerializer(Class<?> beanType) {
        this((Class) beanType, (Map) null);
    }

    public JavaBeanSerializer(Class<?> beanType, String... aliasList) {
        this((Class) beanType, createAliasMap(aliasList));
    }

    static Map<String, String> createAliasMap(String... aliasList) {
        Map<String, String> aliasMap = new HashMap();
        for (String alias : aliasList) {
            aliasMap.put(alias, alias);
        }
        return aliasMap;
    }

    public JavaBeanSerializer(Class<?> beanType, Map<String, String> aliasMap) {
        this(TypeUtils.buildBeanInfo(beanType, aliasMap, null));
    }

    public JavaBeanSerializer(SerializeBeanInfo beanInfo) {
        int i;
        this.beanInfo = beanInfo;
        this.sortedGetters = new FieldSerializer[beanInfo.sortedFields.length];
        for (i = 0; i < this.sortedGetters.length; i++) {
            this.sortedGetters[i] = new FieldSerializer(beanInfo.beanType, beanInfo.sortedFields[i]);
        }
        if (beanInfo.fields == beanInfo.sortedFields) {
            this.getters = this.sortedGetters;
            return;
        }
        this.getters = new FieldSerializer[beanInfo.fields.length];
        for (i = 0; i < this.getters.length; i++) {
            this.getters[i] = getFieldSerializer(beanInfo.fields[i].name);
        }
    }

    public void writeDirectNonContext(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        write(serializer, object, fieldName, fieldType, features);
    }

    public void writeAsArray(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        write(serializer, object, fieldName, fieldType, features);
    }

    public void writeAsArrayNonContext(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        write(serializer, object, fieldName, fieldType, features);
    }

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
        } else if (!writeReference(serializer, object, features)) {
            FieldSerializer[] getters;
            if (out.sortField) {
                getters = this.sortedGetters;
            } else {
                getters = this.getters;
            }
            SerialContext parent = serializer.context;
            serializer.setContext(parent, object, fieldName, this.beanInfo.features, features);
            boolean writeAsArray = isWriteAsArray(serializer, features);
            char startSeperator = writeAsArray ? '[' : '{';
            char endSeperator = writeAsArray ? ']' : '}';
            Object originalValue;
            try {
                out.append(startSeperator);
                if (getters.length > 0) {
                    if (out.isEnabled(SerializerFeature.PrettyFormat)) {
                        serializer.incrementIndent();
                        serializer.println();
                    }
                }
                boolean commaFlag = false;
                if (((this.beanInfo.features & SerializerFeature.WriteClassName.mask) != 0 || serializer.isWriteClassName(fieldType, object)) && object.getClass() != fieldType) {
                    writeClassName(serializer, object);
                    commaFlag = true;
                }
                char seperator = commaFlag ? ',' : '\u0000';
                boolean directWritePrefix = out.quoteFieldNames && !out.useSingleQuotes;
                commaFlag = writeBefore(serializer, object, seperator) == ',';
                boolean skipTransient = out.isEnabled(SerializerFeature.SkipTransientField);
                boolean ignoreNonFieldGetter = out.isEnabled(SerializerFeature.IgnoreNonFieldGetter);
                for (FieldSerializer fieldSerializer : getters) {
                    Field field = fieldSerializer.fieldInfo.field;
                    FieldInfo fieldInfo = fieldSerializer.fieldInfo;
                    String fieldInfoName = fieldInfo.name;
                    Class<?> fieldClass = fieldInfo.fieldClass;
                    if (!((skipTransient && field != null && fieldInfo.fieldTransient) || (ignoreNonFieldGetter && field == null))) {
                        if (applyName(serializer, object, fieldInfo.name)) {
                            if (applyLabel(serializer, fieldInfo.label)) {
                                originalValue = fieldSerializer.getPropertyValue(object);
                                if (apply(serializer, object, fieldInfoName, originalValue)) {
                                    String key = processKey(serializer, object, fieldInfoName, originalValue);
                                    Object propertyValue = processValue(serializer, fieldSerializer.fieldContext, object, fieldInfoName, originalValue);
                                    if (!(propertyValue != null || writeAsArray || fieldSerializer.writeNull)) {
                                        if (!out.isEnabled(SerializerFeature.WRITE_MAP_NULL_FEATURES)) {
                                            continue;
                                        }
                                    }
                                    if (propertyValue != null && out.notWriteDefaultValue) {
                                        Class<?> fieldCLass = fieldInfo.fieldClass;
                                        if (fieldCLass == Byte.TYPE) {
                                            if ((propertyValue instanceof Byte) && ((Byte) propertyValue).byteValue() == (byte) 0) {
                                            }
                                        }
                                        if (fieldCLass == Short.TYPE) {
                                            if ((propertyValue instanceof Short) && ((Short) propertyValue).shortValue() == (short) 0) {
                                            }
                                        }
                                        if (fieldCLass == Integer.TYPE) {
                                            if ((propertyValue instanceof Integer) && ((Integer) propertyValue).intValue() == 0) {
                                            }
                                        }
                                        if (fieldCLass == Long.TYPE) {
                                            if ((propertyValue instanceof Long) && ((Long) propertyValue).longValue() == 0) {
                                            }
                                        }
                                        if (fieldCLass == Float.TYPE) {
                                            if ((propertyValue instanceof Float) && ((Float) propertyValue).floatValue() == 0.0f) {
                                            }
                                        }
                                        if (fieldCLass == Double.TYPE) {
                                            if ((propertyValue instanceof Double) && ((Double) propertyValue).doubleValue() == 0.0d) {
                                            }
                                        }
                                        if (fieldCLass == Boolean.TYPE && (propertyValue instanceof Boolean) && !((Boolean) propertyValue).booleanValue()) {
                                        }
                                    }
                                    if (commaFlag) {
                                        out.write(44);
                                        if (out.isEnabled(SerializerFeature.PrettyFormat)) {
                                            serializer.println();
                                        }
                                    }
                                    if (key != fieldInfoName) {
                                        if (!writeAsArray) {
                                            out.writeFieldName(key, true);
                                        }
                                        serializer.write(propertyValue);
                                    } else if (originalValue != propertyValue) {
                                        if (!writeAsArray) {
                                            fieldSerializer.writePrefix(serializer);
                                        }
                                        serializer.write(propertyValue);
                                    } else {
                                        if (!writeAsArray) {
                                            if (directWritePrefix) {
                                                out.write(fieldInfo.name_chars, 0, fieldInfo.name_chars.length);
                                            } else {
                                                fieldSerializer.writePrefix(serializer);
                                            }
                                        }
                                        if (writeAsArray) {
                                            fieldSerializer.writeValue(serializer, propertyValue);
                                        } else {
                                            JSONField fieldAnnotation = fieldInfo.getAnnotation();
                                            if (fieldClass != String.class || (fieldAnnotation != null && fieldAnnotation.serializeUsing() != Void.class)) {
                                                fieldSerializer.writeValue(serializer, propertyValue);
                                            } else if (propertyValue != null) {
                                                String propertyValueString = (String) propertyValue;
                                                if (out.useSingleQuotes) {
                                                    out.writeStringWithSingleQuote(propertyValueString);
                                                } else {
                                                    out.writeStringWithDoubleQuote(propertyValueString, '\u0000');
                                                }
                                            } else if ((out.features & SerializerFeature.WriteNullStringAsEmpty.mask) == 0 && (fieldSerializer.features & SerializerFeature.WriteNullStringAsEmpty.mask) == 0) {
                                                out.writeNull();
                                            } else {
                                                out.writeString("");
                                            }
                                        }
                                    }
                                    commaFlag = true;
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    }
                }
                writeAfter(serializer, object, commaFlag ? ',' : '\u0000');
                if (getters.length > 0) {
                    if (out.isEnabled(SerializerFeature.PrettyFormat)) {
                        serializer.decrementIdent();
                        serializer.println();
                    }
                }
                out.append(endSeperator);
                serializer.context = parent;
            } catch (InvocationTargetException ex) {
                if (out.isEnabled(SerializerFeature.IgnoreErrorGetter)) {
                    originalValue = null;
                } else {
                    throw ex;
                }
            } catch (Exception e) {
                try {
                    String errorMessage = "write javaBean error";
                    if (object != null) {
                        errorMessage = errorMessage + ", class " + object.getClass().getName();
                    }
                    if (fieldName != null) {
                        errorMessage = errorMessage + ", fieldName : " + fieldName;
                    }
                    if (e.getMessage() != null) {
                        errorMessage = errorMessage + ", " + e.getMessage();
                    }
                    throw new JSONException(errorMessage, e);
                } catch (Throwable th) {
                    serializer.context = parent;
                }
            }
        }
    }

    protected void writeClassName(JSONSerializer serializer, Object object) {
        serializer.out.writeFieldName(serializer.config.typeKey, false);
        String typeName = this.beanInfo.typeName;
        if (typeName == null) {
            Class<?> clazz = object.getClass();
            if (TypeUtils.isProxy(clazz)) {
                clazz = clazz.getSuperclass();
            }
            typeName = clazz.getName();
        }
        serializer.write(typeName);
    }

    public boolean writeReference(JSONSerializer serializer, Object object, int fieldFeatures) {
        SerialContext context = serializer.context;
        int mask = SerializerFeature.DisableCircularReferenceDetect.mask;
        if (context == null || (context.features & mask) != 0 || (fieldFeatures & mask) != 0 || serializer.references == null || !serializer.references.containsKey(object)) {
            return false;
        }
        serializer.writeReference(object);
        return true;
    }

    protected boolean isWriteAsArray(JSONSerializer serializer) {
        return isWriteAsArray(serializer, 0);
    }

    protected boolean isWriteAsArray(JSONSerializer serializer, int fieldFeatrues) {
        int mask = SerializerFeature.BeanToArray.mask;
        return ((this.beanInfo.features & mask) == 0 && !serializer.out.beanToArray && (fieldFeatrues & mask) == 0) ? false : true;
    }

    public Object getFieldValue(Object object, String key) {
        FieldSerializer fieldDeser = getFieldSerializer(key);
        if (fieldDeser == null) {
            throw new JSONException("field not found. " + key);
        }
        try {
            return fieldDeser.getPropertyValue(object);
        } catch (InvocationTargetException ex) {
            throw new JSONException("getFieldValue error." + key, ex);
        } catch (IllegalAccessException ex2) {
            throw new JSONException("getFieldValue error." + key, ex2);
        }
    }

    public FieldSerializer getFieldSerializer(String key) {
        if (key == null) {
            return null;
        }
        int low = 0;
        int high = this.sortedGetters.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = this.sortedGetters[mid].fieldInfo.name.compareTo(key);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp <= 0) {
                return this.sortedGetters[mid];
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    public List<Object> getFieldValues(Object object) throws Exception {
        List<Object> fieldValues = new ArrayList(this.sortedGetters.length);
        for (FieldSerializer getter : this.sortedGetters) {
            fieldValues.add(getter.getPropertyValue(object));
        }
        return fieldValues;
    }

    public int getSize(Object object) throws Exception {
        int size = 0;
        for (FieldSerializer getter : this.sortedGetters) {
            if (getter.getPropertyValue(object) != null) {
                size++;
            }
        }
        return size;
    }

    public Map<String, Object> getFieldValuesMap(Object object) throws Exception {
        Map<String, Object> map = new LinkedHashMap(this.sortedGetters.length);
        for (FieldSerializer getter : this.sortedGetters) {
            map.put(getter.fieldInfo.name, getter.getPropertyValue(object));
        }
        return map;
    }

    protected BeanContext getBeanContext(int orinal) {
        return this.sortedGetters[orinal].fieldContext;
    }

    protected Type getFieldType(int ordinal) {
        return this.sortedGetters[ordinal].fieldInfo.fieldType;
    }

    protected char writeBefore(JSONSerializer jsonBeanDeser, Object object, char seperator) {
        if (jsonBeanDeser.beforeFilters != null) {
            for (BeforeFilter beforeFilter : jsonBeanDeser.beforeFilters) {
                seperator = beforeFilter.writeBefore(jsonBeanDeser, object, seperator);
            }
        }
        if (this.beforeFilters != null) {
            for (BeforeFilter beforeFilter2 : this.beforeFilters) {
                seperator = beforeFilter2.writeBefore(jsonBeanDeser, object, seperator);
            }
        }
        return seperator;
    }

    protected char writeAfter(JSONSerializer jsonBeanDeser, Object object, char seperator) {
        if (jsonBeanDeser.afterFilters != null) {
            for (AfterFilter afterFilter : jsonBeanDeser.afterFilters) {
                seperator = afterFilter.writeAfter(jsonBeanDeser, object, seperator);
            }
        }
        if (this.afterFilters != null) {
            for (AfterFilter afterFilter2 : this.afterFilters) {
                seperator = afterFilter2.writeAfter(jsonBeanDeser, object, seperator);
            }
        }
        return seperator;
    }

    protected boolean applyLabel(JSONSerializer jsonBeanDeser, String label) {
        if (jsonBeanDeser.labelFilters != null) {
            for (LabelFilter propertyFilter : jsonBeanDeser.labelFilters) {
                if (!propertyFilter.apply(label)) {
                    return false;
                }
            }
        }
        if (this.labelFilters != null) {
            for (LabelFilter propertyFilter2 : this.labelFilters) {
                if (!propertyFilter2.apply(label)) {
                    return false;
                }
            }
        }
        return true;
    }
}
