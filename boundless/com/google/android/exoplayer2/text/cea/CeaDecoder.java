package com.google.android.exoplayer2.text.cea;

import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleDecoder;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.text.SubtitleInputBuffer;
import com.google.android.exoplayer2.text.SubtitleOutputBuffer;
import com.google.android.exoplayer2.util.Assertions;
import java.util.LinkedList;
import java.util.TreeSet;

abstract class CeaDecoder implements SubtitleDecoder {
    private static final int NUM_INPUT_BUFFERS = 10;
    private static final int NUM_OUTPUT_BUFFERS = 2;
    private final LinkedList<SubtitleInputBuffer> availableInputBuffers = new LinkedList();
    private final LinkedList<SubtitleOutputBuffer> availableOutputBuffers;
    private SubtitleInputBuffer dequeuedInputBuffer;
    private long playbackPositionUs;
    private final TreeSet<SubtitleInputBuffer> queuedInputBuffers;

    protected abstract Subtitle createSubtitle();

    protected abstract void decode(SubtitleInputBuffer subtitleInputBuffer);

    public abstract String getName();

    protected abstract boolean isNewSubtitleDataAvailable();

    public CeaDecoder() {
        int i;
        for (i = 0; i < 10; i++) {
            this.availableInputBuffers.add(new SubtitleInputBuffer());
        }
        this.availableOutputBuffers = new LinkedList();
        for (i = 0; i < 2; i++) {
            this.availableOutputBuffers.add(new CeaOutputBuffer(this));
        }
        this.queuedInputBuffers = new TreeSet();
    }

    public void setPositionUs(long positionUs) {
        this.playbackPositionUs = positionUs;
    }

    public SubtitleInputBuffer dequeueInputBuffer() throws SubtitleDecoderException {
        Assertions.checkState(this.dequeuedInputBuffer == null);
        if (this.availableInputBuffers.isEmpty()) {
            return null;
        }
        this.dequeuedInputBuffer = (SubtitleInputBuffer) this.availableInputBuffers.pollFirst();
        return this.dequeuedInputBuffer;
    }

    public void queueInputBuffer(SubtitleInputBuffer inputBuffer) throws SubtitleDecoderException {
        boolean z;
        boolean z2 = true;
        if (inputBuffer != null) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkArgument(z);
        if (inputBuffer != this.dequeuedInputBuffer) {
            z2 = false;
        }
        Assertions.checkArgument(z2);
        this.queuedInputBuffers.add(inputBuffer);
        this.dequeuedInputBuffer = null;
    }

    public SubtitleOutputBuffer dequeueOutputBuffer() throws SubtitleDecoderException {
        if (this.availableOutputBuffers.isEmpty()) {
            return null;
        }
        while (!this.queuedInputBuffers.isEmpty() && ((SubtitleInputBuffer) this.queuedInputBuffers.first()).timeUs <= this.playbackPositionUs) {
            SubtitleInputBuffer inputBuffer = (SubtitleInputBuffer) this.queuedInputBuffers.pollFirst();
            if (inputBuffer.isEndOfStream()) {
                SubtitleOutputBuffer outputBuffer = (SubtitleOutputBuffer) this.availableOutputBuffers.pollFirst();
                outputBuffer.addFlag(4);
                releaseInputBuffer(inputBuffer);
                return outputBuffer;
            }
            decode(inputBuffer);
            if (isNewSubtitleDataAvailable()) {
                Subtitle subtitle = createSubtitle();
                if (!inputBuffer.isDecodeOnly()) {
                    outputBuffer = (SubtitleOutputBuffer) this.availableOutputBuffers.pollFirst();
                    outputBuffer.setContent(inputBuffer.timeUs, subtitle, 0);
                    releaseInputBuffer(inputBuffer);
                    return outputBuffer;
                }
            }
            releaseInputBuffer(inputBuffer);
        }
        return null;
    }

    private void releaseInputBuffer(SubtitleInputBuffer inputBuffer) {
        inputBuffer.clear();
        this.availableInputBuffers.add(inputBuffer);
    }

    protected void releaseOutputBuffer(SubtitleOutputBuffer outputBuffer) {
        outputBuffer.clear();
        this.availableOutputBuffers.add(outputBuffer);
    }

    public void flush() {
        this.playbackPositionUs = 0;
        while (!this.queuedInputBuffers.isEmpty()) {
            releaseInputBuffer((SubtitleInputBuffer) this.queuedInputBuffers.pollFirst());
        }
        if (this.dequeuedInputBuffer != null) {
            releaseInputBuffer(this.dequeuedInputBuffer);
            this.dequeuedInputBuffer = null;
        }
    }

    public void release() {
    }
}
