package com.easemob.util;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Time;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class VoiceRecorder {
    static final String EXTENSION = ".amr";
    static final String PREFIX = "voice";
    private File file;
    private Handler handler;
    private boolean isRecording = false;
    MediaRecorder recorder;
    private long startTime;
    private String voiceFileName = null;
    private String voiceFilePath = null;

    public VoiceRecorder(Handler handler) {
        this.handler = handler;
    }

    public void discardRecording() {
        if (this.recorder != null) {
            this.recorder.stop();
            this.recorder.release();
            this.recorder = null;
            if (!(this.file == null || !this.file.exists() || this.file.isDirectory())) {
                this.file.delete();
            }
            this.isRecording = false;
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if (this.recorder != null) {
            this.recorder.release();
        }
    }

    public String getVoiceFileName(String str) {
        Time time = new Time();
        time.setToNow();
        return new StringBuilder(String.valueOf(str)).append(time.toString().substring(0, 15)).append(EXTENSION).toString();
    }

    public String getVoiceFilePath() {
        return PathUtil.getInstance().getVoicePath() + "/" + this.voiceFileName;
    }

    public boolean isRecording() {
        return this.isRecording;
    }

    public String startRecording(String str, String str2, Context context) {
        this.file = null;
        try {
            this.recorder = new MediaRecorder();
            this.recorder.setAudioSource(1);
            this.recorder.setOutputFormat(3);
            this.recorder.setAudioEncoder(1);
            this.recorder.setAudioChannels(1);
            this.recorder.setAudioSamplingRate(8000);
            this.recorder.setAudioEncodingBitRate(64);
            this.voiceFileName = getVoiceFileName(str2);
            this.voiceFilePath = getVoiceFilePath();
            this.file = new File(this.voiceFilePath);
            this.recorder.setOutputFile(this.file.getAbsolutePath());
            this.recorder.prepare();
            this.isRecording = true;
            this.recorder.start();
        } catch (IOException e) {
            EMLog.e(PREFIX, "prepare() failed");
        }
        new Thread(new Runnable() {
            public void run() {
                while (VoiceRecorder.this.isRecording) {
                    try {
                        Message message = new Message();
                        message.what = (VoiceRecorder.this.recorder.getMaxAmplitude() * 13) / 32767;
                        VoiceRecorder.this.handler.sendMessage(message);
                        SystemClock.sleep(100);
                    } catch (Exception e) {
                        EMLog.e(VoiceRecorder.PREFIX, e.toString());
                        return;
                    }
                }
            }
        }).start();
        this.startTime = new Date().getTime();
        EMLog.d(PREFIX, "start voice recording to file:" + this.file.getAbsolutePath());
        return this.file == null ? null : this.file.getAbsolutePath();
    }

    public int stopRecoding() {
        if (this.recorder == null) {
            return 0;
        }
        this.isRecording = false;
        this.recorder.stop();
        this.recorder.release();
        this.recorder = null;
        int time = ((int) (new Date().getTime() - this.startTime)) / 1000;
        EMLog.d(PREFIX, "voice recording finished. seconds:" + time + " file length:" + new File(this.voiceFilePath).length());
        return time;
    }
}
