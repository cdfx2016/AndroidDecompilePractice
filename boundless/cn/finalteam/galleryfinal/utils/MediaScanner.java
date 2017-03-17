package cn.finalteam.galleryfinal.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

public class MediaScanner {
    private MusicSannerClient client = null;
    private String filePath = null;
    private String[] filePaths = null;
    private String fileType = null;
    private MediaScannerConnection mediaScanConn = null;

    class MusicSannerClient implements MediaScannerConnectionClient {
        MusicSannerClient() {
        }

        public void onMediaScannerConnected() {
            if (MediaScanner.this.filePath != null) {
                MediaScanner.this.mediaScanConn.scanFile(MediaScanner.this.filePath, MediaScanner.this.fileType);
            }
            if (MediaScanner.this.filePaths != null) {
                for (String file : MediaScanner.this.filePaths) {
                    MediaScanner.this.mediaScanConn.scanFile(file, MediaScanner.this.fileType);
                }
            }
            MediaScanner.this.filePath = null;
            MediaScanner.this.fileType = null;
            MediaScanner.this.filePaths = null;
        }

        public void onScanCompleted(String path, Uri uri) {
            MediaScanner.this.mediaScanConn.disconnect();
        }
    }

    public MediaScanner(Context context) {
        if (this.client == null) {
            this.client = new MusicSannerClient();
        }
        if (this.mediaScanConn == null) {
            this.mediaScanConn = new MediaScannerConnection(context, this.client);
        }
    }

    public void scanFile(String filePath, String fileType) {
        this.filePath = filePath;
        this.fileType = fileType;
        this.mediaScanConn.connect();
    }

    public void scanFile(String[] filePaths, String fileType) {
        this.filePaths = filePaths;
        this.fileType = fileType;
        this.mediaScanConn.connect();
    }

    public void unScanFile() {
        this.mediaScanConn.disconnect();
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
