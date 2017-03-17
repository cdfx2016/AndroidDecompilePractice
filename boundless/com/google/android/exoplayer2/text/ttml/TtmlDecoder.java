package com.google.android.exoplayer2.text.ttml;

import android.util.Log;
import android.util.Pair;
import com.easemob.util.HanziToPinyin.Token;
import com.fanyu.boundless.util.FileUtil;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.util.XmlPullParserUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public final class TtmlDecoder extends SimpleSubtitleDecoder {
    private static final String ATTR_BEGIN = "begin";
    private static final String ATTR_DURATION = "dur";
    private static final String ATTR_END = "end";
    private static final String ATTR_REGION = "region";
    private static final String ATTR_STYLE = "style";
    private static final Pattern CLOCK_TIME = Pattern.compile("^([0-9][0-9]+):([0-9][0-9]):([0-9][0-9])(?:(\\.[0-9]+)|:([0-9][0-9])(?:\\.([0-9]+))?)?$");
    private static final FrameAndTickRate DEFAULT_FRAME_AND_TICK_RATE = new FrameAndTickRate(30.0f, 1, 1);
    private static final int DEFAULT_FRAME_RATE = 30;
    private static final Pattern FONT_SIZE = Pattern.compile("^(([0-9]*.)?[0-9]+)(px|em|%)$");
    private static final Pattern OFFSET_TIME = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)(h|m|s|ms|f|t)$");
    private static final Pattern PERCENTAGE_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)% (\\d+\\.?\\d*?)%$");
    private static final String TAG = "TtmlDecoder";
    private static final String TTP = "http://www.w3.org/ns/ttml#parameter";
    private final XmlPullParserFactory xmlParserFactory;

    private static final class FrameAndTickRate {
        final float effectiveFrameRate;
        final int subFrameRate;
        final int tickRate;

        FrameAndTickRate(float effectiveFrameRate, int subFrameRate, int tickRate) {
            this.effectiveFrameRate = effectiveFrameRate;
            this.subFrameRate = subFrameRate;
            this.tickRate = tickRate;
        }
    }

    public TtmlDecoder() {
        super(TAG);
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
            this.xmlParserFactory.setNamespaceAware(true);
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    protected TtmlSubtitle decode(byte[] bytes, int length) throws SubtitleDecoderException {
        try {
            XmlPullParser xmlParser = this.xmlParserFactory.newPullParser();
            Map<String, TtmlStyle> globalStyles = new HashMap();
            Map<String, TtmlRegion> regionMap = new HashMap();
            regionMap.put("", new TtmlRegion());
            xmlParser.setInput(new ByteArrayInputStream(bytes, 0, length), null);
            TtmlSubtitle ttmlSubtitle = null;
            LinkedList<TtmlNode> nodeStack = new LinkedList();
            int unsupportedNodeDepth = 0;
            FrameAndTickRate frameAndTickRate = DEFAULT_FRAME_AND_TICK_RATE;
            for (int eventType = xmlParser.getEventType(); eventType != 1; eventType = xmlParser.getEventType()) {
                TtmlNode parent = (TtmlNode) nodeStack.peekLast();
                if (unsupportedNodeDepth == 0) {
                    String name = xmlParser.getName();
                    if (eventType == 2) {
                        if (TtmlNode.TAG_TT.equals(name)) {
                            frameAndTickRate = parseFrameAndTickRates(xmlParser);
                        }
                        if (!isSupportedTag(name)) {
                            Log.i(TAG, "Ignoring unsupported tag: " + xmlParser.getName());
                            unsupportedNodeDepth++;
                        } else if (TtmlNode.TAG_HEAD.equals(name)) {
                            parseHeader(xmlParser, globalStyles, regionMap);
                        } else {
                            try {
                                TtmlNode node = parseNode(xmlParser, parent, regionMap, frameAndTickRate);
                                nodeStack.addLast(node);
                                if (parent != null) {
                                    parent.addChild(node);
                                }
                            } catch (SubtitleDecoderException e) {
                                Log.w(TAG, "Suppressing parser error", e);
                                unsupportedNodeDepth++;
                            }
                        }
                    } else if (eventType == 4) {
                        parent.addChild(TtmlNode.buildTextNode(xmlParser.getText()));
                    } else if (eventType == 3) {
                        if (xmlParser.getName().equals(TtmlNode.TAG_TT)) {
                            ttmlSubtitle = new TtmlSubtitle((TtmlNode) nodeStack.getLast(), globalStyles, regionMap);
                        }
                        nodeStack.removeLast();
                    } else {
                        continue;
                    }
                } else if (eventType == 2) {
                    unsupportedNodeDepth++;
                } else if (eventType == 3) {
                    unsupportedNodeDepth--;
                }
                xmlParser.next();
            }
            return ttmlSubtitle;
        } catch (Throwable xppe) {
            throw new SubtitleDecoderException("Unable to decode source", xppe);
        } catch (IOException e2) {
            throw new IllegalStateException("Unexpected error when reading input.", e2);
        }
    }

    private FrameAndTickRate parseFrameAndTickRates(XmlPullParser xmlParser) throws SubtitleDecoderException {
        int frameRate = 30;
        String frameRateString = xmlParser.getAttributeValue(TTP, "frameRate");
        if (frameRateString != null) {
            frameRate = Integer.parseInt(frameRateString);
        }
        float frameRateMultiplier = 1.0f;
        String frameRateMultiplierString = xmlParser.getAttributeValue(TTP, "frameRateMultiplier");
        if (frameRateMultiplierString != null) {
            String[] parts = frameRateMultiplierString.split(Token.SEPARATOR);
            if (parts.length != 2) {
                throw new SubtitleDecoderException("frameRateMultiplier doesn't have 2 parts");
            }
            frameRateMultiplier = ((float) Integer.parseInt(parts[0])) / ((float) Integer.parseInt(parts[1]));
        }
        int subFrameRate = DEFAULT_FRAME_AND_TICK_RATE.subFrameRate;
        String subFrameRateString = xmlParser.getAttributeValue(TTP, "subFrameRate");
        if (subFrameRateString != null) {
            subFrameRate = Integer.parseInt(subFrameRateString);
        }
        int tickRate = DEFAULT_FRAME_AND_TICK_RATE.tickRate;
        String tickRateString = xmlParser.getAttributeValue(TTP, "tickRate");
        if (tickRateString != null) {
            tickRate = Integer.parseInt(tickRateString);
        }
        return new FrameAndTickRate(((float) frameRate) * frameRateMultiplier, subFrameRate, tickRate);
    }

    private Map<String, TtmlStyle> parseHeader(XmlPullParser xmlParser, Map<String, TtmlStyle> globalStyles, Map<String, TtmlRegion> globalRegions) throws IOException, XmlPullParserException {
        do {
            xmlParser.next();
            if (XmlPullParserUtil.isStartTag(xmlParser, "style")) {
                String parentStyleId = XmlPullParserUtil.getAttributeValue(xmlParser, "style");
                TtmlStyle style = parseStyleAttributes(xmlParser, new TtmlStyle());
                if (parentStyleId != null) {
                    for (String id : parseStyleIds(parentStyleId)) {
                        style.chain((TtmlStyle) globalStyles.get(id));
                    }
                }
                if (style.getId() != null) {
                    globalStyles.put(style.getId(), style);
                }
            } else if (XmlPullParserUtil.isStartTag(xmlParser, "region")) {
                Pair<String, TtmlRegion> ttmlRegionInfo = parseRegionAttributes(xmlParser);
                if (ttmlRegionInfo != null) {
                    globalRegions.put(ttmlRegionInfo.first, ttmlRegionInfo.second);
                }
            }
        } while (!XmlPullParserUtil.isEndTag(xmlParser, TtmlNode.TAG_HEAD));
        return globalStyles;
    }

    private Pair<String, TtmlRegion> parseRegionAttributes(XmlPullParser xmlParser) {
        String regionId = XmlPullParserUtil.getAttributeValue(xmlParser, "id");
        String regionOrigin = XmlPullParserUtil.getAttributeValue(xmlParser, TtmlNode.ATTR_TTS_ORIGIN);
        String regionExtent = XmlPullParserUtil.getAttributeValue(xmlParser, TtmlNode.ATTR_TTS_EXTENT);
        if (regionOrigin == null || regionId == null) {
            return null;
        }
        float position = Cue.DIMEN_UNSET;
        float line = Cue.DIMEN_UNSET;
        Matcher originMatcher = PERCENTAGE_COORDINATES.matcher(regionOrigin);
        if (originMatcher.matches()) {
            try {
                position = Float.parseFloat(originMatcher.group(1)) / 100.0f;
                line = Float.parseFloat(originMatcher.group(2)) / 100.0f;
            } catch (NumberFormatException e) {
                Log.w(TAG, "Ignoring region with malformed origin: '" + regionOrigin + "'", e);
                position = Cue.DIMEN_UNSET;
            }
        }
        float width = Cue.DIMEN_UNSET;
        if (regionExtent != null) {
            Matcher extentMatcher = PERCENTAGE_COORDINATES.matcher(regionExtent);
            if (extentMatcher.matches()) {
                try {
                    width = Float.parseFloat(extentMatcher.group(1)) / 100.0f;
                } catch (NumberFormatException e2) {
                    Log.w(TAG, "Ignoring malformed region extent: '" + regionExtent + "'", e2);
                }
            }
        }
        if (position != Cue.DIMEN_UNSET) {
            return new Pair(regionId, new TtmlRegion(position, line, 0, width));
        }
        return null;
    }

    private String[] parseStyleIds(String parentStyleIds) {
        return parentStyleIds.split("\\s+");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.google.android.exoplayer2.text.ttml.TtmlStyle parseStyleAttributes(org.xmlpull.v1.XmlPullParser r13, com.google.android.exoplayer2.text.ttml.TtmlStyle r14) {
        /*
        r12 = this;
        r9 = 3;
        r8 = 2;
        r6 = -1;
        r7 = 1;
        r5 = 0;
        r0 = r13.getAttributeCount();
        r3 = 0;
    L_0x000a:
        if (r3 >= r0) goto L_0x0226;
    L_0x000c:
        r1 = r13.getAttributeValue(r3);
        r4 = r13.getAttributeName(r3);
        r10 = r4.hashCode();
        switch(r10) {
            case -1550943582: goto L_0x005e;
            case -1224696685: goto L_0x0040;
            case -1065511464: goto L_0x0068;
            case -879295043: goto L_0x0073;
            case -734428249: goto L_0x0054;
            case 3355: goto L_0x0022;
            case 94842723: goto L_0x0036;
            case 365601008: goto L_0x004a;
            case 1287124693: goto L_0x002c;
            default: goto L_0x001b;
        };
    L_0x001b:
        r4 = r6;
    L_0x001c:
        switch(r4) {
            case 0: goto L_0x007f;
            case 1: goto L_0x0095;
            case 2: goto L_0x00c3;
            case 3: goto L_0x00f1;
            case 4: goto L_0x00fb;
            case 5: goto L_0x0125;
            case 6: goto L_0x0135;
            case 7: goto L_0x0145;
            case 8: goto L_0x01c4;
            default: goto L_0x001f;
        };
    L_0x001f:
        r3 = r3 + 1;
        goto L_0x000a;
    L_0x0022:
        r10 = "id";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x002a:
        r4 = r5;
        goto L_0x001c;
    L_0x002c:
        r10 = "backgroundColor";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x0034:
        r4 = r7;
        goto L_0x001c;
    L_0x0036:
        r10 = "color";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x003e:
        r4 = r8;
        goto L_0x001c;
    L_0x0040:
        r10 = "fontFamily";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x0048:
        r4 = r9;
        goto L_0x001c;
    L_0x004a:
        r10 = "fontSize";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x0052:
        r4 = 4;
        goto L_0x001c;
    L_0x0054:
        r10 = "fontWeight";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x005c:
        r4 = 5;
        goto L_0x001c;
    L_0x005e:
        r10 = "fontStyle";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x0066:
        r4 = 6;
        goto L_0x001c;
    L_0x0068:
        r10 = "textAlign";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x0071:
        r4 = 7;
        goto L_0x001c;
    L_0x0073:
        r10 = "textDecoration";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x007c:
        r4 = 8;
        goto L_0x001c;
    L_0x007f:
        r4 = "style";
        r10 = r13.getName();
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001f;
    L_0x008c:
        r4 = r12.createIfNull(r14);
        r14 = r4.setId(r1);
        goto L_0x001f;
    L_0x0095:
        r14 = r12.createIfNull(r14);
        r4 = com.google.android.exoplayer2.util.ColorParser.parseTtmlColor(r1);	 Catch:{ IllegalArgumentException -> 0x00a2 }
        r14.setBackgroundColor(r4);	 Catch:{ IllegalArgumentException -> 0x00a2 }
        goto L_0x001f;
    L_0x00a2:
        r2 = move-exception;
        r4 = "TtmlDecoder";
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "failed parsing background value: '";
        r10 = r10.append(r11);
        r10 = r10.append(r1);
        r11 = "'";
        r10 = r10.append(r11);
        r10 = r10.toString();
        android.util.Log.w(r4, r10);
        goto L_0x001f;
    L_0x00c3:
        r14 = r12.createIfNull(r14);
        r4 = com.google.android.exoplayer2.util.ColorParser.parseTtmlColor(r1);	 Catch:{ IllegalArgumentException -> 0x00d0 }
        r14.setFontColor(r4);	 Catch:{ IllegalArgumentException -> 0x00d0 }
        goto L_0x001f;
    L_0x00d0:
        r2 = move-exception;
        r4 = "TtmlDecoder";
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "failed parsing color value: '";
        r10 = r10.append(r11);
        r10 = r10.append(r1);
        r11 = "'";
        r10 = r10.append(r11);
        r10 = r10.toString();
        android.util.Log.w(r4, r10);
        goto L_0x001f;
    L_0x00f1:
        r4 = r12.createIfNull(r14);
        r14 = r4.setFontFamily(r1);
        goto L_0x001f;
    L_0x00fb:
        r14 = r12.createIfNull(r14);	 Catch:{ SubtitleDecoderException -> 0x0104 }
        parseFontSize(r1, r14);	 Catch:{ SubtitleDecoderException -> 0x0104 }
        goto L_0x001f;
    L_0x0104:
        r2 = move-exception;
        r4 = "TtmlDecoder";
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "failed parsing fontSize value: '";
        r10 = r10.append(r11);
        r10 = r10.append(r1);
        r11 = "'";
        r10 = r10.append(r11);
        r10 = r10.toString();
        android.util.Log.w(r4, r10);
        goto L_0x001f;
    L_0x0125:
        r4 = r12.createIfNull(r14);
        r10 = "bold";
        r10 = r10.equalsIgnoreCase(r1);
        r14 = r4.setBold(r10);
        goto L_0x001f;
    L_0x0135:
        r4 = r12.createIfNull(r14);
        r10 = "italic";
        r10 = r10.equalsIgnoreCase(r1);
        r14 = r4.setItalic(r10);
        goto L_0x001f;
    L_0x0145:
        r4 = com.google.android.exoplayer2.util.Util.toLowerInvariant(r1);
        r10 = r4.hashCode();
        switch(r10) {
            case -1364013995: goto L_0x018a;
            case 100571: goto L_0x0180;
            case 3317767: goto L_0x0162;
            case 108511772: goto L_0x0176;
            case 109757538: goto L_0x016c;
            default: goto L_0x0150;
        };
    L_0x0150:
        r4 = r6;
    L_0x0151:
        switch(r4) {
            case 0: goto L_0x0156;
            case 1: goto L_0x0194;
            case 2: goto L_0x01a0;
            case 3: goto L_0x01ac;
            case 4: goto L_0x01b8;
            default: goto L_0x0154;
        };
    L_0x0154:
        goto L_0x001f;
    L_0x0156:
        r4 = r12.createIfNull(r14);
        r10 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r14 = r4.setTextAlign(r10);
        goto L_0x001f;
    L_0x0162:
        r10 = "left";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x0150;
    L_0x016a:
        r4 = r5;
        goto L_0x0151;
    L_0x016c:
        r10 = "start";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x0150;
    L_0x0174:
        r4 = r7;
        goto L_0x0151;
    L_0x0176:
        r10 = "right";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x0150;
    L_0x017e:
        r4 = r8;
        goto L_0x0151;
    L_0x0180:
        r10 = "end";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x0150;
    L_0x0188:
        r4 = r9;
        goto L_0x0151;
    L_0x018a:
        r10 = "center";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x0150;
    L_0x0192:
        r4 = 4;
        goto L_0x0151;
    L_0x0194:
        r4 = r12.createIfNull(r14);
        r10 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r14 = r4.setTextAlign(r10);
        goto L_0x001f;
    L_0x01a0:
        r4 = r12.createIfNull(r14);
        r10 = android.text.Layout.Alignment.ALIGN_OPPOSITE;
        r14 = r4.setTextAlign(r10);
        goto L_0x001f;
    L_0x01ac:
        r4 = r12.createIfNull(r14);
        r10 = android.text.Layout.Alignment.ALIGN_OPPOSITE;
        r14 = r4.setTextAlign(r10);
        goto L_0x001f;
    L_0x01b8:
        r4 = r12.createIfNull(r14);
        r10 = android.text.Layout.Alignment.ALIGN_CENTER;
        r14 = r4.setTextAlign(r10);
        goto L_0x001f;
    L_0x01c4:
        r4 = com.google.android.exoplayer2.util.Util.toLowerInvariant(r1);
        r10 = r4.hashCode();
        switch(r10) {
            case -1461280213: goto L_0x01fe;
            case -1026963764: goto L_0x01f3;
            case 913457136: goto L_0x01e9;
            case 1679736913: goto L_0x01df;
            default: goto L_0x01cf;
        };
    L_0x01cf:
        r4 = r6;
    L_0x01d0:
        switch(r4) {
            case 0: goto L_0x01d5;
            case 1: goto L_0x0208;
            case 2: goto L_0x0212;
            case 3: goto L_0x021c;
            default: goto L_0x01d3;
        };
    L_0x01d3:
        goto L_0x001f;
    L_0x01d5:
        r4 = r12.createIfNull(r14);
        r14 = r4.setLinethrough(r7);
        goto L_0x001f;
    L_0x01df:
        r10 = "linethrough";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x01cf;
    L_0x01e7:
        r4 = r5;
        goto L_0x01d0;
    L_0x01e9:
        r10 = "nolinethrough";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x01cf;
    L_0x01f1:
        r4 = r7;
        goto L_0x01d0;
    L_0x01f3:
        r10 = "underline";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x01cf;
    L_0x01fc:
        r4 = r8;
        goto L_0x01d0;
    L_0x01fe:
        r10 = "nounderline";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x01cf;
    L_0x0206:
        r4 = r9;
        goto L_0x01d0;
    L_0x0208:
        r4 = r12.createIfNull(r14);
        r14 = r4.setLinethrough(r5);
        goto L_0x001f;
    L_0x0212:
        r4 = r12.createIfNull(r14);
        r14 = r4.setUnderline(r7);
        goto L_0x001f;
    L_0x021c:
        r4 = r12.createIfNull(r14);
        r14 = r4.setUnderline(r5);
        goto L_0x001f;
    L_0x0226:
        return r14;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.parseStyleAttributes(org.xmlpull.v1.XmlPullParser, com.google.android.exoplayer2.text.ttml.TtmlStyle):com.google.android.exoplayer2.text.ttml.TtmlStyle");
    }

    private TtmlStyle createIfNull(TtmlStyle style) {
        return style == null ? new TtmlStyle() : style;
    }

    private TtmlNode parseNode(XmlPullParser parser, TtmlNode parent, Map<String, TtmlRegion> regionMap, FrameAndTickRate frameAndTickRate) throws SubtitleDecoderException {
        long duration = C.TIME_UNSET;
        long startTime = C.TIME_UNSET;
        long endTime = C.TIME_UNSET;
        String regionId = "";
        String[] styleIds = null;
        int attributeCount = parser.getAttributeCount();
        TtmlStyle style = parseStyleAttributes(parser, null);
        for (int i = 0; i < attributeCount; i++) {
            String attr = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);
            Object obj = -1;
            switch (attr.hashCode()) {
                case -934795532:
                    if (attr.equals("region")) {
                        obj = 4;
                        break;
                    }
                    break;
                case 99841:
                    if (attr.equals(ATTR_DURATION)) {
                        obj = 2;
                        break;
                    }
                    break;
                case 100571:
                    if (attr.equals("end")) {
                        obj = 1;
                        break;
                    }
                    break;
                case 93616297:
                    if (attr.equals(ATTR_BEGIN)) {
                        obj = null;
                        break;
                    }
                    break;
                case 109780401:
                    if (attr.equals("style")) {
                        obj = 3;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case null:
                    startTime = parseTimeExpression(value, frameAndTickRate);
                    break;
                case 1:
                    endTime = parseTimeExpression(value, frameAndTickRate);
                    break;
                case 2:
                    duration = parseTimeExpression(value, frameAndTickRate);
                    break;
                case 3:
                    String[] ids = parseStyleIds(value);
                    if (ids.length <= 0) {
                        break;
                    }
                    styleIds = ids;
                    break;
                case 4:
                    if (!regionMap.containsKey(value)) {
                        break;
                    }
                    regionId = value;
                    break;
                default:
                    break;
            }
        }
        if (!(parent == null || parent.startTimeUs == C.TIME_UNSET)) {
            if (startTime != C.TIME_UNSET) {
                startTime += parent.startTimeUs;
            }
            if (endTime != C.TIME_UNSET) {
                endTime += parent.startTimeUs;
            }
        }
        if (endTime == C.TIME_UNSET) {
            if (duration != C.TIME_UNSET) {
                endTime = startTime + duration;
            } else if (!(parent == null || parent.endTimeUs == C.TIME_UNSET)) {
                endTime = parent.endTimeUs;
            }
        }
        return TtmlNode.buildNode(parser.getName(), startTime, endTime, style, styleIds, regionId);
    }

    private static boolean isSupportedTag(String tag) {
        if (tag.equals(TtmlNode.TAG_TT) || tag.equals(TtmlNode.TAG_HEAD) || tag.equals(TtmlNode.TAG_BODY) || tag.equals(TtmlNode.TAG_DIV) || tag.equals(TtmlNode.TAG_P) || tag.equals(TtmlNode.TAG_SPAN) || tag.equals(TtmlNode.TAG_BR) || tag.equals("style") || tag.equals(TtmlNode.TAG_STYLING) || tag.equals(TtmlNode.TAG_LAYOUT) || tag.equals("region") || tag.equals(TtmlNode.TAG_METADATA) || tag.equals(TtmlNode.TAG_SMPTE_IMAGE) || tag.equals(TtmlNode.TAG_SMPTE_DATA) || tag.equals(TtmlNode.TAG_SMPTE_INFORMATION)) {
            return true;
        }
        return false;
    }

    private static void parseFontSize(String expression, TtmlStyle out) throws SubtitleDecoderException {
        Matcher matcher;
        String[] expressions = expression.split("\\s+");
        if (expressions.length == 1) {
            matcher = FONT_SIZE.matcher(expression);
        } else if (expressions.length == 2) {
            matcher = FONT_SIZE.matcher(expressions[1]);
            Log.w(TAG, "Multiple values in fontSize attribute. Picking the second value for vertical font size and ignoring the first.");
        } else {
            throw new SubtitleDecoderException("Invalid number of entries for fontSize: " + expressions.length + FileUtil.FILE_EXTENSION_SEPARATOR);
        }
        if (matcher.matches()) {
            String unit = matcher.group(3);
            int i = -1;
            switch (unit.hashCode()) {
                case 37:
                    if (unit.equals("%")) {
                        i = 2;
                        break;
                    }
                    break;
                case 3240:
                    if (unit.equals("em")) {
                        i = 1;
                        break;
                    }
                    break;
                case 3592:
                    if (unit.equals("px")) {
                        i = 0;
                        break;
                    }
                    break;
            }
            switch (i) {
                case 0:
                    out.setFontSizeUnit(1);
                    break;
                case 1:
                    out.setFontSizeUnit(2);
                    break;
                case 2:
                    out.setFontSizeUnit(3);
                    break;
                default:
                    throw new SubtitleDecoderException("Invalid unit for fontSize: '" + unit + "'.");
            }
            out.setFontSize(Float.valueOf(matcher.group(1)).floatValue());
            return;
        }
        throw new SubtitleDecoderException("Invalid expression for fontSize: '" + expression + "'.");
    }

    private static long parseTimeExpression(String time, FrameAndTickRate frameAndTickRate) throws SubtitleDecoderException {
        Matcher matcher = CLOCK_TIME.matcher(time);
        if (matcher.matches()) {
            double durationSeconds = (((double) (Long.parseLong(matcher.group(1)) * 3600)) + ((double) (Long.parseLong(matcher.group(2)) * 60))) + ((double) Long.parseLong(matcher.group(3)));
            String fraction = matcher.group(4);
            durationSeconds += fraction != null ? Double.parseDouble(fraction) : 0.0d;
            String frames = matcher.group(5);
            durationSeconds += frames != null ? (double) (((float) Long.parseLong(frames)) / frameAndTickRate.effectiveFrameRate) : 0.0d;
            String subframes = matcher.group(6);
            return (long) (1000000.0d * (durationSeconds + (subframes != null ? (((double) Long.parseLong(subframes)) / ((double) frameAndTickRate.subFrameRate)) / ((double) frameAndTickRate.effectiveFrameRate) : 0.0d)));
        }
        matcher = OFFSET_TIME.matcher(time);
        if (matcher.matches()) {
            double offsetSeconds = Double.parseDouble(matcher.group(1));
            String unit = matcher.group(2);
            Object obj = -1;
            switch (unit.hashCode()) {
                case 102:
                    if (unit.equals("f")) {
                        obj = 4;
                        break;
                    }
                    break;
                case 104:
                    if (unit.equals("h")) {
                        obj = null;
                        break;
                    }
                    break;
                case 109:
                    if (unit.equals("m")) {
                        obj = 1;
                        break;
                    }
                    break;
                case 115:
                    if (unit.equals("s")) {
                        obj = 2;
                        break;
                    }
                    break;
                case 116:
                    if (unit.equals("t")) {
                        obj = 5;
                        break;
                    }
                    break;
                case 3494:
                    if (unit.equals("ms")) {
                        obj = 3;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case null:
                    offsetSeconds *= 3600.0d;
                    break;
                case 1:
                    offsetSeconds *= 60.0d;
                    break;
                case 3:
                    offsetSeconds /= 1000.0d;
                    break;
                case 4:
                    offsetSeconds /= (double) frameAndTickRate.effectiveFrameRate;
                    break;
                case 5:
                    offsetSeconds /= (double) frameAndTickRate.tickRate;
                    break;
            }
            return (long) (1000000.0d * offsetSeconds);
        }
        throw new SubtitleDecoderException("Malformed time expression: " + time);
    }
}
