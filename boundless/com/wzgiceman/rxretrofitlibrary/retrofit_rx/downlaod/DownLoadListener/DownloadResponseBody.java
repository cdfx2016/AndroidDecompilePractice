package com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DownLoadListener;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class DownloadResponseBody extends ResponseBody {
    private BufferedSource bufferedSource;
    private DownloadProgressListener progressListener;
    private ResponseBody responseBody;

    public DownloadResponseBody(ResponseBody responseBody, DownloadProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    public MediaType contentType() {
        return this.responseBody.contentType();
    }

    public long contentLength() {
        return this.responseBody.contentLength();
    }

    public BufferedSource source() {
        if (this.bufferedSource == null) {
            this.bufferedSource = Okio.buffer(source(this.responseBody.source()));
        }
        return this.bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0;

            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                this.totalBytesRead = (bytesRead != -1 ? bytesRead : 0) + this.totalBytesRead;
                if (DownloadResponseBody.this.progressListener != null) {
                    DownloadResponseBody.this.progressListener.update(this.totalBytesRead, DownloadResponseBody.this.responseBody.contentLength(), bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }
}
