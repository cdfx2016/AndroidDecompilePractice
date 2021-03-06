package com.google.android.exoplayer2.text;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import java.util.Collections;
import java.util.List;

public final class TextRenderer extends BaseRenderer implements Callback {
    private static final int MSG_UPDATE_OUTPUT = 0;
    private SubtitleDecoder decoder;
    private final SubtitleDecoderFactory decoderFactory;
    private final FormatHolder formatHolder;
    private boolean inputStreamEnded;
    private SubtitleInputBuffer nextInputBuffer;
    private SubtitleOutputBuffer nextSubtitle;
    private int nextSubtitleEventIndex;
    private final Output output;
    private final Handler outputHandler;
    private boolean outputStreamEnded;
    private SubtitleOutputBuffer subtitle;

    public interface Output {
        void onCues(List<Cue> list);
    }

    public TextRenderer(Output output, Looper outputLooper) {
        this(output, outputLooper, SubtitleDecoderFactory.DEFAULT);
    }

    public TextRenderer(Output output, Looper outputLooper, SubtitleDecoderFactory decoderFactory) {
        super(3);
        this.output = (Output) Assertions.checkNotNull(output);
        this.outputHandler = outputLooper == null ? null : new Handler(outputLooper, this);
        this.decoderFactory = decoderFactory;
        this.formatHolder = new FormatHolder();
    }

    public int supportsFormat(Format format) {
        if (this.decoderFactory.supportsFormat(format)) {
            return 3;
        }
        return MimeTypes.isText(format.sampleMimeType) ? 1 : 0;
    }

    protected void onStreamChanged(Format[] formats) throws ExoPlaybackException {
        if (this.decoder != null) {
            this.decoder.release();
            this.nextInputBuffer = null;
        }
        this.decoder = this.decoderFactory.createDecoder(formats[0]);
    }

    protected void onPositionReset(long positionUs, boolean joining) {
        this.inputStreamEnded = false;
        this.outputStreamEnded = false;
        if (this.subtitle != null) {
            this.subtitle.release();
            this.subtitle = null;
        }
        if (this.nextSubtitle != null) {
            this.nextSubtitle.release();
            this.nextSubtitle = null;
        }
        this.nextInputBuffer = null;
        clearOutput();
        this.decoder.flush();
    }

    public void render(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        if (!this.outputStreamEnded) {
            if (this.nextSubtitle == null) {
                this.decoder.setPositionUs(positionUs);
                try {
                    this.nextSubtitle = (SubtitleOutputBuffer) this.decoder.dequeueOutputBuffer();
                } catch (SubtitleDecoderException e) {
                    throw ExoPlaybackException.createForRenderer(e, getIndex());
                }
            }
            if (getState() == 2) {
                boolean textRendererNeedsUpdate = false;
                if (this.subtitle != null) {
                    long subtitleNextEventTimeUs = getNextEventTime();
                    while (subtitleNextEventTimeUs <= positionUs) {
                        this.nextSubtitleEventIndex++;
                        subtitleNextEventTimeUs = getNextEventTime();
                        textRendererNeedsUpdate = true;
                    }
                }
                if (this.nextSubtitle != null) {
                    if (this.nextSubtitle.isEndOfStream()) {
                        if (!textRendererNeedsUpdate && getNextEventTime() == Long.MAX_VALUE) {
                            if (this.subtitle != null) {
                                this.subtitle.release();
                                this.subtitle = null;
                            }
                            this.nextSubtitle.release();
                            this.nextSubtitle = null;
                            this.outputStreamEnded = true;
                        }
                    } else if (this.nextSubtitle.timeUs <= positionUs) {
                        if (this.subtitle != null) {
                            this.subtitle.release();
                        }
                        this.subtitle = this.nextSubtitle;
                        this.nextSubtitle = null;
                        this.nextSubtitleEventIndex = this.subtitle.getNextEventTimeIndex(positionUs);
                        textRendererNeedsUpdate = true;
                    }
                }
                if (textRendererNeedsUpdate) {
                    updateOutput(this.subtitle.getCues(positionUs));
                }
                while (!this.inputStreamEnded) {
                    if (this.nextInputBuffer == null) {
                        this.nextInputBuffer = (SubtitleInputBuffer) this.decoder.dequeueInputBuffer();
                        if (this.nextInputBuffer == null) {
                            return;
                        }
                    }
                    int result = readSource(this.formatHolder, this.nextInputBuffer);
                    if (result == -4) {
                        this.nextInputBuffer.clearFlag(Integer.MIN_VALUE);
                        if (this.nextInputBuffer.isEndOfStream()) {
                            this.inputStreamEnded = true;
                        } else {
                            try {
                                this.nextInputBuffer.subsampleOffsetUs = this.formatHolder.format.subsampleOffsetUs;
                                this.nextInputBuffer.flip();
                            } catch (SubtitleDecoderException e2) {
                                throw ExoPlaybackException.createForRenderer(e2, getIndex());
                            }
                        }
                        this.decoder.queueInputBuffer(this.nextInputBuffer);
                        this.nextInputBuffer = null;
                    } else if (result == -3) {
                        return;
                    }
                }
            }
        }
    }

    protected void onDisabled() {
        if (this.subtitle != null) {
            this.subtitle.release();
            this.subtitle = null;
        }
        if (this.nextSubtitle != null) {
            this.nextSubtitle.release();
            this.nextSubtitle = null;
        }
        this.decoder.release();
        this.decoder = null;
        this.nextInputBuffer = null;
        clearOutput();
        super.onDisabled();
    }

    public boolean isEnded() {
        return this.outputStreamEnded;
    }

    public boolean isReady() {
        return true;
    }

    private long getNextEventTime() {
        if (this.nextSubtitleEventIndex == -1 || this.nextSubtitleEventIndex >= this.subtitle.getEventTimeCount()) {
            return Long.MAX_VALUE;
        }
        return this.subtitle.getEventTime(this.nextSubtitleEventIndex);
    }

    private void updateOutput(List<Cue> cues) {
        if (this.outputHandler != null) {
            this.outputHandler.obtainMessage(0, cues).sendToTarget();
        } else {
            invokeUpdateOutputInternal(cues);
        }
    }

    private void clearOutput() {
        updateOutput(Collections.emptyList());
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                invokeUpdateOutputInternal((List) msg.obj);
                return true;
            default:
                return false;
        }
    }

    private void invokeUpdateOutputInternal(List<Cue> cues) {
        this.output.onCues(cues);
    }
}
