package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONLexerBase;
import com.alibaba.fastjson.parser.ParseContext;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.JavaBeanInfo;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.xiaomi.mipush.sdk.Constants;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JavaBeanDeserializer implements ObjectDeserializer {
    public final JavaBeanInfo beanInfo;
    protected final Class<?> clazz;
    private ConcurrentMap<String, Object> extraFieldDeserializers;
    private final FieldDeserializer[] fieldDeserializers;
    protected final FieldDeserializer[] sortedFieldDeserializers;

    public JavaBeanDeserializer(ParserConfig config, Class<?> clazz) {
        this(config, clazz, clazz);
    }

    public JavaBeanDeserializer(ParserConfig config, Class<?> clazz, Type type) {
        this(config, JavaBeanInfo.build(clazz, type, config.propertyNamingStrategy));
    }

    public JavaBeanDeserializer(ParserConfig config, JavaBeanInfo beanInfo) {
        int i;
        this.clazz = beanInfo.clazz;
        this.beanInfo = beanInfo;
        this.sortedFieldDeserializers = new FieldDeserializer[beanInfo.sortedFields.length];
        int size = beanInfo.sortedFields.length;
        for (i = 0; i < size; i++) {
            this.sortedFieldDeserializers[i] = config.createFieldDeserializer(config, beanInfo, beanInfo.sortedFields[i]);
        }
        this.fieldDeserializers = new FieldDeserializer[beanInfo.fields.length];
        size = beanInfo.fields.length;
        for (i = 0; i < size; i++) {
            this.fieldDeserializers[i] = getFieldDeserializer(beanInfo.fields[i].name);
        }
    }

    public FieldDeserializer getFieldDeserializer(String key) {
        if (key == null) {
            return null;
        }
        int low = 0;
        int high = this.sortedFieldDeserializers.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = this.sortedFieldDeserializers[mid].fieldInfo.name.compareTo(key);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp <= 0) {
                return this.sortedFieldDeserializers[mid];
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    public Object createInstance(DefaultJSONParser parser, Type type) {
        Class<?> clazz;
        if ((type instanceof Class) && this.clazz.isInterface()) {
            clazz = (Class) type;
            return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, new JSONObject());
        } else if (this.beanInfo.defaultConstructor == null) {
            return null;
        } else {
            Object object;
            Constructor<?> constructor = this.beanInfo.defaultConstructor;
            if (this.beanInfo.defaultConstructorParameterSize == 0) {
                object = constructor.newInstance(new Object[0]);
            } else {
                ParseContext context = parser.getContext();
                String parentName = context.object.getClass().getName();
                String typeName = "";
                if (type instanceof Class) {
                    typeName = ((Class) type).getName();
                }
                if (parentName.length() != typeName.lastIndexOf(36) - 1) {
                    char[] typeChars = typeName.toCharArray();
                    StringBuilder clsNameBuilder = new StringBuilder();
                    clsNameBuilder.append(parentName).append("$");
                    Map<String, Object> outterCached = new HashMap();
                    outterCached.put(parentName, context.object);
                    for (int i = parentName.length() + 1; i <= typeName.lastIndexOf(36); i++) {
                        char thisChar = typeChars[i];
                        if (thisChar == '$') {
                            String clsName = clsNameBuilder.toString();
                            Object outter = outterCached.get(parentName);
                            try {
                                clazz = Class.forName(parentName);
                                if (outter != null) {
                                    Constructor<?> innerClsConstructor = Class.forName(clsName).getDeclaredConstructor(new Class[]{clazz});
                                    if (!innerClsConstructor.isAccessible()) {
                                        innerClsConstructor.setAccessible(true);
                                    }
                                    outterCached.put(clsName, innerClsConstructor.newInstance(new Object[]{outter}));
                                    parentName = clsName;
                                }
                            } catch (ClassNotFoundException e) {
                                throw new JSONException("unable to find class " + parentName);
                            } catch (NoSuchMethodException e2) {
                                throw new RuntimeException(e2);
                            } catch (InvocationTargetException e3) {
                                throw new RuntimeException("can not instantiate " + clsName);
                            } catch (IllegalAccessException e4) {
                                throw new RuntimeException(e4);
                            } catch (InstantiationException e5) {
                                throw new RuntimeException(e5);
                            } catch (Exception e6) {
                                throw new JSONException("create instance error, class " + this.clazz.getName(), e6);
                            }
                        }
                        clsNameBuilder.append(thisChar);
                    }
                    object = constructor.newInstance(new Object[]{outterCached.get(parentName)});
                } else {
                    object = constructor.newInstance(new Object[]{context.object});
                }
            }
            if (parser != null && parser.lexer.isEnabled(Feature.InitStringFieldAsEmpty)) {
                for (FieldInfo fieldInfo : this.beanInfo.fields) {
                    if (fieldInfo.fieldClass == String.class) {
                        try {
                            fieldInfo.set(object, "");
                        } catch (Exception e62) {
                            throw new JSONException("create instance error, class " + this.clazz.getName(), e62);
                        }
                    }
                }
            }
            return object;
        }
    }

    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        return deserialze(parser, type, fieldName, 0);
    }

    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName, int features) {
        return deserialze(parser, type, fieldName, null, features);
    }

    public <T> T deserialzeArrayMapping(DefaultJSONParser parser, Type type, Object fieldName, Object object) {
        JSONLexer lexer = parser.lexer;
        if (lexer.token() != 14) {
            throw new JSONException("error");
        }
        object = createInstance(parser, type);
        int i = 0;
        int size = this.sortedFieldDeserializers.length;
        while (i < size) {
            char seperator = i == size + -1 ? ']' : ',';
            FieldDeserializer fieldDeser = this.sortedFieldDeserializers[i];
            Class<?> fieldClass = fieldDeser.fieldInfo.fieldClass;
            if (fieldClass == Integer.TYPE) {
                fieldDeser.setValue(object, lexer.scanInt(seperator));
            } else if (fieldClass == String.class) {
                fieldDeser.setValue(object, lexer.scanString(seperator));
            } else if (fieldClass == Long.TYPE) {
                fieldDeser.setValue(object, lexer.scanLong(seperator));
            } else if (fieldClass.isEnum()) {
                Object value;
                char ch = lexer.getCurrent();
                if (ch == '\"' || ch == 'n') {
                    value = lexer.scanEnum(fieldClass, parser.getSymbolTable(), seperator);
                } else if (ch < '0' || ch > '9') {
                    value = scanEnum(lexer, seperator);
                } else {
                    value = ((EnumDeserializer) ((DefaultFieldDeserializer) fieldDeser).getFieldValueDeserilizer(parser.getConfig())).valueOf(lexer.scanInt(seperator));
                }
                fieldDeser.setValue(object, value);
            } else if (fieldClass == Boolean.TYPE) {
                fieldDeser.setValue(object, lexer.scanBoolean(seperator));
            } else if (fieldClass == Float.TYPE) {
                fieldDeser.setValue(object, Float.valueOf(lexer.scanFloat(seperator)));
            } else if (fieldClass == Double.TYPE) {
                fieldDeser.setValue(object, Double.valueOf(lexer.scanDouble(seperator)));
            } else if (fieldClass == Date.class && lexer.getCurrent() == '1') {
                fieldDeser.setValue(object, new Date(lexer.scanLong(seperator)));
            } else {
                lexer.nextToken(14);
                fieldDeser.setValue(object, parser.parseObject(fieldDeser.fieldInfo.fieldType));
                check(lexer, seperator == ']' ? 15 : 16);
            }
            i++;
        }
        lexer.nextToken(16);
        return object;
    }

    protected void check(JSONLexer lexer, int token) {
        if (lexer.token() != token) {
            throw new JSONException("syntax error");
        }
    }

    protected Enum<?> scanEnum(JSONLexer lexer, char seperator) {
        throw new JSONException("illegal enum. " + lexer.info());
    }

    protected <T> T deserialze(com.alibaba.fastjson.parser.DefaultJSONParser r47, java.lang.reflect.Type r48, java.lang.Object r49, java.lang.Object r50, int r51) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer.deserialze(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.reflect.Type, java.lang.Object, java.lang.Object, int):T. bs: [B:15:0x0040, B:331:0x0643, B:346:0x0694, B:354:0x06c9]
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:86)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
*/
        /*
        r46 = this;
        r4 = com.alibaba.fastjson.JSON.class;
        r0 = r48;
        if (r0 == r4) goto L_0x000c;
    L_0x0006:
        r4 = com.alibaba.fastjson.JSONObject.class;
        r0 = r48;
        if (r0 != r4) goto L_0x0011;
    L_0x000c:
        r12 = r47.parse();
    L_0x0010:
        return r12;
    L_0x0011:
        r0 = r47;
        r0 = r0.lexer;
        r30 = r0;
        r30 = (com.alibaba.fastjson.parser.JSONLexerBase) r30;
        r42 = r30.token();
        r4 = 8;
        r0 = r42;
        if (r0 != r4) goto L_0x002c;
    L_0x0023:
        r4 = 16;
        r0 = r30;
        r0.nextToken(r4);
        r12 = 0;
        goto L_0x0010;
    L_0x002c:
        r15 = r47.getContext();
        if (r50 == 0) goto L_0x0036;
    L_0x0032:
        if (r15 == 0) goto L_0x0036;
    L_0x0034:
        r15 = r15.parent;
    L_0x0036:
        r13 = 0;
        r9 = 0;
        r4 = 13;
        r0 = r42;
        if (r0 != r4) goto L_0x0059;
    L_0x003e:
        r4 = 16;
        r0 = r30;	 Catch:{ all -> 0x0133 }
        r0.nextToken(r4);	 Catch:{ all -> 0x0133 }
        if (r50 != 0) goto L_0x004b;	 Catch:{ all -> 0x0133 }
    L_0x0047:
        r50 = r46.createInstance(r47, r48);	 Catch:{ all -> 0x0133 }
    L_0x004b:
        if (r13 == 0) goto L_0x0051;
    L_0x004d:
        r0 = r50;
        r13.object = r0;
    L_0x0051:
        r0 = r47;
        r0.setContext(r15);
        r12 = r50;
        goto L_0x0010;
    L_0x0059:
        r4 = 14;
        r0 = r42;
        if (r0 != r4) goto L_0x0094;
    L_0x005f:
        r4 = com.alibaba.fastjson.parser.Feature.SupportArrayToBean;	 Catch:{ all -> 0x0133 }
        r0 = r4.mask;	 Catch:{ all -> 0x0133 }
        r31 = r0;	 Catch:{ all -> 0x0133 }
        r0 = r46;	 Catch:{ all -> 0x0133 }
        r4 = r0.beanInfo;	 Catch:{ all -> 0x0133 }
        r4 = r4.parserFeatures;	 Catch:{ all -> 0x0133 }
        r4 = r4 & r31;	 Catch:{ all -> 0x0133 }
        if (r4 != 0) goto L_0x007d;	 Catch:{ all -> 0x0133 }
    L_0x006f:
        r4 = com.alibaba.fastjson.parser.Feature.SupportArrayToBean;	 Catch:{ all -> 0x0133 }
        r0 = r30;	 Catch:{ all -> 0x0133 }
        r4 = r0.isEnabled(r4);	 Catch:{ all -> 0x0133 }
        if (r4 != 0) goto L_0x007d;	 Catch:{ all -> 0x0133 }
    L_0x0079:
        r4 = r51 & r31;	 Catch:{ all -> 0x0133 }
        if (r4 == 0) goto L_0x0091;	 Catch:{ all -> 0x0133 }
    L_0x007d:
        r29 = 1;	 Catch:{ all -> 0x0133 }
    L_0x007f:
        if (r29 == 0) goto L_0x0094;	 Catch:{ all -> 0x0133 }
    L_0x0081:
        r12 = r46.deserialzeArrayMapping(r47, r48, r49, r50);	 Catch:{ all -> 0x0133 }
        if (r13 == 0) goto L_0x008b;
    L_0x0087:
        r0 = r50;
        r13.object = r0;
    L_0x008b:
        r0 = r47;
        r0.setContext(r15);
        goto L_0x0010;
    L_0x0091:
        r29 = 0;
        goto L_0x007f;
    L_0x0094:
        r4 = 12;
        r0 = r42;
        if (r0 == r4) goto L_0x0140;
    L_0x009a:
        r4 = 16;
        r0 = r42;
        if (r0 == r4) goto L_0x0140;
    L_0x00a0:
        r4 = r30.isBlankInput();	 Catch:{ all -> 0x0133 }
        if (r4 == 0) goto L_0x00b4;
    L_0x00a6:
        r12 = 0;
        if (r13 == 0) goto L_0x00ad;
    L_0x00a9:
        r0 = r50;
        r13.object = r0;
    L_0x00ad:
        r0 = r47;
        r0.setContext(r15);
        goto L_0x0010;
    L_0x00b4:
        r4 = 4;
        r0 = r42;
        if (r0 != r4) goto L_0x00d4;
    L_0x00b9:
        r41 = r30.stringVal();	 Catch:{ all -> 0x0133 }
        r4 = r41.length();	 Catch:{ all -> 0x0133 }
        if (r4 != 0) goto L_0x00d4;	 Catch:{ all -> 0x0133 }
    L_0x00c3:
        r30.nextToken();	 Catch:{ all -> 0x0133 }
        r12 = 0;
        if (r13 == 0) goto L_0x00cd;
    L_0x00c9:
        r0 = r50;
        r13.object = r0;
    L_0x00cd:
        r0 = r47;
        r0.setContext(r15);
        goto L_0x0010;
    L_0x00d4:
        r4 = 14;
        r0 = r42;
        if (r0 != r4) goto L_0x00f6;
    L_0x00da:
        r4 = r30.getCurrent();	 Catch:{ all -> 0x0133 }
        r5 = 93;	 Catch:{ all -> 0x0133 }
        if (r4 != r5) goto L_0x00f6;	 Catch:{ all -> 0x0133 }
    L_0x00e2:
        r30.next();	 Catch:{ all -> 0x0133 }
        r30.nextToken();	 Catch:{ all -> 0x0133 }
        r12 = 0;
        if (r13 == 0) goto L_0x00ef;
    L_0x00eb:
        r0 = r50;
        r13.object = r0;
    L_0x00ef:
        r0 = r47;
        r0.setContext(r15);
        goto L_0x0010;
    L_0x00f6:
        r4 = new java.lang.StringBuffer;	 Catch:{ all -> 0x0133 }
        r4.<init>();	 Catch:{ all -> 0x0133 }
        r5 = "syntax error, expect {, actual ";	 Catch:{ all -> 0x0133 }
        r4 = r4.append(r5);	 Catch:{ all -> 0x0133 }
        r5 = r30.tokenName();	 Catch:{ all -> 0x0133 }
        r4 = r4.append(r5);	 Catch:{ all -> 0x0133 }
        r5 = ", pos ";	 Catch:{ all -> 0x0133 }
        r4 = r4.append(r5);	 Catch:{ all -> 0x0133 }
        r5 = r30.pos();	 Catch:{ all -> 0x0133 }
        r10 = r4.append(r5);	 Catch:{ all -> 0x0133 }
        r0 = r49;	 Catch:{ all -> 0x0133 }
        r4 = r0 instanceof java.lang.String;	 Catch:{ all -> 0x0133 }
        if (r4 == 0) goto L_0x0129;	 Catch:{ all -> 0x0133 }
    L_0x011e:
        r4 = ", fieldName ";	 Catch:{ all -> 0x0133 }
        r4 = r10.append(r4);	 Catch:{ all -> 0x0133 }
        r0 = r49;	 Catch:{ all -> 0x0133 }
        r4.append(r0);	 Catch:{ all -> 0x0133 }
    L_0x0129:
        r4 = new com.alibaba.fastjson.JSONException;	 Catch:{ all -> 0x0133 }
        r5 = r10.toString();	 Catch:{ all -> 0x0133 }
        r4.<init>(r5);	 Catch:{ all -> 0x0133 }
        throw r4;	 Catch:{ all -> 0x0133 }
    L_0x0133:
        r4 = move-exception;
    L_0x0134:
        if (r13 == 0) goto L_0x013a;
    L_0x0136:
        r0 = r50;
        r13.object = r0;
    L_0x013a:
        r0 = r47;
        r0.setContext(r15);
        throw r4;
    L_0x0140:
        r0 = r47;	 Catch:{ all -> 0x0133 }
        r4 = r0.resolveStatus;	 Catch:{ all -> 0x0133 }
        r5 = 2;	 Catch:{ all -> 0x0133 }
        if (r4 != r5) goto L_0x014c;	 Catch:{ all -> 0x0133 }
    L_0x0147:
        r4 = 0;	 Catch:{ all -> 0x0133 }
        r0 = r47;	 Catch:{ all -> 0x0133 }
        r0.resolveStatus = r4;	 Catch:{ all -> 0x0133 }
    L_0x014c:
        r22 = 0;
        r27 = r9;
    L_0x0150:
        r6 = 0;
        r21 = 0;
        r23 = 0;
        r20 = 0;
        r0 = r46;	 Catch:{ all -> 0x0370 }
        r4 = r0.sortedFieldDeserializers;	 Catch:{ all -> 0x0370 }
        r4 = r4.length;	 Catch:{ all -> 0x0370 }
        r0 = r22;	 Catch:{ all -> 0x0370 }
        if (r0 >= r4) goto L_0x0172;	 Catch:{ all -> 0x0370 }
    L_0x0160:
        r0 = r46;	 Catch:{ all -> 0x0370 }
        r4 = r0.sortedFieldDeserializers;	 Catch:{ all -> 0x0370 }
        r21 = r4[r22];	 Catch:{ all -> 0x0370 }
        r0 = r21;	 Catch:{ all -> 0x0370 }
        r0 = r0.fieldInfo;	 Catch:{ all -> 0x0370 }
        r23 = r0;	 Catch:{ all -> 0x0370 }
        r0 = r23;	 Catch:{ all -> 0x0370 }
        r0 = r0.fieldClass;	 Catch:{ all -> 0x0370 }
        r20 = r0;	 Catch:{ all -> 0x0370 }
    L_0x0172:
        r33 = 0;	 Catch:{ all -> 0x0370 }
        r45 = 0;	 Catch:{ all -> 0x0370 }
        r26 = 0;	 Catch:{ all -> 0x0370 }
        if (r21 == 0) goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x017a:
        r0 = r23;	 Catch:{ all -> 0x0370 }
        r0 = r0.name_chars;	 Catch:{ all -> 0x0370 }
        r34 = r0;	 Catch:{ all -> 0x0370 }
        r4 = java.lang.Integer.TYPE;	 Catch:{ all -> 0x0370 }
        r0 = r20;	 Catch:{ all -> 0x0370 }
        if (r0 == r4) goto L_0x018c;	 Catch:{ all -> 0x0370 }
    L_0x0186:
        r4 = java.lang.Integer.class;	 Catch:{ all -> 0x0370 }
        r0 = r20;	 Catch:{ all -> 0x0370 }
        if (r0 != r4) goto L_0x01f5;	 Catch:{ all -> 0x0370 }
    L_0x018c:
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r1 = r34;	 Catch:{ all -> 0x0370 }
        r4 = r0.scanFieldInt(r1);	 Catch:{ all -> 0x0370 }
        r26 = java.lang.Integer.valueOf(r4);	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0370 }
        if (r4 <= 0) goto L_0x01e6;	 Catch:{ all -> 0x0370 }
    L_0x019e:
        r33 = 1;	 Catch:{ all -> 0x0370 }
        r45 = 1;	 Catch:{ all -> 0x0370 }
    L_0x01a2:
        if (r33 != 0) goto L_0x04cc;	 Catch:{ all -> 0x0370 }
    L_0x01a4:
        r0 = r47;	 Catch:{ all -> 0x0370 }
        r4 = r0.symbolTable;	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r6 = r0.scanSymbol(r4);	 Catch:{ all -> 0x0370 }
        if (r6 != 0) goto L_0x0334;	 Catch:{ all -> 0x0370 }
    L_0x01b0:
        r42 = r30.token();	 Catch:{ all -> 0x0370 }
        r4 = 13;	 Catch:{ all -> 0x0370 }
        r0 = r42;	 Catch:{ all -> 0x0370 }
        if (r0 != r4) goto L_0x0320;	 Catch:{ all -> 0x0370 }
    L_0x01ba:
        r4 = 16;	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r0.nextToken(r4);	 Catch:{ all -> 0x0370 }
        r9 = r27;
    L_0x01c3:
        if (r50 != 0) goto L_0x064f;
    L_0x01c5:
        if (r9 != 0) goto L_0x05b7;
    L_0x01c7:
        r50 = r46.createInstance(r47, r48);	 Catch:{ all -> 0x0133 }
        if (r13 != 0) goto L_0x01d7;	 Catch:{ all -> 0x0133 }
    L_0x01cd:
        r0 = r47;	 Catch:{ all -> 0x0133 }
        r1 = r50;	 Catch:{ all -> 0x0133 }
        r2 = r49;	 Catch:{ all -> 0x0133 }
        r13 = r0.setContext(r15, r1, r2);	 Catch:{ all -> 0x0133 }
    L_0x01d7:
        if (r13 == 0) goto L_0x01dd;
    L_0x01d9:
        r0 = r50;
        r13.object = r0;
    L_0x01dd:
        r0 = r47;
        r0.setContext(r15);
        r12 = r50;
        goto L_0x0010;
    L_0x01e6:
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0370 }
        r5 = -2;	 Catch:{ all -> 0x0370 }
        if (r4 != r5) goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x01ed:
        r9 = r27;	 Catch:{ all -> 0x0370 }
    L_0x01ef:
        r22 = r22 + 1;	 Catch:{ all -> 0x0370 }
        r27 = r9;	 Catch:{ all -> 0x0370 }
        goto L_0x0150;	 Catch:{ all -> 0x0370 }
    L_0x01f5:
        r4 = java.lang.Long.TYPE;	 Catch:{ all -> 0x0370 }
        r0 = r20;	 Catch:{ all -> 0x0370 }
        if (r0 == r4) goto L_0x0201;	 Catch:{ all -> 0x0370 }
    L_0x01fb:
        r4 = java.lang.Long.class;	 Catch:{ all -> 0x0370 }
        r0 = r20;	 Catch:{ all -> 0x0370 }
        if (r0 != r4) goto L_0x0222;	 Catch:{ all -> 0x0370 }
    L_0x0201:
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r1 = r34;	 Catch:{ all -> 0x0370 }
        r4 = r0.scanFieldLong(r1);	 Catch:{ all -> 0x0370 }
        r26 = java.lang.Long.valueOf(r4);	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0370 }
        if (r4 <= 0) goto L_0x0218;	 Catch:{ all -> 0x0370 }
    L_0x0213:
        r33 = 1;	 Catch:{ all -> 0x0370 }
        r45 = 1;	 Catch:{ all -> 0x0370 }
        goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x0218:
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0370 }
        r5 = -2;	 Catch:{ all -> 0x0370 }
        if (r4 != r5) goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x021f:
        r9 = r27;	 Catch:{ all -> 0x0370 }
        goto L_0x01ef;	 Catch:{ all -> 0x0370 }
    L_0x0222:
        r4 = java.lang.String.class;	 Catch:{ all -> 0x0370 }
        r0 = r20;	 Catch:{ all -> 0x0370 }
        if (r0 != r4) goto L_0x0246;	 Catch:{ all -> 0x0370 }
    L_0x0228:
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r1 = r34;	 Catch:{ all -> 0x0370 }
        r26 = r0.scanFieldString(r1);	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0370 }
        if (r4 <= 0) goto L_0x023c;	 Catch:{ all -> 0x0370 }
    L_0x0236:
        r33 = 1;	 Catch:{ all -> 0x0370 }
        r45 = 1;	 Catch:{ all -> 0x0370 }
        goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x023c:
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0370 }
        r5 = -2;	 Catch:{ all -> 0x0370 }
        if (r4 != r5) goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x0243:
        r9 = r27;	 Catch:{ all -> 0x0370 }
        goto L_0x01ef;	 Catch:{ all -> 0x0370 }
    L_0x0246:
        r4 = java.lang.Boolean.TYPE;	 Catch:{ all -> 0x0370 }
        r0 = r20;	 Catch:{ all -> 0x0370 }
        if (r0 == r4) goto L_0x0252;	 Catch:{ all -> 0x0370 }
    L_0x024c:
        r4 = java.lang.Boolean.class;	 Catch:{ all -> 0x0370 }
        r0 = r20;	 Catch:{ all -> 0x0370 }
        if (r0 != r4) goto L_0x0275;	 Catch:{ all -> 0x0370 }
    L_0x0252:
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r1 = r34;	 Catch:{ all -> 0x0370 }
        r4 = r0.scanFieldBoolean(r1);	 Catch:{ all -> 0x0370 }
        r26 = java.lang.Boolean.valueOf(r4);	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0370 }
        if (r4 <= 0) goto L_0x026a;	 Catch:{ all -> 0x0370 }
    L_0x0264:
        r33 = 1;	 Catch:{ all -> 0x0370 }
        r45 = 1;	 Catch:{ all -> 0x0370 }
        goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x026a:
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0370 }
        r5 = -2;	 Catch:{ all -> 0x0370 }
        if (r4 != r5) goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x0271:
        r9 = r27;	 Catch:{ all -> 0x0370 }
        goto L_0x01ef;	 Catch:{ all -> 0x0370 }
    L_0x0275:
        r4 = java.lang.Float.TYPE;	 Catch:{ all -> 0x0370 }
        r0 = r20;	 Catch:{ all -> 0x0370 }
        if (r0 == r4) goto L_0x0281;	 Catch:{ all -> 0x0370 }
    L_0x027b:
        r4 = java.lang.Float.class;	 Catch:{ all -> 0x0370 }
        r0 = r20;	 Catch:{ all -> 0x0370 }
        if (r0 != r4) goto L_0x02a4;	 Catch:{ all -> 0x0370 }
    L_0x0281:
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r1 = r34;	 Catch:{ all -> 0x0370 }
        r4 = r0.scanFieldFloat(r1);	 Catch:{ all -> 0x0370 }
        r26 = java.lang.Float.valueOf(r4);	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0370 }
        if (r4 <= 0) goto L_0x0299;	 Catch:{ all -> 0x0370 }
    L_0x0293:
        r33 = 1;	 Catch:{ all -> 0x0370 }
        r45 = 1;	 Catch:{ all -> 0x0370 }
        goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x0299:
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0370 }
        r5 = -2;	 Catch:{ all -> 0x0370 }
        if (r4 != r5) goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x02a0:
        r9 = r27;	 Catch:{ all -> 0x0370 }
        goto L_0x01ef;	 Catch:{ all -> 0x0370 }
    L_0x02a4:
        r4 = java.lang.Double.TYPE;	 Catch:{ all -> 0x0370 }
        r0 = r20;	 Catch:{ all -> 0x0370 }
        if (r0 == r4) goto L_0x02b0;	 Catch:{ all -> 0x0370 }
    L_0x02aa:
        r4 = java.lang.Double.class;	 Catch:{ all -> 0x0370 }
        r0 = r20;	 Catch:{ all -> 0x0370 }
        if (r0 != r4) goto L_0x02d3;	 Catch:{ all -> 0x0370 }
    L_0x02b0:
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r1 = r34;	 Catch:{ all -> 0x0370 }
        r4 = r0.scanFieldDouble(r1);	 Catch:{ all -> 0x0370 }
        r26 = java.lang.Double.valueOf(r4);	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0370 }
        if (r4 <= 0) goto L_0x02c8;	 Catch:{ all -> 0x0370 }
    L_0x02c2:
        r33 = 1;	 Catch:{ all -> 0x0370 }
        r45 = 1;	 Catch:{ all -> 0x0370 }
        goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x02c8:
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0370 }
        r5 = -2;	 Catch:{ all -> 0x0370 }
        if (r4 != r5) goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x02cf:
        r9 = r27;	 Catch:{ all -> 0x0370 }
        goto L_0x01ef;	 Catch:{ all -> 0x0370 }
    L_0x02d3:
        r4 = r20.isEnum();	 Catch:{ all -> 0x0370 }
        if (r4 == 0) goto L_0x0312;	 Catch:{ all -> 0x0370 }
    L_0x02d9:
        r4 = r47.getConfig();	 Catch:{ all -> 0x0370 }
        r0 = r20;	 Catch:{ all -> 0x0370 }
        r4 = r4.getDeserializer(r0);	 Catch:{ all -> 0x0370 }
        r4 = r4 instanceof com.alibaba.fastjson.parser.deserializer.EnumDeserializer;	 Catch:{ all -> 0x0370 }
        if (r4 == 0) goto L_0x0312;	 Catch:{ all -> 0x0370 }
    L_0x02e7:
        r0 = r47;	 Catch:{ all -> 0x0370 }
        r4 = r0.symbolTable;	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r1 = r34;	 Catch:{ all -> 0x0370 }
        r18 = r0.scanFieldSymbol(r1, r4);	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0370 }
        if (r4 <= 0) goto L_0x0307;	 Catch:{ all -> 0x0370 }
    L_0x02f9:
        r33 = 1;	 Catch:{ all -> 0x0370 }
        r45 = 1;	 Catch:{ all -> 0x0370 }
        r0 = r20;	 Catch:{ all -> 0x0370 }
        r1 = r18;	 Catch:{ all -> 0x0370 }
        r26 = java.lang.Enum.valueOf(r0, r1);	 Catch:{ all -> 0x0370 }
        goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x0307:
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0370 }
        r5 = -2;	 Catch:{ all -> 0x0370 }
        if (r4 != r5) goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x030e:
        r9 = r27;	 Catch:{ all -> 0x0370 }
        goto L_0x01ef;	 Catch:{ all -> 0x0370 }
    L_0x0312:
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r1 = r34;	 Catch:{ all -> 0x0370 }
        r4 = r0.matchField(r1);	 Catch:{ all -> 0x0370 }
        if (r4 == 0) goto L_0x06f1;	 Catch:{ all -> 0x0370 }
    L_0x031c:
        r33 = 1;	 Catch:{ all -> 0x0370 }
        goto L_0x01a2;	 Catch:{ all -> 0x0370 }
    L_0x0320:
        r4 = 16;	 Catch:{ all -> 0x0370 }
        r0 = r42;	 Catch:{ all -> 0x0370 }
        if (r0 != r4) goto L_0x0334;	 Catch:{ all -> 0x0370 }
    L_0x0326:
        r4 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas;	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r4 = r0.isEnabled(r4);	 Catch:{ all -> 0x0370 }
        if (r4 == 0) goto L_0x0334;	 Catch:{ all -> 0x0370 }
    L_0x0330:
        r9 = r27;	 Catch:{ all -> 0x0370 }
        goto L_0x01ef;	 Catch:{ all -> 0x0370 }
    L_0x0334:
        r4 = "$ref";	 Catch:{ all -> 0x0370 }
        if (r4 != r6) goto L_0x042e;	 Catch:{ all -> 0x0370 }
    L_0x0338:
        r4 = 4;	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r0.nextTokenWithColon(r4);	 Catch:{ all -> 0x0370 }
        r42 = r30.token();	 Catch:{ all -> 0x0370 }
        r4 = 4;	 Catch:{ all -> 0x0370 }
        r0 = r42;	 Catch:{ all -> 0x0370 }
        if (r0 != r4) goto L_0x03f2;	 Catch:{ all -> 0x0370 }
    L_0x0347:
        r38 = r30.stringVal();	 Catch:{ all -> 0x0370 }
        r4 = "@";	 Catch:{ all -> 0x0370 }
        r0 = r38;	 Catch:{ all -> 0x0370 }
        r4 = r4.equals(r0);	 Catch:{ all -> 0x0370 }
        if (r4 == 0) goto L_0x0375;	 Catch:{ all -> 0x0370 }
    L_0x0355:
        r0 = r15.object;	 Catch:{ all -> 0x0370 }
        r50 = r0;	 Catch:{ all -> 0x0370 }
    L_0x0359:
        r4 = 13;	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r0.nextToken(r4);	 Catch:{ all -> 0x0370 }
        r4 = r30.token();	 Catch:{ all -> 0x0370 }
        r5 = 13;	 Catch:{ all -> 0x0370 }
        if (r4 == r5) goto L_0x040f;	 Catch:{ all -> 0x0370 }
    L_0x0368:
        r4 = new com.alibaba.fastjson.JSONException;	 Catch:{ all -> 0x0370 }
        r5 = "illegal ref";	 Catch:{ all -> 0x0370 }
        r4.<init>(r5);	 Catch:{ all -> 0x0370 }
        throw r4;	 Catch:{ all -> 0x0370 }
    L_0x0370:
        r4 = move-exception;	 Catch:{ all -> 0x0370 }
        r9 = r27;	 Catch:{ all -> 0x0370 }
        goto L_0x0134;	 Catch:{ all -> 0x0370 }
    L_0x0375:
        r4 = "..";	 Catch:{ all -> 0x0370 }
        r0 = r38;	 Catch:{ all -> 0x0370 }
        r4 = r4.equals(r0);	 Catch:{ all -> 0x0370 }
        if (r4 == 0) goto L_0x03a4;	 Catch:{ all -> 0x0370 }
    L_0x037f:
        r0 = r15.parent;	 Catch:{ all -> 0x0370 }
        r37 = r0;	 Catch:{ all -> 0x0370 }
        r0 = r37;	 Catch:{ all -> 0x0370 }
        r4 = r0.object;	 Catch:{ all -> 0x0370 }
        if (r4 == 0) goto L_0x0390;	 Catch:{ all -> 0x0370 }
    L_0x0389:
        r0 = r37;	 Catch:{ all -> 0x0370 }
        r0 = r0.object;	 Catch:{ all -> 0x0370 }
        r50 = r0;	 Catch:{ all -> 0x0370 }
        goto L_0x0359;	 Catch:{ all -> 0x0370 }
    L_0x0390:
        r4 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask;	 Catch:{ all -> 0x0370 }
        r0 = r37;	 Catch:{ all -> 0x0370 }
        r1 = r38;	 Catch:{ all -> 0x0370 }
        r4.<init>(r0, r1);	 Catch:{ all -> 0x0370 }
        r0 = r47;	 Catch:{ all -> 0x0370 }
        r0.addResolveTask(r4);	 Catch:{ all -> 0x0370 }
        r4 = 1;	 Catch:{ all -> 0x0370 }
        r0 = r47;	 Catch:{ all -> 0x0370 }
        r0.resolveStatus = r4;	 Catch:{ all -> 0x0370 }
        goto L_0x0359;	 Catch:{ all -> 0x0370 }
    L_0x03a4:
        r4 = "$";	 Catch:{ all -> 0x0370 }
        r0 = r38;	 Catch:{ all -> 0x0370 }
        r4 = r4.equals(r0);	 Catch:{ all -> 0x0370 }
        if (r4 == 0) goto L_0x03df;	 Catch:{ all -> 0x0370 }
    L_0x03ae:
        r39 = r15;	 Catch:{ all -> 0x0370 }
    L_0x03b0:
        r0 = r39;	 Catch:{ all -> 0x0370 }
        r4 = r0.parent;	 Catch:{ all -> 0x0370 }
        if (r4 == 0) goto L_0x03bd;	 Catch:{ all -> 0x0370 }
    L_0x03b6:
        r0 = r39;	 Catch:{ all -> 0x0370 }
        r0 = r0.parent;	 Catch:{ all -> 0x0370 }
        r39 = r0;	 Catch:{ all -> 0x0370 }
        goto L_0x03b0;	 Catch:{ all -> 0x0370 }
    L_0x03bd:
        r0 = r39;	 Catch:{ all -> 0x0370 }
        r4 = r0.object;	 Catch:{ all -> 0x0370 }
        if (r4 == 0) goto L_0x03ca;	 Catch:{ all -> 0x0370 }
    L_0x03c3:
        r0 = r39;	 Catch:{ all -> 0x0370 }
        r0 = r0.object;	 Catch:{ all -> 0x0370 }
        r50 = r0;	 Catch:{ all -> 0x0370 }
        goto L_0x0359;	 Catch:{ all -> 0x0370 }
    L_0x03ca:
        r4 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask;	 Catch:{ all -> 0x0370 }
        r0 = r39;	 Catch:{ all -> 0x0370 }
        r1 = r38;	 Catch:{ all -> 0x0370 }
        r4.<init>(r0, r1);	 Catch:{ all -> 0x0370 }
        r0 = r47;	 Catch:{ all -> 0x0370 }
        r0.addResolveTask(r4);	 Catch:{ all -> 0x0370 }
        r4 = 1;	 Catch:{ all -> 0x0370 }
        r0 = r47;	 Catch:{ all -> 0x0370 }
        r0.resolveStatus = r4;	 Catch:{ all -> 0x0370 }
        goto L_0x0359;	 Catch:{ all -> 0x0370 }
    L_0x03df:
        r4 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask;	 Catch:{ all -> 0x0370 }
        r0 = r38;	 Catch:{ all -> 0x0370 }
        r4.<init>(r15, r0);	 Catch:{ all -> 0x0370 }
        r0 = r47;	 Catch:{ all -> 0x0370 }
        r0.addResolveTask(r4);	 Catch:{ all -> 0x0370 }
        r4 = 1;	 Catch:{ all -> 0x0370 }
        r0 = r47;	 Catch:{ all -> 0x0370 }
        r0.resolveStatus = r4;	 Catch:{ all -> 0x0370 }
        goto L_0x0359;	 Catch:{ all -> 0x0370 }
    L_0x03f2:
        r4 = new com.alibaba.fastjson.JSONException;	 Catch:{ all -> 0x0370 }
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0370 }
        r5.<init>();	 Catch:{ all -> 0x0370 }
        r7 = "illegal ref, ";	 Catch:{ all -> 0x0370 }
        r5 = r5.append(r7);	 Catch:{ all -> 0x0370 }
        r7 = com.alibaba.fastjson.parser.JSONToken.name(r42);	 Catch:{ all -> 0x0370 }
        r5 = r5.append(r7);	 Catch:{ all -> 0x0370 }
        r5 = r5.toString();	 Catch:{ all -> 0x0370 }
        r4.<init>(r5);	 Catch:{ all -> 0x0370 }
        throw r4;	 Catch:{ all -> 0x0370 }
    L_0x040f:
        r4 = 16;	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r0.nextToken(r4);	 Catch:{ all -> 0x0370 }
        r0 = r47;	 Catch:{ all -> 0x0370 }
        r1 = r50;	 Catch:{ all -> 0x0370 }
        r2 = r49;	 Catch:{ all -> 0x0370 }
        r0.setContext(r15, r1, r2);	 Catch:{ all -> 0x0370 }
        if (r13 == 0) goto L_0x0425;
    L_0x0421:
        r0 = r50;
        r13.object = r0;
    L_0x0425:
        r0 = r47;
        r0.setContext(r15);
        r12 = r50;
        goto L_0x0010;
    L_0x042e:
        r4 = com.alibaba.fastjson.JSON.DEFAULT_TYPE_KEY;	 Catch:{ all -> 0x0370 }
        if (r4 != r6) goto L_0x04cc;	 Catch:{ all -> 0x0370 }
    L_0x0432:
        r4 = 4;	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r0.nextTokenWithColon(r4);	 Catch:{ all -> 0x0370 }
        r4 = r30.token();	 Catch:{ all -> 0x0370 }
        r5 = 4;	 Catch:{ all -> 0x0370 }
        if (r4 != r5) goto L_0x04c3;	 Catch:{ all -> 0x0370 }
    L_0x043f:
        r43 = r30.stringVal();	 Catch:{ all -> 0x0370 }
        r4 = 16;	 Catch:{ all -> 0x0370 }
        r0 = r30;	 Catch:{ all -> 0x0370 }
        r0.nextToken(r4);	 Catch:{ all -> 0x0370 }
        r0 = r46;	 Catch:{ all -> 0x0370 }
        r4 = r0.beanInfo;	 Catch:{ all -> 0x0370 }
        r4 = r4.typeName;	 Catch:{ all -> 0x0370 }
        r0 = r43;	 Catch:{ all -> 0x0370 }
        r4 = r0.equals(r4);	 Catch:{ all -> 0x0370 }
        if (r4 == 0) goto L_0x0467;	 Catch:{ all -> 0x0370 }
    L_0x0458:
        r4 = r30.token();	 Catch:{ all -> 0x0370 }
        r5 = 13;	 Catch:{ all -> 0x0370 }
        if (r4 != r5) goto L_0x06f1;	 Catch:{ all -> 0x0370 }
    L_0x0460:
        r30.nextToken();	 Catch:{ all -> 0x0370 }
        r9 = r27;	 Catch:{ all -> 0x0370 }
        goto L_0x01c3;	 Catch:{ all -> 0x0370 }
    L_0x0467:
        r14 = r47.getConfig();	 Catch:{ all -> 0x0370 }
        r0 = r46;	 Catch:{ all -> 0x0370 }
        r4 = r0.beanInfo;	 Catch:{ all -> 0x0370 }
        r0 = r46;	 Catch:{ all -> 0x0370 }
        r1 = r43;	 Catch:{ all -> 0x0370 }
        r16 = r0.getSeeAlso(r14, r4, r1);	 Catch:{ all -> 0x0370 }
        r44 = 0;	 Catch:{ all -> 0x0370 }
        if (r16 != 0) goto L_0x04a1;	 Catch:{ all -> 0x0370 }
    L_0x047b:
        r4 = r14.getDefaultClassLoader();	 Catch:{ all -> 0x0370 }
        r0 = r43;	 Catch:{ all -> 0x0370 }
        r44 = com.alibaba.fastjson.util.TypeUtils.loadClass(r0, r4);	 Catch:{ all -> 0x0370 }
        r19 = com.alibaba.fastjson.util.TypeUtils.getClass(r48);	 Catch:{ all -> 0x0370 }
        if (r19 == 0) goto L_0x0497;	 Catch:{ all -> 0x0370 }
    L_0x048b:
        if (r44 == 0) goto L_0x04ba;	 Catch:{ all -> 0x0370 }
    L_0x048d:
        r0 = r19;	 Catch:{ all -> 0x0370 }
        r1 = r44;	 Catch:{ all -> 0x0370 }
        r4 = r0.isAssignableFrom(r1);	 Catch:{ all -> 0x0370 }
        if (r4 == 0) goto L_0x04ba;	 Catch:{ all -> 0x0370 }
    L_0x0497:
        r4 = r47.getConfig();	 Catch:{ all -> 0x0370 }
        r0 = r44;	 Catch:{ all -> 0x0370 }
        r16 = r4.getDeserializer(r0);	 Catch:{ all -> 0x0370 }
    L_0x04a1:
        r0 = r16;	 Catch:{ all -> 0x0370 }
        r1 = r47;	 Catch:{ all -> 0x0370 }
        r2 = r44;	 Catch:{ all -> 0x0370 }
        r3 = r49;	 Catch:{ all -> 0x0370 }
        r12 = r0.deserialze(r1, r2, r3);	 Catch:{ all -> 0x0370 }
        if (r13 == 0) goto L_0x04b3;
    L_0x04af:
        r0 = r50;
        r13.object = r0;
    L_0x04b3:
        r0 = r47;
        r0.setContext(r15);
        goto L_0x0010;
    L_0x04ba:
        r4 = new com.alibaba.fastjson.JSONException;	 Catch:{ all -> 0x0370 }
        r5 = "type not match";	 Catch:{ all -> 0x0370 }
        r4.<init>(r5);	 Catch:{ all -> 0x0370 }
        throw r4;	 Catch:{ all -> 0x0370 }
    L_0x04c3:
        r4 = new com.alibaba.fastjson.JSONException;	 Catch:{ all -> 0x0370 }
        r5 = "syntax error";	 Catch:{ all -> 0x0370 }
        r4.<init>(r5);	 Catch:{ all -> 0x0370 }
        throw r4;	 Catch:{ all -> 0x0370 }
    L_0x04cc:
        if (r50 != 0) goto L_0x06ed;	 Catch:{ all -> 0x0370 }
    L_0x04ce:
        if (r27 != 0) goto L_0x06ed;	 Catch:{ all -> 0x0370 }
    L_0x04d0:
        r50 = r46.createInstance(r47, r48);	 Catch:{ all -> 0x0370 }
        if (r50 != 0) goto L_0x06e9;	 Catch:{ all -> 0x0370 }
    L_0x04d6:
        r9 = new java.util.HashMap;	 Catch:{ all -> 0x0370 }
        r0 = r46;	 Catch:{ all -> 0x0370 }
        r4 = r0.fieldDeserializers;	 Catch:{ all -> 0x0370 }
        r4 = r4.length;	 Catch:{ all -> 0x0370 }
        r9.<init>(r4);	 Catch:{ all -> 0x0370 }
    L_0x04e0:
        r0 = r47;	 Catch:{ all -> 0x0133 }
        r1 = r50;	 Catch:{ all -> 0x0133 }
        r2 = r49;	 Catch:{ all -> 0x0133 }
        r13 = r0.setContext(r15, r1, r2);	 Catch:{ all -> 0x0133 }
    L_0x04ea:
        if (r33 == 0) goto L_0x055a;	 Catch:{ all -> 0x0133 }
    L_0x04ec:
        if (r45 != 0) goto L_0x0512;	 Catch:{ all -> 0x0133 }
    L_0x04ee:
        r0 = r21;	 Catch:{ all -> 0x0133 }
        r1 = r47;	 Catch:{ all -> 0x0133 }
        r2 = r50;	 Catch:{ all -> 0x0133 }
        r3 = r48;	 Catch:{ all -> 0x0133 }
        r0.parseField(r1, r2, r3, r9);	 Catch:{ all -> 0x0133 }
    L_0x04f9:
        r4 = r30.token();	 Catch:{ all -> 0x0133 }
        r5 = 16;	 Catch:{ all -> 0x0133 }
        if (r4 == r5) goto L_0x01ef;	 Catch:{ all -> 0x0133 }
    L_0x0501:
        r4 = r30.token();	 Catch:{ all -> 0x0133 }
        r5 = 13;	 Catch:{ all -> 0x0133 }
        if (r4 != r5) goto L_0x0586;	 Catch:{ all -> 0x0133 }
    L_0x0509:
        r4 = 16;	 Catch:{ all -> 0x0133 }
        r0 = r30;	 Catch:{ all -> 0x0133 }
        r0.nextToken(r4);	 Catch:{ all -> 0x0133 }
        goto L_0x01c3;	 Catch:{ all -> 0x0133 }
    L_0x0512:
        if (r50 != 0) goto L_0x0526;	 Catch:{ all -> 0x0133 }
    L_0x0514:
        r0 = r23;	 Catch:{ all -> 0x0133 }
        r4 = r0.name;	 Catch:{ all -> 0x0133 }
        r0 = r26;	 Catch:{ all -> 0x0133 }
        r9.put(r4, r0);	 Catch:{ all -> 0x0133 }
    L_0x051d:
        r0 = r30;	 Catch:{ all -> 0x0133 }
        r4 = r0.matchStat;	 Catch:{ all -> 0x0133 }
        r5 = 4;	 Catch:{ all -> 0x0133 }
        if (r4 != r5) goto L_0x04f9;	 Catch:{ all -> 0x0133 }
    L_0x0524:
        goto L_0x01c3;	 Catch:{ all -> 0x0133 }
    L_0x0526:
        if (r26 != 0) goto L_0x0550;	 Catch:{ all -> 0x0133 }
    L_0x0528:
        r4 = java.lang.Integer.TYPE;	 Catch:{ all -> 0x0133 }
        r0 = r20;	 Catch:{ all -> 0x0133 }
        if (r0 == r4) goto L_0x051d;	 Catch:{ all -> 0x0133 }
    L_0x052e:
        r4 = java.lang.Long.TYPE;	 Catch:{ all -> 0x0133 }
        r0 = r20;	 Catch:{ all -> 0x0133 }
        if (r0 == r4) goto L_0x051d;	 Catch:{ all -> 0x0133 }
    L_0x0534:
        r4 = java.lang.Float.TYPE;	 Catch:{ all -> 0x0133 }
        r0 = r20;	 Catch:{ all -> 0x0133 }
        if (r0 == r4) goto L_0x051d;	 Catch:{ all -> 0x0133 }
    L_0x053a:
        r4 = java.lang.Double.TYPE;	 Catch:{ all -> 0x0133 }
        r0 = r20;	 Catch:{ all -> 0x0133 }
        if (r0 == r4) goto L_0x051d;	 Catch:{ all -> 0x0133 }
    L_0x0540:
        r4 = java.lang.Boolean.TYPE;	 Catch:{ all -> 0x0133 }
        r0 = r20;	 Catch:{ all -> 0x0133 }
        if (r0 == r4) goto L_0x051d;	 Catch:{ all -> 0x0133 }
    L_0x0546:
        r0 = r21;	 Catch:{ all -> 0x0133 }
        r1 = r50;	 Catch:{ all -> 0x0133 }
        r2 = r26;	 Catch:{ all -> 0x0133 }
        r0.setValue(r1, r2);	 Catch:{ all -> 0x0133 }
        goto L_0x051d;	 Catch:{ all -> 0x0133 }
    L_0x0550:
        r0 = r21;	 Catch:{ all -> 0x0133 }
        r1 = r50;	 Catch:{ all -> 0x0133 }
        r2 = r26;	 Catch:{ all -> 0x0133 }
        r0.setValue(r1, r2);	 Catch:{ all -> 0x0133 }
        goto L_0x051d;	 Catch:{ all -> 0x0133 }
    L_0x055a:
        r4 = r46;	 Catch:{ all -> 0x0133 }
        r5 = r47;	 Catch:{ all -> 0x0133 }
        r7 = r50;	 Catch:{ all -> 0x0133 }
        r8 = r48;	 Catch:{ all -> 0x0133 }
        r32 = r4.parseField(r5, r6, r7, r8, r9);	 Catch:{ all -> 0x0133 }
        if (r32 != 0) goto L_0x0575;	 Catch:{ all -> 0x0133 }
    L_0x0568:
        r4 = r30.token();	 Catch:{ all -> 0x0133 }
        r5 = 13;	 Catch:{ all -> 0x0133 }
        if (r4 != r5) goto L_0x01ef;	 Catch:{ all -> 0x0133 }
    L_0x0570:
        r30.nextToken();	 Catch:{ all -> 0x0133 }
        goto L_0x01c3;	 Catch:{ all -> 0x0133 }
    L_0x0575:
        r4 = r30.token();	 Catch:{ all -> 0x0133 }
        r5 = 17;	 Catch:{ all -> 0x0133 }
        if (r4 != r5) goto L_0x04f9;	 Catch:{ all -> 0x0133 }
    L_0x057d:
        r4 = new com.alibaba.fastjson.JSONException;	 Catch:{ all -> 0x0133 }
        r5 = "syntax error, unexpect token ':'";	 Catch:{ all -> 0x0133 }
        r4.<init>(r5);	 Catch:{ all -> 0x0133 }
        throw r4;	 Catch:{ all -> 0x0133 }
    L_0x0586:
        r4 = r30.token();	 Catch:{ all -> 0x0133 }
        r5 = 18;	 Catch:{ all -> 0x0133 }
        if (r4 == r5) goto L_0x0595;	 Catch:{ all -> 0x0133 }
    L_0x058e:
        r4 = r30.token();	 Catch:{ all -> 0x0133 }
        r5 = 1;	 Catch:{ all -> 0x0133 }
        if (r4 != r5) goto L_0x01ef;	 Catch:{ all -> 0x0133 }
    L_0x0595:
        r4 = new com.alibaba.fastjson.JSONException;	 Catch:{ all -> 0x0133 }
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0133 }
        r5.<init>();	 Catch:{ all -> 0x0133 }
        r7 = "syntax error, unexpect token ";	 Catch:{ all -> 0x0133 }
        r5 = r5.append(r7);	 Catch:{ all -> 0x0133 }
        r7 = r30.token();	 Catch:{ all -> 0x0133 }
        r7 = com.alibaba.fastjson.parser.JSONToken.name(r7);	 Catch:{ all -> 0x0133 }
        r5 = r5.append(r7);	 Catch:{ all -> 0x0133 }
        r5 = r5.toString();	 Catch:{ all -> 0x0133 }
        r4.<init>(r5);	 Catch:{ all -> 0x0133 }
        throw r4;	 Catch:{ all -> 0x0133 }
    L_0x05b7:
        r0 = r46;	 Catch:{ all -> 0x0133 }
        r4 = r0.beanInfo;	 Catch:{ all -> 0x0133 }
        r0 = r4.fields;	 Catch:{ all -> 0x0133 }
        r24 = r0;	 Catch:{ all -> 0x0133 }
        r0 = r24;	 Catch:{ all -> 0x0133 }
        r0 = r0.length;	 Catch:{ all -> 0x0133 }
        r40 = r0;	 Catch:{ all -> 0x0133 }
        r0 = r40;	 Catch:{ all -> 0x0133 }
        r0 = new java.lang.Object[r0];	 Catch:{ all -> 0x0133 }
        r36 = r0;	 Catch:{ all -> 0x0133 }
        r28 = 0;	 Catch:{ all -> 0x0133 }
    L_0x05cc:
        r0 = r28;	 Catch:{ all -> 0x0133 }
        r1 = r40;	 Catch:{ all -> 0x0133 }
        if (r0 >= r1) goto L_0x063b;	 Catch:{ all -> 0x0133 }
    L_0x05d2:
        r23 = r24[r28];	 Catch:{ all -> 0x0133 }
        r0 = r23;	 Catch:{ all -> 0x0133 }
        r4 = r0.name;	 Catch:{ all -> 0x0133 }
        r35 = r9.get(r4);	 Catch:{ all -> 0x0133 }
        if (r35 != 0) goto L_0x05ef;	 Catch:{ all -> 0x0133 }
    L_0x05de:
        r0 = r23;	 Catch:{ all -> 0x0133 }
        r0 = r0.fieldType;	 Catch:{ all -> 0x0133 }
        r25 = r0;	 Catch:{ all -> 0x0133 }
        r4 = java.lang.Byte.TYPE;	 Catch:{ all -> 0x0133 }
        r0 = r25;	 Catch:{ all -> 0x0133 }
        if (r0 != r4) goto L_0x05f4;	 Catch:{ all -> 0x0133 }
    L_0x05ea:
        r4 = 0;	 Catch:{ all -> 0x0133 }
        r35 = java.lang.Byte.valueOf(r4);	 Catch:{ all -> 0x0133 }
    L_0x05ef:
        r36[r28] = r35;	 Catch:{ all -> 0x0133 }
        r28 = r28 + 1;	 Catch:{ all -> 0x0133 }
        goto L_0x05cc;	 Catch:{ all -> 0x0133 }
    L_0x05f4:
        r4 = java.lang.Short.TYPE;	 Catch:{ all -> 0x0133 }
        r0 = r25;	 Catch:{ all -> 0x0133 }
        if (r0 != r4) goto L_0x0600;	 Catch:{ all -> 0x0133 }
    L_0x05fa:
        r4 = 0;	 Catch:{ all -> 0x0133 }
        r35 = java.lang.Short.valueOf(r4);	 Catch:{ all -> 0x0133 }
        goto L_0x05ef;	 Catch:{ all -> 0x0133 }
    L_0x0600:
        r4 = java.lang.Integer.TYPE;	 Catch:{ all -> 0x0133 }
        r0 = r25;	 Catch:{ all -> 0x0133 }
        if (r0 != r4) goto L_0x060c;	 Catch:{ all -> 0x0133 }
    L_0x0606:
        r4 = 0;	 Catch:{ all -> 0x0133 }
        r35 = java.lang.Integer.valueOf(r4);	 Catch:{ all -> 0x0133 }
        goto L_0x05ef;	 Catch:{ all -> 0x0133 }
    L_0x060c:
        r4 = java.lang.Long.TYPE;	 Catch:{ all -> 0x0133 }
        r0 = r25;	 Catch:{ all -> 0x0133 }
        if (r0 != r4) goto L_0x0619;	 Catch:{ all -> 0x0133 }
    L_0x0612:
        r4 = 0;	 Catch:{ all -> 0x0133 }
        r35 = java.lang.Long.valueOf(r4);	 Catch:{ all -> 0x0133 }
        goto L_0x05ef;	 Catch:{ all -> 0x0133 }
    L_0x0619:
        r4 = java.lang.Float.TYPE;	 Catch:{ all -> 0x0133 }
        r0 = r25;	 Catch:{ all -> 0x0133 }
        if (r0 != r4) goto L_0x0625;	 Catch:{ all -> 0x0133 }
    L_0x061f:
        r4 = 0;	 Catch:{ all -> 0x0133 }
        r35 = java.lang.Float.valueOf(r4);	 Catch:{ all -> 0x0133 }
        goto L_0x05ef;	 Catch:{ all -> 0x0133 }
    L_0x0625:
        r4 = java.lang.Double.TYPE;	 Catch:{ all -> 0x0133 }
        r0 = r25;	 Catch:{ all -> 0x0133 }
        if (r0 != r4) goto L_0x0632;	 Catch:{ all -> 0x0133 }
    L_0x062b:
        r4 = 0;	 Catch:{ all -> 0x0133 }
        r35 = java.lang.Double.valueOf(r4);	 Catch:{ all -> 0x0133 }
        goto L_0x05ef;	 Catch:{ all -> 0x0133 }
    L_0x0632:
        r4 = java.lang.Boolean.TYPE;	 Catch:{ all -> 0x0133 }
        r0 = r25;	 Catch:{ all -> 0x0133 }
        if (r0 != r4) goto L_0x05ef;	 Catch:{ all -> 0x0133 }
    L_0x0638:
        r35 = java.lang.Boolean.FALSE;	 Catch:{ all -> 0x0133 }
        goto L_0x05ef;	 Catch:{ all -> 0x0133 }
    L_0x063b:
        r0 = r46;	 Catch:{ all -> 0x0133 }
        r4 = r0.beanInfo;	 Catch:{ all -> 0x0133 }
        r4 = r4.creatorConstructor;	 Catch:{ all -> 0x0133 }
        if (r4 == 0) goto L_0x068c;
    L_0x0643:
        r0 = r46;	 Catch:{ Exception -> 0x0666 }
        r4 = r0.beanInfo;	 Catch:{ Exception -> 0x0666 }
        r4 = r4.creatorConstructor;	 Catch:{ Exception -> 0x0666 }
        r0 = r36;	 Catch:{ Exception -> 0x0666 }
        r50 = r4.newInstance(r0);	 Catch:{ Exception -> 0x0666 }
    L_0x064f:
        r0 = r46;	 Catch:{ all -> 0x0133 }
        r4 = r0.beanInfo;	 Catch:{ all -> 0x0133 }
        r11 = r4.buildMethod;	 Catch:{ all -> 0x0133 }
        if (r11 != 0) goto L_0x06c8;
    L_0x0657:
        if (r13 == 0) goto L_0x065d;
    L_0x0659:
        r0 = r50;
        r13.object = r0;
    L_0x065d:
        r0 = r47;
        r0.setContext(r15);
        r12 = r50;
        goto L_0x0010;
    L_0x0666:
        r17 = move-exception;
        r4 = new com.alibaba.fastjson.JSONException;	 Catch:{ all -> 0x0133 }
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0133 }
        r5.<init>();	 Catch:{ all -> 0x0133 }
        r7 = "create instance error, ";	 Catch:{ all -> 0x0133 }
        r5 = r5.append(r7);	 Catch:{ all -> 0x0133 }
        r0 = r46;	 Catch:{ all -> 0x0133 }
        r7 = r0.beanInfo;	 Catch:{ all -> 0x0133 }
        r7 = r7.creatorConstructor;	 Catch:{ all -> 0x0133 }
        r7 = r7.toGenericString();	 Catch:{ all -> 0x0133 }
        r5 = r5.append(r7);	 Catch:{ all -> 0x0133 }
        r5 = r5.toString();	 Catch:{ all -> 0x0133 }
        r0 = r17;	 Catch:{ all -> 0x0133 }
        r4.<init>(r5, r0);	 Catch:{ all -> 0x0133 }
        throw r4;	 Catch:{ all -> 0x0133 }
    L_0x068c:
        r0 = r46;	 Catch:{ all -> 0x0133 }
        r4 = r0.beanInfo;	 Catch:{ all -> 0x0133 }
        r4 = r4.factoryMethod;	 Catch:{ all -> 0x0133 }
        if (r4 == 0) goto L_0x064f;
    L_0x0694:
        r0 = r46;	 Catch:{ Exception -> 0x06a2 }
        r4 = r0.beanInfo;	 Catch:{ Exception -> 0x06a2 }
        r4 = r4.factoryMethod;	 Catch:{ Exception -> 0x06a2 }
        r5 = 0;	 Catch:{ Exception -> 0x06a2 }
        r0 = r36;	 Catch:{ Exception -> 0x06a2 }
        r50 = r4.invoke(r5, r0);	 Catch:{ Exception -> 0x06a2 }
        goto L_0x064f;
    L_0x06a2:
        r17 = move-exception;
        r4 = new com.alibaba.fastjson.JSONException;	 Catch:{ all -> 0x0133 }
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0133 }
        r5.<init>();	 Catch:{ all -> 0x0133 }
        r7 = "create factory method error, ";	 Catch:{ all -> 0x0133 }
        r5 = r5.append(r7);	 Catch:{ all -> 0x0133 }
        r0 = r46;	 Catch:{ all -> 0x0133 }
        r7 = r0.beanInfo;	 Catch:{ all -> 0x0133 }
        r7 = r7.factoryMethod;	 Catch:{ all -> 0x0133 }
        r7 = r7.toString();	 Catch:{ all -> 0x0133 }
        r5 = r5.append(r7);	 Catch:{ all -> 0x0133 }
        r5 = r5.toString();	 Catch:{ all -> 0x0133 }
        r0 = r17;	 Catch:{ all -> 0x0133 }
        r4.<init>(r5, r0);	 Catch:{ all -> 0x0133 }
        throw r4;	 Catch:{ all -> 0x0133 }
    L_0x06c8:
        r4 = 0;
        r4 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x06de }
        r0 = r50;	 Catch:{ Exception -> 0x06de }
        r12 = r11.invoke(r0, r4);	 Catch:{ Exception -> 0x06de }
        if (r13 == 0) goto L_0x06d7;
    L_0x06d3:
        r0 = r50;
        r13.object = r0;
    L_0x06d7:
        r0 = r47;
        r0.setContext(r15);
        goto L_0x0010;
    L_0x06de:
        r17 = move-exception;
        r4 = new com.alibaba.fastjson.JSONException;	 Catch:{ all -> 0x0133 }
        r5 = "build object error";	 Catch:{ all -> 0x0133 }
        r0 = r17;	 Catch:{ all -> 0x0133 }
        r4.<init>(r5, r0);	 Catch:{ all -> 0x0133 }
        throw r4;	 Catch:{ all -> 0x0133 }
    L_0x06e9:
        r9 = r27;
        goto L_0x04e0;
    L_0x06ed:
        r9 = r27;
        goto L_0x04ea;
    L_0x06f1:
        r9 = r27;
        goto L_0x01ef;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer.deserialze(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.reflect.Type, java.lang.Object, java.lang.Object, int):T");
    }

    public boolean parseField(DefaultJSONParser parser, String key, Object object, Type objectType, Map<String, Object> fieldValues) {
        JSONLexer lexer = parser.lexer;
        FieldDeserializer fieldDeserializer = smartMatch(key);
        int mask = Feature.SupportNonPublicField.mask;
        if (fieldDeserializer == null && (parser.lexer.isEnabled(mask) || (this.beanInfo.parserFeatures & mask) != 0)) {
            Field field;
            if (this.extraFieldDeserializers == null) {
                ConcurrentHashMap extraFieldDeserializers = new ConcurrentHashMap(1, AdaptiveVideoTrackSelection.DEFAULT_BANDWIDTH_FRACTION, 1);
                for (Field field2 : this.clazz.getDeclaredFields()) {
                    String fieldName = field2.getName();
                    if (getFieldDeserializer(fieldName) == null) {
                        int fieldModifiers = field2.getModifiers();
                        if ((fieldModifiers & 16) == 0 && (fieldModifiers & 8) == 0) {
                            extraFieldDeserializers.put(fieldName, field2);
                        }
                    }
                }
                this.extraFieldDeserializers = extraFieldDeserializers;
            }
            FieldDeserializer deserOrField = this.extraFieldDeserializers.get(key);
            if (deserOrField != null) {
                if (deserOrField instanceof FieldDeserializer) {
                    fieldDeserializer = deserOrField;
                } else {
                    field2 = (Field) deserOrField;
                    field2.setAccessible(true);
                    fieldDeserializer = new DefaultFieldDeserializer(parser.getConfig(), this.clazz, new FieldInfo(key, field2.getDeclaringClass(), field2.getType(), field2.getGenericType(), field2, 0, 0, 0));
                    this.extraFieldDeserializers.put(key, fieldDeserializer);
                }
            }
        }
        if (fieldDeserializer == null) {
            if (lexer.isEnabled(Feature.IgnoreNotMatch)) {
                parser.parseExtra(object, key);
                return false;
            }
            throw new JSONException("setter not found, class " + this.clazz.getName() + ", property " + key);
        }
        lexer.nextTokenWithColon(fieldDeserializer.getFastMatchToken());
        fieldDeserializer.parseField(parser, object, objectType, fieldValues);
        return true;
    }

    public FieldDeserializer smartMatch(String key) {
        if (key == null) {
            return null;
        }
        int length;
        int i;
        FieldDeserializer fieldDeserializer = getFieldDeserializer(key);
        FieldDeserializer fieldDeser;
        if (fieldDeserializer == null) {
            boolean startsWithIs = key.startsWith("is");
            FieldDeserializer[] fieldDeserializerArr = this.sortedFieldDeserializers;
            length = fieldDeserializerArr.length;
            i = 0;
            while (i < length) {
                fieldDeser = fieldDeserializerArr[i];
                FieldInfo fieldInfo = fieldDeser.fieldInfo;
                Class<?> fieldClass = fieldInfo.fieldClass;
                String fieldName = fieldInfo.name;
                if (!fieldName.equalsIgnoreCase(key)) {
                    if (startsWithIs && ((fieldClass == Boolean.TYPE || fieldClass == Boolean.class) && fieldName.equalsIgnoreCase(key.substring(2)))) {
                        fieldDeserializer = fieldDeser;
                        break;
                    }
                    i++;
                } else {
                    fieldDeserializer = fieldDeser;
                    break;
                }
            }
        }
        if (fieldDeserializer == null) {
            boolean snakeOrkebab = false;
            String key2 = null;
            int i2 = 0;
            while (i2 < key.length()) {
                char ch = key.charAt(i2);
                if (ch == '_') {
                    snakeOrkebab = true;
                    key2 = key.replaceAll("_", "");
                    break;
                } else if (ch == '-') {
                    snakeOrkebab = true;
                    key2 = key.replaceAll(Constants.ACCEPT_TIME_SEPARATOR_SERVER, "");
                    break;
                } else {
                    i2++;
                }
            }
            if (snakeOrkebab) {
                fieldDeserializer = getFieldDeserializer(key2);
                if (fieldDeserializer == null) {
                    for (FieldDeserializer fieldDeser2 : this.sortedFieldDeserializers) {
                        if (fieldDeser2.fieldInfo.name.equalsIgnoreCase(key2)) {
                            fieldDeserializer = fieldDeser2;
                            break;
                        }
                    }
                }
            }
        }
        if (fieldDeserializer != null) {
            return fieldDeserializer;
        }
        for (FieldDeserializer fieldDeser22 : this.sortedFieldDeserializers) {
            if (fieldDeser22.fieldInfo.alternateName(key)) {
                return fieldDeser22;
            }
        }
        return fieldDeserializer;
    }

    public int getFastMatchToken() {
        return 12;
    }

    public Object createInstance(Map<String, Object> map, ParserConfig config) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Object object = null;
        if (this.beanInfo.creatorConstructor == null && this.beanInfo.buildMethod == null && this.beanInfo.factoryMethod == null) {
            object = createInstance(null, (Type) this.clazz);
            for (Entry<String, Object> entry : map.entrySet()) {
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                FieldDeserializer fieldDeser = smartMatch(key);
                if (fieldDeser != null) {
                    Method method = fieldDeser.fieldInfo.method;
                    if (method != null) {
                        value = TypeUtils.cast(value, method.getGenericParameterTypes()[0], config);
                        method.invoke(object, new Object[]{value});
                    } else {
                        fieldDeser.fieldInfo.field.set(object, TypeUtils.cast(value, fieldDeser.fieldInfo.fieldType, config));
                    }
                }
            }
        } else {
            FieldInfo[] fieldInfoList = this.beanInfo.fields;
            int size = fieldInfoList.length;
            Object[] params = new Object[size];
            for (int i = 0; i < size; i++) {
                params[i] = map.get(fieldInfoList[i].name);
            }
            if (this.beanInfo.creatorConstructor != null) {
                try {
                    object = this.beanInfo.creatorConstructor.newInstance(params);
                } catch (Exception e) {
                    throw new JSONException("create instance error, " + this.beanInfo.creatorConstructor.toGenericString(), e);
                }
            } else if (this.beanInfo.factoryMethod != null) {
                try {
                    object = this.beanInfo.factoryMethod.invoke(null, params);
                } catch (Exception e2) {
                    throw new JSONException("create factory method error, " + this.beanInfo.factoryMethod.toString(), e2);
                }
            }
        }
        return object;
    }

    public Type getFieldType(int ordinal) {
        return this.sortedFieldDeserializers[ordinal].fieldInfo.fieldType;
    }

    protected Object parseRest(DefaultJSONParser parser, Type type, Object fieldName, Object instance, int features) {
        return deserialze(parser, type, fieldName, instance, features);
    }

    protected JavaBeanDeserializer getSeeAlso(ParserConfig config, JavaBeanInfo beanInfo, String typeName) {
        if (beanInfo.jsonType == null) {
            return null;
        }
        for (Type seeAlsoClass : beanInfo.jsonType.seeAlso()) {
            ObjectDeserializer seeAlsoDeser = config.getDeserializer(seeAlsoClass);
            if (seeAlsoDeser instanceof JavaBeanDeserializer) {
                JavaBeanDeserializer seeAlsoJavaBeanDeser = (JavaBeanDeserializer) seeAlsoDeser;
                JavaBeanInfo subBeanInfo = seeAlsoJavaBeanDeser.beanInfo;
                if (subBeanInfo.typeName.equals(typeName)) {
                    return seeAlsoJavaBeanDeser;
                }
                JavaBeanDeserializer subSeeAlso = getSeeAlso(config, subBeanInfo, typeName);
                if (subSeeAlso != null) {
                    return subSeeAlso;
                }
            }
        }
        return null;
    }

    protected static void parseArray(Collection collection, ObjectDeserializer deser, DefaultJSONParser parser, Type type, Object fieldName) {
        JSONLexerBase lexer = parser.lexer;
        int token = lexer.token();
        if (token == 8) {
            lexer.nextToken(16);
            token = lexer.token();
            return;
        }
        if (token != 14) {
            parser.throwException(token);
        }
        if (lexer.getCurrent() == '[') {
            lexer.next();
            lexer.setToken(14);
        } else {
            lexer.nextToken(14);
        }
        if (lexer.token() == 15) {
            lexer.nextToken();
            return;
        }
        int index = 0;
        while (true) {
            collection.add(deser.deserialze(parser, type, Integer.valueOf(index)));
            index++;
            if (lexer.token() != 16) {
                break;
            } else if (lexer.getCurrent() == '[') {
                lexer.next();
                lexer.setToken(14);
            } else {
                lexer.nextToken(14);
            }
        }
        token = lexer.token();
        if (token != 15) {
            parser.throwException(token);
        }
        if (lexer.getCurrent() == ',') {
            lexer.next();
            lexer.setToken(16);
            return;
        }
        lexer.nextToken(16);
    }
}
