package com.alibaba.fastjson.parser.deserializer;

import cn.finalteam.toolsfinal.io.FilenameUtils;
import cn.finalteam.toolsfinal.io.IOUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.serializer.BeanContext;
import com.alibaba.fastjson.serializer.ContextObjectSerializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

public class Jdk8DateCodec extends ContextObjectDeserializer implements ObjectSerializer, ContextObjectSerializer, ObjectDeserializer {
    private static final DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String defaultPatttern = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter formatter_d10_cn = DateTimeFormatter.ofPattern("yyyy年M月d日");
    private static final DateTimeFormatter formatter_d10_de = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter formatter_d10_eur = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter formatter_d10_in = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter formatter_d10_kr = DateTimeFormatter.ofPattern("yyyy년M월d일");
    private static final DateTimeFormatter formatter_d10_tw = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter formatter_d10_us = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter formatter_d8 = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter formatter_dt19_cn = DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm:ss");
    private static final DateTimeFormatter formatter_dt19_cn_1 = DateTimeFormatter.ofPattern("yyyy年M月d日 H时m分s秒");
    private static final DateTimeFormatter formatter_dt19_de = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final DateTimeFormatter formatter_dt19_eur = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter formatter_dt19_in = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final DateTimeFormatter formatter_dt19_kr = DateTimeFormatter.ofPattern("yyyy년M월d일 HH:mm:ss");
    private static final DateTimeFormatter formatter_dt19_tw = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private static final DateTimeFormatter formatter_dt19_us = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    public static final Jdk8DateCodec instance = new Jdk8DateCodec();

    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName, String format, int feature) {
        JSONLexer lexer = parser.lexer;
        if (lexer.token() == 4) {
            String text = lexer.stringVal();
            lexer.nextToken();
            DateTimeFormatter formatter = null;
            if (format != null) {
                formatter = DateTimeFormatter.ofPattern(format);
            }
            if (type == LocalDateTime.class) {
                T localDateTime;
                if (text.length() == 10 || text.length() == 8) {
                    localDateTime = LocalDateTime.of(parseLocalDate(text, format, formatter), LocalTime.MIN);
                } else {
                    localDateTime = parseDateTime(text, formatter);
                }
                return localDateTime;
            } else if (type == LocalDate.class) {
                if (text.length() != 23) {
                    return parseLocalDate(text, format, formatter);
                }
                localDateTime = LocalDateTime.parse(text);
                return LocalDate.of(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth());
            } else if (type == LocalTime.class) {
                if (text.length() != 23) {
                    return LocalTime.parse(text);
                }
                localDateTime = LocalDateTime.parse(text);
                return LocalTime.of(localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond(), localDateTime.getNano());
            } else if (type == ZonedDateTime.class) {
                return ZonedDateTime.parse(text);
            } else {
                if (type == OffsetDateTime.class) {
                    return OffsetDateTime.parse(text);
                }
                if (type == OffsetTime.class) {
                    return OffsetTime.parse(text);
                }
                if (type == ZoneId.class) {
                    return ZoneId.of(text);
                }
                if (type == Period.class) {
                    return Period.parse(text);
                }
                if (type == Duration.class) {
                    return Duration.parse(text);
                }
                if (type == Instant.class) {
                    return Instant.parse(text);
                }
                return null;
            }
        }
        throw new UnsupportedOperationException();
    }

    protected LocalDateTime parseDateTime(String text, DateTimeFormatter formatter) {
        if (formatter == null) {
            char c4;
            if (text.length() == 19) {
                c4 = text.charAt(4);
                char c7 = text.charAt(7);
                char c10 = text.charAt(10);
                char c13 = text.charAt(13);
                char c16 = text.charAt(16);
                if (c13 == ':' && c16 == ':') {
                    if (c4 == '-' && c7 == '-') {
                        if (c10 == 'T') {
                            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                        } else if (c10 == ' ') {
                            formatter = defaultFormatter;
                        }
                    } else if (c4 == '-' && c7 == '-') {
                        formatter = defaultFormatter;
                    } else if (c4 == IOUtils.DIR_SEPARATOR_UNIX && c7 == IOUtils.DIR_SEPARATOR_UNIX) {
                        formatter = formatter_dt19_tw;
                    } else {
                        char c0 = text.charAt(0);
                        char c1 = text.charAt(1);
                        char c2 = text.charAt(2);
                        char c3 = text.charAt(3);
                        char c5 = text.charAt(5);
                        if (c2 == IOUtils.DIR_SEPARATOR_UNIX && c5 == IOUtils.DIR_SEPARATOR_UNIX) {
                            int v1 = ((c3 - 48) * 10) + (c4 - 48);
                            if (((c0 - 48) * 10) + (c1 - 48) > 12) {
                                formatter = formatter_dt19_eur;
                            } else if (v1 > 12) {
                                formatter = formatter_dt19_us;
                            } else {
                                String country = Locale.getDefault().getCountry();
                                if (country.equals("US")) {
                                    formatter = formatter_dt19_us;
                                } else if (country.equals("BR") || country.equals("AU")) {
                                    formatter = formatter_dt19_eur;
                                }
                            }
                        } else if (c2 == FilenameUtils.EXTENSION_SEPARATOR && c5 == FilenameUtils.EXTENSION_SEPARATOR) {
                            formatter = formatter_dt19_de;
                        } else if (c2 == '-' && c5 == '-') {
                            formatter = formatter_dt19_in;
                        }
                    }
                }
            }
            if (text.length() >= 17) {
                c4 = text.charAt(4);
                if (c4 == '年') {
                    if (text.charAt(text.length() - 1) == '秒') {
                        formatter = formatter_dt19_cn_1;
                    } else {
                        formatter = formatter_dt19_cn;
                    }
                } else if (c4 == '년') {
                    formatter = formatter_dt19_kr;
                }
            }
        }
        if (formatter == null) {
            return LocalDateTime.parse(text);
        }
        return LocalDateTime.parse(text, formatter);
    }

    protected LocalDate parseLocalDate(String text, String format, DateTimeFormatter formatter) {
        if (formatter == null) {
            char c4;
            if (text.length() == 8) {
                formatter = formatter_d8;
            }
            if (text.length() == 10) {
                c4 = text.charAt(4);
                char c7 = text.charAt(7);
                if (c4 == IOUtils.DIR_SEPARATOR_UNIX && c7 == IOUtils.DIR_SEPARATOR_UNIX) {
                    formatter = formatter_d10_tw;
                }
                char c0 = text.charAt(0);
                char c1 = text.charAt(1);
                char c2 = text.charAt(2);
                char c3 = text.charAt(3);
                char c5 = text.charAt(5);
                if (c2 == IOUtils.DIR_SEPARATOR_UNIX && c5 == IOUtils.DIR_SEPARATOR_UNIX) {
                    int v1 = ((c3 - 48) * 10) + (c4 - 48);
                    if (((c0 - 48) * 10) + (c1 - 48) > 12) {
                        formatter = formatter_d10_eur;
                    } else if (v1 > 12) {
                        formatter = formatter_d10_us;
                    } else {
                        String country = Locale.getDefault().getCountry();
                        if (country.equals("US")) {
                            formatter = formatter_d10_us;
                        } else if (country.equals("BR") || country.equals("AU")) {
                            formatter = formatter_d10_eur;
                        }
                    }
                } else if (c2 == FilenameUtils.EXTENSION_SEPARATOR && c5 == FilenameUtils.EXTENSION_SEPARATOR) {
                    formatter = formatter_d10_de;
                } else if (c2 == '-' && c5 == '-') {
                    formatter = formatter_d10_in;
                }
            }
            if (text.length() >= 9) {
                c4 = text.charAt(4);
                if (c4 == '年') {
                    formatter = formatter_d10_cn;
                } else if (c4 == '년') {
                    formatter = formatter_d10_kr;
                }
            }
        }
        if (formatter == null) {
            return LocalDate.parse(text);
        }
        return LocalDate.parse(text, formatter);
    }

    public int getFastMatchToken() {
        return 4;
    }

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
        } else if (fieldType == LocalDateTime.class) {
            TemporalAccessor dateTime = (LocalDateTime) object;
            if (dateTime.getNano() == 0) {
                String format = serializer.getDateFormatPattern();
                if (format == null) {
                    format = JSON.DEFFAULT_DATE_FORMAT;
                }
                write(out, dateTime, format);
                return;
            }
            out.writeString(object.toString());
        } else {
            out.writeString(object.toString());
        }
    }

    public void write(JSONSerializer serializer, Object object, BeanContext context) throws IOException {
        write(serializer.out, (TemporalAccessor) object, context.getFormat());
    }

    private void write(SerializeWriter out, TemporalAccessor object, String format) {
        out.writeString(DateTimeFormatter.ofPattern(format).format(object));
    }
}
