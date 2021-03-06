package com.google.android.exoplayer2.source.dash.manifest;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmInitData.SchemeData;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.source.dash.manifest.SegmentBase.SegmentList;
import com.google.android.exoplayer2.source.dash.manifest.SegmentBase.SegmentTemplate;
import com.google.android.exoplayer2.source.dash.manifest.SegmentBase.SegmentTimelineElement;
import com.google.android.exoplayer2.source.dash.manifest.SegmentBase.SingleSegmentBase;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.util.XmlPullParserUtil;
import com.xiaomi.mipush.sdk.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class DashManifestParser extends DefaultHandler implements Parser<DashManifest> {
    private static final Pattern FRAME_RATE_PATTERN = Pattern.compile("(\\d+)(?:/(\\d+))?");
    private static final String TAG = "MpdParser";
    private final String contentId;
    private final XmlPullParserFactory xmlParserFactory;

    private static final class RepresentationInfo {
        public final ArrayList<SchemeData> drmSchemeDatas;
        public final Format format;
        public final SegmentBase segmentBase;

        public RepresentationInfo(Format format, SegmentBase segmentBase, ArrayList<SchemeData> drmSchemeDatas) {
            this.format = format;
            this.segmentBase = segmentBase;
            this.drmSchemeDatas = drmSchemeDatas;
        }
    }

    public DashManifestParser() {
        this(null);
    }

    public DashManifestParser(String contentId) {
        this.contentId = contentId;
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    public DashManifest parse(Uri uri, InputStream inputStream) throws IOException {
        Throwable e;
        try {
            XmlPullParser xpp = this.xmlParserFactory.newPullParser();
            xpp.setInput(inputStream, null);
            if (xpp.next() == 2 && "MPD".equals(xpp.getName())) {
                return parseMediaPresentationDescription(xpp, uri.toString());
            }
            throw new ParserException("inputStream does not contain a valid media presentation description");
        } catch (XmlPullParserException e2) {
            e = e2;
            throw new ParserException(e);
        } catch (ParseException e3) {
            e = e3;
            throw new ParserException(e);
        }
    }

    protected DashManifest parseMediaPresentationDescription(XmlPullParser xpp, String baseUrl) throws XmlPullParserException, IOException, ParseException {
        boolean dynamic;
        long minUpdateTimeMs;
        long timeShiftBufferDepthMs;
        long suggestedPresentationDelayMs;
        UtcTimingElement utcTiming;
        Uri location;
        List<Period> periods;
        long nextPeriodStartMs;
        boolean seenEarlyAccessPeriod;
        boolean seenFirstBaseUrl;
        Pair<Period, Long> periodWithDurationMs;
        Period period;
        long periodDurationMs;
        long availabilityStartTime = parseDateTime(xpp, "availabilityStartTime", C.TIME_UNSET);
        long durationMs = parseDuration(xpp, "mediaPresentationDuration", C.TIME_UNSET);
        long minBufferTimeMs = parseDuration(xpp, "minBufferTime", C.TIME_UNSET);
        String typeString = xpp.getAttributeValue(null, MessageEncoder.ATTR_TYPE);
        if (typeString != null) {
            if (typeString.equals("dynamic")) {
                dynamic = true;
                minUpdateTimeMs = dynamic ? parseDuration(xpp, "minimumUpdatePeriod", C.TIME_UNSET) : C.TIME_UNSET;
                timeShiftBufferDepthMs = dynamic ? parseDuration(xpp, "timeShiftBufferDepth", C.TIME_UNSET) : C.TIME_UNSET;
                suggestedPresentationDelayMs = dynamic ? parseDuration(xpp, "suggestedPresentationDelay", C.TIME_UNSET) : C.TIME_UNSET;
                utcTiming = null;
                location = null;
                periods = new ArrayList();
                nextPeriodStartMs = dynamic ? C.TIME_UNSET : 0;
                seenEarlyAccessPeriod = false;
                seenFirstBaseUrl = false;
                do {
                    xpp.next();
                    if (XmlPullParserUtil.isStartTag(xpp, "BaseURL")) {
                        if (XmlPullParserUtil.isStartTag(xpp, "UTCTiming")) {
                            if (XmlPullParserUtil.isStartTag(xpp, "Location")) {
                                if (XmlPullParserUtil.isStartTag(xpp, "Period") && !seenEarlyAccessPeriod) {
                                    periodWithDurationMs = parsePeriod(xpp, baseUrl, nextPeriodStartMs);
                                    period = periodWithDurationMs.first;
                                    if (period.startMs == C.TIME_UNSET) {
                                        periodDurationMs = ((Long) periodWithDurationMs.second).longValue();
                                        if (periodDurationMs != C.TIME_UNSET) {
                                            nextPeriodStartMs = C.TIME_UNSET;
                                        } else {
                                            nextPeriodStartMs = period.startMs + periodDurationMs;
                                        }
                                        periods.add(period);
                                    } else if (dynamic) {
                                        throw new ParserException("Unable to determine start of period " + periods.size());
                                    } else {
                                        seenEarlyAccessPeriod = true;
                                    }
                                }
                            } else {
                                location = Uri.parse(xpp.nextText());
                            }
                        } else {
                            utcTiming = parseUtcTiming(xpp);
                        }
                    } else if (!seenFirstBaseUrl) {
                        baseUrl = parseBaseUrl(xpp, baseUrl);
                        seenFirstBaseUrl = true;
                    }
                } while (!XmlPullParserUtil.isEndTag(xpp, "MPD"));
                if (durationMs == C.TIME_UNSET) {
                    if (nextPeriodStartMs != C.TIME_UNSET) {
                        durationMs = nextPeriodStartMs;
                    } else if (!dynamic) {
                        throw new ParserException("Unable to determine duration of static manifest.");
                    }
                }
                if (periods.isEmpty()) {
                    return buildMediaPresentationDescription(availabilityStartTime, durationMs, minBufferTimeMs, dynamic, minUpdateTimeMs, timeShiftBufferDepthMs, suggestedPresentationDelayMs, utcTiming, location, periods);
                }
                throw new ParserException("No periods found.");
            }
        }
        dynamic = false;
        if (dynamic) {
        }
        if (dynamic) {
        }
        if (dynamic) {
        }
        utcTiming = null;
        location = null;
        periods = new ArrayList();
        if (dynamic) {
        }
        seenEarlyAccessPeriod = false;
        seenFirstBaseUrl = false;
        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "BaseURL")) {
                if (XmlPullParserUtil.isStartTag(xpp, "UTCTiming")) {
                    if (XmlPullParserUtil.isStartTag(xpp, "Location")) {
                        periodWithDurationMs = parsePeriod(xpp, baseUrl, nextPeriodStartMs);
                        period = periodWithDurationMs.first;
                        if (period.startMs == C.TIME_UNSET) {
                            periodDurationMs = ((Long) periodWithDurationMs.second).longValue();
                            if (periodDurationMs != C.TIME_UNSET) {
                                nextPeriodStartMs = period.startMs + periodDurationMs;
                            } else {
                                nextPeriodStartMs = C.TIME_UNSET;
                            }
                            periods.add(period);
                        } else if (dynamic) {
                            throw new ParserException("Unable to determine start of period " + periods.size());
                        } else {
                            seenEarlyAccessPeriod = true;
                        }
                    } else {
                        location = Uri.parse(xpp.nextText());
                    }
                } else {
                    utcTiming = parseUtcTiming(xpp);
                }
            } else if (seenFirstBaseUrl) {
                baseUrl = parseBaseUrl(xpp, baseUrl);
                seenFirstBaseUrl = true;
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "MPD"));
        if (durationMs == C.TIME_UNSET) {
            if (nextPeriodStartMs != C.TIME_UNSET) {
                durationMs = nextPeriodStartMs;
            } else if (dynamic) {
                throw new ParserException("Unable to determine duration of static manifest.");
            }
        }
        if (periods.isEmpty()) {
            return buildMediaPresentationDescription(availabilityStartTime, durationMs, minBufferTimeMs, dynamic, minUpdateTimeMs, timeShiftBufferDepthMs, suggestedPresentationDelayMs, utcTiming, location, periods);
        }
        throw new ParserException("No periods found.");
    }

    protected DashManifest buildMediaPresentationDescription(long availabilityStartTime, long durationMs, long minBufferTimeMs, boolean dynamic, long minUpdateTimeMs, long timeShiftBufferDepthMs, long suggestedPresentationDelayMs, UtcTimingElement utcTiming, Uri location, List<Period> periods) {
        return new DashManifest(availabilityStartTime, durationMs, minBufferTimeMs, dynamic, minUpdateTimeMs, timeShiftBufferDepthMs, suggestedPresentationDelayMs, utcTiming, location, periods);
    }

    protected UtcTimingElement parseUtcTiming(XmlPullParser xpp) {
        return buildUtcTimingElement(xpp.getAttributeValue(null, "schemeIdUri"), xpp.getAttributeValue(null, "value"));
    }

    protected UtcTimingElement buildUtcTimingElement(String schemeIdUri, String value) {
        return new UtcTimingElement(schemeIdUri, value);
    }

    protected Pair<Period, Long> parsePeriod(XmlPullParser xpp, String baseUrl, long defaultStartMs) throws XmlPullParserException, IOException {
        String id = xpp.getAttributeValue(null, "id");
        long startMs = parseDuration(xpp, TtmlNode.START, defaultStartMs);
        long durationMs = parseDuration(xpp, "duration", C.TIME_UNSET);
        SegmentBase segmentBase = null;
        List<AdaptationSet> adaptationSets = new ArrayList();
        boolean seenFirstBaseUrl = false;
        do {
            xpp.next();
            if (!XmlPullParserUtil.isStartTag(xpp, "BaseURL")) {
                if (XmlPullParserUtil.isStartTag(xpp, "AdaptationSet")) {
                    adaptationSets.add(parseAdaptationSet(xpp, baseUrl, segmentBase));
                } else {
                    if (XmlPullParserUtil.isStartTag(xpp, "SegmentBase")) {
                        segmentBase = parseSegmentBase(xpp, baseUrl, null);
                    } else {
                        if (XmlPullParserUtil.isStartTag(xpp, "SegmentList")) {
                            segmentBase = parseSegmentList(xpp, baseUrl, null);
                        } else {
                            if (XmlPullParserUtil.isStartTag(xpp, "SegmentTemplate")) {
                                segmentBase = parseSegmentTemplate(xpp, baseUrl, null);
                            }
                        }
                    }
                }
            } else if (!seenFirstBaseUrl) {
                baseUrl = parseBaseUrl(xpp, baseUrl);
                seenFirstBaseUrl = true;
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "Period"));
        return Pair.create(buildPeriod(id, startMs, adaptationSets), Long.valueOf(durationMs));
    }

    protected Period buildPeriod(String id, long startMs, List<AdaptationSet> adaptationSets) {
        return new Period(id, startMs, adaptationSets);
    }

    protected AdaptationSet parseAdaptationSet(XmlPullParser xpp, String baseUrl, SegmentBase segmentBase) throws XmlPullParserException, IOException {
        int id = parseInt(xpp, "id", -1);
        int contentType = parseContentType(xpp);
        String mimeType = xpp.getAttributeValue(null, "mimeType");
        String codecs = xpp.getAttributeValue(null, "codecs");
        int width = parseInt(xpp, "width", -1);
        int height = parseInt(xpp, "height", -1);
        float frameRate = parseFrameRate(xpp, -1.0f);
        int audioChannels = -1;
        int audioSamplingRate = parseInt(xpp, "audioSamplingRate", -1);
        String language = xpp.getAttributeValue(null, "lang");
        ArrayList<SchemeData> drmSchemeDatas = new ArrayList();
        List<RepresentationInfo> representationInfos = new ArrayList();
        boolean seenFirstBaseUrl = false;
        do {
            xpp.next();
            if (!XmlPullParserUtil.isStartTag(xpp, "BaseURL")) {
                if (XmlPullParserUtil.isStartTag(xpp, "ContentProtection")) {
                    SchemeData contentProtection = parseContentProtection(xpp);
                    if (contentProtection != null) {
                        drmSchemeDatas.add(contentProtection);
                    }
                } else {
                    if (XmlPullParserUtil.isStartTag(xpp, "ContentComponent")) {
                        language = checkLanguageConsistency(language, xpp.getAttributeValue(null, "lang"));
                        contentType = checkContentTypeConsistency(contentType, parseContentType(xpp));
                    } else {
                        if (XmlPullParserUtil.isStartTag(xpp, "Representation")) {
                            RepresentationInfo representationInfo = parseRepresentation(xpp, baseUrl, mimeType, codecs, width, height, frameRate, audioChannels, audioSamplingRate, language, segmentBase);
                            contentType = checkContentTypeConsistency(contentType, getContentType(representationInfo.format));
                            representationInfos.add(representationInfo);
                        } else {
                            if (XmlPullParserUtil.isStartTag(xpp, "AudioChannelConfiguration")) {
                                audioChannels = parseAudioChannelConfiguration(xpp);
                            } else {
                                if (XmlPullParserUtil.isStartTag(xpp, "SegmentBase")) {
                                    segmentBase = parseSegmentBase(xpp, baseUrl, (SingleSegmentBase) segmentBase);
                                } else {
                                    if (XmlPullParserUtil.isStartTag(xpp, "SegmentList")) {
                                        segmentBase = parseSegmentList(xpp, baseUrl, (SegmentList) segmentBase);
                                    } else {
                                        if (XmlPullParserUtil.isStartTag(xpp, "SegmentTemplate")) {
                                            segmentBase = parseSegmentTemplate(xpp, baseUrl, (SegmentTemplate) segmentBase);
                                        } else if (XmlPullParserUtil.isStartTag(xpp)) {
                                            parseAdaptationSetChild(xpp);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (!seenFirstBaseUrl) {
                baseUrl = parseBaseUrl(xpp, baseUrl);
                seenFirstBaseUrl = true;
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "AdaptationSet"));
        List<Representation> arrayList = new ArrayList(representationInfos.size());
        for (int i = 0; i < representationInfos.size(); i++) {
            arrayList.add(buildRepresentation((RepresentationInfo) representationInfos.get(i), this.contentId, drmSchemeDatas));
        }
        return buildAdaptationSet(id, contentType, arrayList);
    }

    protected AdaptationSet buildAdaptationSet(int id, int contentType, List<Representation> representations) {
        return new AdaptationSet(id, contentType, representations);
    }

    protected int parseContentType(XmlPullParser xpp) {
        String contentType = xpp.getAttributeValue(null, "contentType");
        if (TextUtils.isEmpty(contentType)) {
            return -1;
        }
        if (MimeTypes.BASE_TYPE_AUDIO.equals(contentType)) {
            return 1;
        }
        if (MimeTypes.BASE_TYPE_VIDEO.equals(contentType)) {
            return 2;
        }
        if (MimeTypes.BASE_TYPE_TEXT.equals(contentType)) {
            return 3;
        }
        return -1;
    }

    protected int getContentType(Format format) {
        String sampleMimeType = format.sampleMimeType;
        if (TextUtils.isEmpty(sampleMimeType)) {
            return -1;
        }
        if (MimeTypes.isVideo(sampleMimeType)) {
            return 2;
        }
        if (MimeTypes.isAudio(sampleMimeType)) {
            return 1;
        }
        if (mimeTypeIsRawText(sampleMimeType) || MimeTypes.APPLICATION_RAWCC.equals(format.containerMimeType)) {
            return 3;
        }
        return -1;
    }

    protected SchemeData parseContentProtection(XmlPullParser xpp) throws XmlPullParserException, IOException {
        byte[] data = null;
        UUID uuid = null;
        boolean seenPsshElement = false;
        boolean requiresSecureDecoder = false;
        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "cenc:pssh") && xpp.next() == 4) {
                seenPsshElement = true;
                data = Base64.decode(xpp.getText(), 0);
                uuid = PsshAtomUtil.parseUuid(data);
            } else if (XmlPullParserUtil.isStartTag(xpp, "widevine:license")) {
                String robustnessLevel = xpp.getAttributeValue(null, "robustness_level");
                requiresSecureDecoder = robustnessLevel != null && robustnessLevel.startsWith("HW");
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "ContentProtection"));
        if (!seenPsshElement) {
            return null;
        }
        if (uuid != null) {
            return new SchemeData(uuid, MimeTypes.VIDEO_MP4, data, requiresSecureDecoder);
        }
        Log.w(TAG, "Skipped unsupported ContentProtection element");
        return null;
    }

    protected void parseAdaptationSetChild(XmlPullParser xpp) throws XmlPullParserException, IOException {
    }

    protected RepresentationInfo parseRepresentation(XmlPullParser xpp, String baseUrl, String adaptationSetMimeType, String adaptationSetCodecs, int adaptationSetWidth, int adaptationSetHeight, float adaptationSetFrameRate, int adaptationSetAudioChannels, int adaptationSetAudioSamplingRate, String adaptationSetLanguage, SegmentBase segmentBase) throws XmlPullParserException, IOException {
        String id = xpp.getAttributeValue(null, "id");
        int bandwidth = parseInt(xpp, "bandwidth", -1);
        String mimeType = parseString(xpp, "mimeType", adaptationSetMimeType);
        String codecs = parseString(xpp, "codecs", adaptationSetCodecs);
        int width = parseInt(xpp, "width", adaptationSetWidth);
        int height = parseInt(xpp, "height", adaptationSetHeight);
        float frameRate = parseFrameRate(xpp, adaptationSetFrameRate);
        int audioChannels = adaptationSetAudioChannels;
        int audioSamplingRate = parseInt(xpp, "audioSamplingRate", adaptationSetAudioSamplingRate);
        ArrayList<SchemeData> drmSchemeDatas = new ArrayList();
        boolean seenFirstBaseUrl = false;
        do {
            xpp.next();
            if (!XmlPullParserUtil.isStartTag(xpp, "BaseURL")) {
                if (XmlPullParserUtil.isStartTag(xpp, "AudioChannelConfiguration")) {
                    audioChannels = parseAudioChannelConfiguration(xpp);
                } else {
                    if (XmlPullParserUtil.isStartTag(xpp, "SegmentBase")) {
                        segmentBase = parseSegmentBase(xpp, baseUrl, (SingleSegmentBase) segmentBase);
                    } else {
                        if (XmlPullParserUtil.isStartTag(xpp, "SegmentList")) {
                            segmentBase = parseSegmentList(xpp, baseUrl, (SegmentList) segmentBase);
                        } else {
                            if (XmlPullParserUtil.isStartTag(xpp, "SegmentTemplate")) {
                                segmentBase = parseSegmentTemplate(xpp, baseUrl, (SegmentTemplate) segmentBase);
                            } else {
                                if (XmlPullParserUtil.isStartTag(xpp, "ContentProtection")) {
                                    SchemeData contentProtection = parseContentProtection(xpp);
                                    if (contentProtection != null) {
                                        drmSchemeDatas.add(contentProtection);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (!seenFirstBaseUrl) {
                baseUrl = parseBaseUrl(xpp, baseUrl);
                seenFirstBaseUrl = true;
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "Representation"));
        Format format = buildFormat(id, mimeType, width, height, frameRate, audioChannels, audioSamplingRate, bandwidth, adaptationSetLanguage, codecs);
        if (segmentBase == null) {
            SegmentBase singleSegmentBase = new SingleSegmentBase(baseUrl);
        }
        return new RepresentationInfo(format, segmentBase, drmSchemeDatas);
    }

    protected Format buildFormat(String id, String containerMimeType, int width, int height, float frameRate, int audioChannels, int audioSamplingRate, int bitrate, String language, String codecs) {
        String sampleMimeType = getSampleMimeType(containerMimeType, codecs);
        if (sampleMimeType == null) {
            return Format.createContainerFormat(id, containerMimeType, codecs, sampleMimeType, bitrate);
        }
        if (MimeTypes.isVideo(sampleMimeType)) {
            return Format.createVideoContainerFormat(id, containerMimeType, sampleMimeType, codecs, bitrate, width, height, frameRate, null);
        }
        if (MimeTypes.isAudio(sampleMimeType)) {
            return Format.createAudioContainerFormat(id, containerMimeType, sampleMimeType, codecs, bitrate, audioChannels, audioSamplingRate, null, 0, language);
        }
        if (mimeTypeIsRawText(sampleMimeType)) {
            return Format.createTextContainerFormat(id, containerMimeType, sampleMimeType, codecs, bitrate, 0, language);
        }
        return Format.createContainerFormat(id, containerMimeType, codecs, sampleMimeType, bitrate);
    }

    protected Representation buildRepresentation(RepresentationInfo representationInfo, String contentId, ArrayList<SchemeData> extraDrmSchemeDatas) {
        Format format = representationInfo.format;
        List drmSchemeDatas = representationInfo.drmSchemeDatas;
        drmSchemeDatas.addAll(extraDrmSchemeDatas);
        if (!drmSchemeDatas.isEmpty()) {
            format = format.copyWithDrmInitData(new DrmInitData(drmSchemeDatas));
        }
        return Representation.newInstance(contentId, -1, format, representationInfo.segmentBase);
    }

    protected SingleSegmentBase parseSegmentBase(XmlPullParser xpp, String baseUrl, SingleSegmentBase parent) throws XmlPullParserException, IOException {
        long timescale = parseLong(xpp, "timescale", parent != null ? parent.timescale : 1);
        long presentationTimeOffset = parseLong(xpp, "presentationTimeOffset", parent != null ? parent.presentationTimeOffset : 0);
        long indexStart = parent != null ? parent.indexStart : 0;
        long indexLength = parent != null ? parent.indexLength : 0;
        String indexRangeText = xpp.getAttributeValue(null, "indexRange");
        if (indexRangeText != null) {
            String[] indexRange = indexRangeText.split(Constants.ACCEPT_TIME_SEPARATOR_SERVER);
            indexStart = Long.parseLong(indexRange[0]);
            indexLength = (Long.parseLong(indexRange[1]) - indexStart) + 1;
        }
        RangedUri initialization = parent != null ? parent.initialization : null;
        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "Initialization")) {
                initialization = parseInitialization(xpp, baseUrl);
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "SegmentBase"));
        return buildSingleSegmentBase(initialization, timescale, presentationTimeOffset, baseUrl, indexStart, indexLength);
    }

    protected SingleSegmentBase buildSingleSegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset, String baseUrl, long indexStart, long indexLength) {
        return new SingleSegmentBase(initialization, timescale, presentationTimeOffset, baseUrl, indexStart, indexLength);
    }

    protected SegmentList parseSegmentList(XmlPullParser xpp, String baseUrl, SegmentList parent) throws XmlPullParserException, IOException {
        long timescale = parseLong(xpp, "timescale", parent != null ? parent.timescale : 1);
        long presentationTimeOffset = parseLong(xpp, "presentationTimeOffset", parent != null ? parent.presentationTimeOffset : 0);
        long duration = parseLong(xpp, "duration", parent != null ? parent.duration : C.TIME_UNSET);
        int startNumber = parseInt(xpp, "startNumber", parent != null ? parent.startNumber : 1);
        RangedUri initialization = null;
        List<SegmentTimelineElement> timeline = null;
        List<RangedUri> segments = null;
        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "Initialization")) {
                initialization = parseInitialization(xpp, baseUrl);
            } else {
                if (XmlPullParserUtil.isStartTag(xpp, "SegmentTimeline")) {
                    timeline = parseSegmentTimeline(xpp);
                } else {
                    if (XmlPullParserUtil.isStartTag(xpp, "SegmentURL")) {
                        if (segments == null) {
                            segments = new ArrayList();
                        }
                        segments.add(parseSegmentUrl(xpp, baseUrl));
                    }
                }
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "SegmentList"));
        if (parent != null) {
            if (initialization == null) {
                initialization = parent.initialization;
            }
            if (timeline == null) {
                timeline = parent.segmentTimeline;
            }
            if (segments == null) {
                segments = parent.mediaSegments;
            }
        }
        return buildSegmentList(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, segments);
    }

    protected SegmentList buildSegmentList(RangedUri initialization, long timescale, long presentationTimeOffset, int startNumber, long duration, List<SegmentTimelineElement> timeline, List<RangedUri> segments) {
        return new SegmentList(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, segments);
    }

    protected SegmentTemplate parseSegmentTemplate(XmlPullParser xpp, String baseUrl, SegmentTemplate parent) throws XmlPullParserException, IOException {
        long timescale = parseLong(xpp, "timescale", parent != null ? parent.timescale : 1);
        long presentationTimeOffset = parseLong(xpp, "presentationTimeOffset", parent != null ? parent.presentationTimeOffset : 0);
        long duration = parseLong(xpp, "duration", parent != null ? parent.duration : C.TIME_UNSET);
        int startNumber = parseInt(xpp, "startNumber", parent != null ? parent.startNumber : 1);
        UrlTemplate mediaTemplate = parseUrlTemplate(xpp, "media", parent != null ? parent.mediaTemplate : null);
        UrlTemplate initializationTemplate = parseUrlTemplate(xpp, "initialization", parent != null ? parent.initializationTemplate : null);
        RangedUri initialization = null;
        List<SegmentTimelineElement> timeline = null;
        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "Initialization")) {
                initialization = parseInitialization(xpp, baseUrl);
            } else {
                if (XmlPullParserUtil.isStartTag(xpp, "SegmentTimeline")) {
                    timeline = parseSegmentTimeline(xpp);
                }
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "SegmentTemplate"));
        if (parent != null) {
            if (initialization == null) {
                initialization = parent.initialization;
            }
            if (timeline == null) {
                timeline = parent.segmentTimeline;
            }
        }
        return buildSegmentTemplate(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, initializationTemplate, mediaTemplate, baseUrl);
    }

    protected SegmentTemplate buildSegmentTemplate(RangedUri initialization, long timescale, long presentationTimeOffset, int startNumber, long duration, List<SegmentTimelineElement> timeline, UrlTemplate initializationTemplate, UrlTemplate mediaTemplate, String baseUrl) {
        return new SegmentTemplate(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, initializationTemplate, mediaTemplate, baseUrl);
    }

    protected List<SegmentTimelineElement> parseSegmentTimeline(XmlPullParser xpp) throws XmlPullParserException, IOException {
        List<SegmentTimelineElement> segmentTimeline = new ArrayList();
        long elapsedTime = 0;
        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "S")) {
                elapsedTime = parseLong(xpp, "t", elapsedTime);
                long duration = parseLong(xpp, "d", C.TIME_UNSET);
                int count = parseInt(xpp, "r", 0) + 1;
                for (int i = 0; i < count; i++) {
                    segmentTimeline.add(buildSegmentTimelineElement(elapsedTime, duration));
                    elapsedTime += duration;
                }
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "SegmentTimeline"));
        return segmentTimeline;
    }

    protected SegmentTimelineElement buildSegmentTimelineElement(long elapsedTime, long duration) {
        return new SegmentTimelineElement(elapsedTime, duration);
    }

    protected UrlTemplate parseUrlTemplate(XmlPullParser xpp, String name, UrlTemplate defaultValue) {
        String valueString = xpp.getAttributeValue(null, name);
        if (valueString != null) {
            return UrlTemplate.compile(valueString);
        }
        return defaultValue;
    }

    protected RangedUri parseInitialization(XmlPullParser xpp, String baseUrl) {
        return parseRangedUrl(xpp, baseUrl, "sourceURL", "range");
    }

    protected RangedUri parseSegmentUrl(XmlPullParser xpp, String baseUrl) {
        return parseRangedUrl(xpp, baseUrl, "media", "mediaRange");
    }

    protected RangedUri parseRangedUrl(XmlPullParser xpp, String baseUrl, String urlAttribute, String rangeAttribute) {
        String urlText = xpp.getAttributeValue(null, urlAttribute);
        long rangeStart = 0;
        long rangeLength = -1;
        String rangeText = xpp.getAttributeValue(null, rangeAttribute);
        if (rangeText != null) {
            String[] rangeTextArray = rangeText.split(Constants.ACCEPT_TIME_SEPARATOR_SERVER);
            rangeStart = Long.parseLong(rangeTextArray[0]);
            if (rangeTextArray.length == 2) {
                rangeLength = (Long.parseLong(rangeTextArray[1]) - rangeStart) + 1;
            }
        }
        return buildRangedUri(baseUrl, urlText, rangeStart, rangeLength);
    }

    protected RangedUri buildRangedUri(String baseUrl, String urlText, long rangeStart, long rangeLength) {
        return new RangedUri(baseUrl, urlText, rangeStart, rangeLength);
    }

    protected int parseAudioChannelConfiguration(XmlPullParser xpp) throws XmlPullParserException, IOException {
        int audioChannels = -1;
        if ("urn:mpeg:dash:23003:3:audio_channel_configuration:2011".equals(parseString(xpp, "schemeIdUri", null))) {
            audioChannels = parseInt(xpp, "value", -1);
        }
        do {
            xpp.next();
        } while (!XmlPullParserUtil.isEndTag(xpp, "AudioChannelConfiguration"));
        return audioChannels;
    }

    private static String getSampleMimeType(String containerMimeType, String codecs) {
        if (MimeTypes.isAudio(containerMimeType)) {
            return MimeTypes.getAudioMediaMimeType(codecs);
        }
        if (MimeTypes.isVideo(containerMimeType)) {
            return MimeTypes.getVideoMediaMimeType(codecs);
        }
        if (MimeTypes.APPLICATION_RAWCC.equals(containerMimeType)) {
            if (codecs != null) {
                if (codecs.contains("cea708")) {
                    return MimeTypes.APPLICATION_CEA708;
                }
                if (codecs.contains("eia608") || codecs.contains("cea608")) {
                    return MimeTypes.APPLICATION_CEA608;
                }
            }
            return null;
        } else if (mimeTypeIsRawText(containerMimeType)) {
            return containerMimeType;
        } else {
            if (MimeTypes.APPLICATION_MP4.equals(containerMimeType)) {
                if ("stpp".equals(codecs)) {
                    return MimeTypes.APPLICATION_TTML;
                }
                if ("wvtt".equals(codecs)) {
                    return MimeTypes.APPLICATION_MP4VTT;
                }
            }
            return null;
        }
    }

    private static boolean mimeTypeIsRawText(String mimeType) {
        return MimeTypes.isText(mimeType) || MimeTypes.APPLICATION_TTML.equals(mimeType);
    }

    private static String checkLanguageConsistency(String firstLanguage, String secondLanguage) {
        if (firstLanguage == null) {
            return secondLanguage;
        }
        if (secondLanguage == null) {
            return firstLanguage;
        }
        Assertions.checkState(firstLanguage.equals(secondLanguage));
        return firstLanguage;
    }

    private static int checkContentTypeConsistency(int firstType, int secondType) {
        if (firstType == -1) {
            return secondType;
        }
        if (secondType == -1) {
            return firstType;
        }
        Assertions.checkState(firstType == secondType);
        return firstType;
    }

    protected static float parseFrameRate(XmlPullParser xpp, float defaultValue) {
        float frameRate = defaultValue;
        String frameRateAttribute = xpp.getAttributeValue(null, "frameRate");
        if (frameRateAttribute == null) {
            return frameRate;
        }
        Matcher frameRateMatcher = FRAME_RATE_PATTERN.matcher(frameRateAttribute);
        if (!frameRateMatcher.matches()) {
            return frameRate;
        }
        int numerator = Integer.parseInt(frameRateMatcher.group(1));
        String denominatorString = frameRateMatcher.group(2);
        if (TextUtils.isEmpty(denominatorString)) {
            return (float) numerator;
        }
        return ((float) numerator) / ((float) Integer.parseInt(denominatorString));
    }

    protected static long parseDuration(XmlPullParser xpp, String name, long defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : Util.parseXsDuration(value);
    }

    protected static long parseDateTime(XmlPullParser xpp, String name, long defaultValue) throws ParseException {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : Util.parseXsDateTime(value);
    }

    protected static String parseBaseUrl(XmlPullParser xpp, String parentBaseUrl) throws XmlPullParserException, IOException {
        xpp.next();
        return UriUtil.resolve(parentBaseUrl, xpp.getText());
    }

    protected static int parseInt(XmlPullParser xpp, String name, int defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    protected static long parseLong(XmlPullParser xpp, String name, long defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    protected static String parseString(XmlPullParser xpp, String name, String defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : value;
    }
}
