package com.wzgiceman.rxretrofitlibrary.retrofit_rx.listener.upload;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ProgressRequestBody extends RequestBody {
    private BufferedSink bufferedSink;
    private final UploadProgressListener progressListener;
    private final RequestBody requestBody;

    public ProgressRequestBody(RequestBody requestBody, UploadProgressListener progressListener) {
        this.requestBody = requestBody;
        this.progressListener = progressListener;
    }

    public MediaType contentType() {
        return this.requestBody.contentType();
    }

    public long contentLength() throws IOException {
        return this.requestBody.contentLength();
    }

    public void writeTo(BufferedSink sink) throws IOException {
        if (this.bufferedSink == null) {
            this.bufferedSink = Okio.buffer(sink(sink));
        }
        this.requestBody.writeTo(this.bufferedSink);
        this.bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long totalBytesCount = 0;
            long writtenBytesCount = 0;

            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                this.writtenBytesCount += byteCount;
                if (this.totalBytesCount == 0) {
                    this.totalBytesCount = ProgressRequestBody.this.contentLength();
                }
                Observable.just(Long.valueOf(this.writtenBytesCount)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
                    public void call(Long aLong) {
                        ProgressRequestBody.this.progressListener.onProgress(AnonymousClass1.this.writtenBytesCount, AnonymousClass1.this.totalBytesCount);
                    }
                });
            }
        };
    }
}
