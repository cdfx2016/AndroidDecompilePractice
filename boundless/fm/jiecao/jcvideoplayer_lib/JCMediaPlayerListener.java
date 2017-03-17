package fm.jiecao.jcvideoplayer_lib;

public interface JCMediaPlayerListener {
    void autoFullscreen(float f);

    void autoQuitFullscreen();

    boolean downStairs();

    int getScreenType();

    int getState();

    String getUrl();

    void goBackOnThisFloor();

    void onAutoCompletion();

    void onBufferingUpdate(int i);

    void onCompletion();

    void onError(int i, int i2);

    void onInfo(int i, int i2);

    void onPrepared();

    void onScrollChange();

    void onSeekComplete();

    void onVideoSizeChanged();
}
